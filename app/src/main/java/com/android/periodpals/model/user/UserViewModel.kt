package com.android.periodpals.model.user

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel for managing user data.
 *
 * @property userRepository The repository used for loading and saving user profiles.
 */
private const val TAG = "UserViewModel"

class UserViewModel(private val userRepository: UserRepositorySupabase) : ViewModel() {

  private val _user = mutableStateOf<User?>(null)
  val user: State<User?> = _user

  /**
   * Loads the user profile and updates the user state.
   *
   * @param onSuccess Callback function to be called when the user profile is successfully loaded.
   * @param onFailure Callback function to be called when there is an error loading the user
   *   profile.
   */
  fun loadUser(
      onSuccess: () -> Unit = { Log.d(TAG, "loadUser success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "loadUser failure callback: $e")
      }
  ) {
    viewModelScope.launch {
      userRepository.loadUserProfile(
          onSuccess = { userDto ->
            Log.d(TAG, "loadUserProfile: Successful")
            _user.value = userDto.asUser()
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "loadUserProfile: fail to load user profile: ${e.message}")
            _user.value = null
            onFailure(e)
          },
      )
    }
  }

  /**
   * Saves the user profile.
   *
   * @param user The user profile to be saved.
   * @param onSuccess Callback function to be called when the user profile is successfully saved.
   * @param onFailure Callback function to be called when there is an error saving the user profile.
   */
  fun saveUser(
      user: User,
      onSuccess: () -> Unit = { Log.d(TAG, "saveUser success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "saveUser failure callback: $e")
      }
  ) {
    viewModelScope.launch {
      userRepository.upsertUserProfile(
          user.asUserDto(),
          onSuccess = {
            Log.d(TAG, "saveUser: Success")
            _user.value = it.asUser()
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "saveUser: fail to save user: ${e.message}")
            _user.value = null
            onFailure(e)
          })
    }
  }

  fun deleteAccount(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    viewModelScope.launch {
      try {
        userRepository.deleteUserProfile(
            onSuccess = {
              Log.d(TAG, "deleteAccount: Success")
              _user.value = null
              onSuccess()
            },
            onFailure = { exception ->
              Log.d(TAG, "deleteAccount : fail to delete user: ${exception.message}")
              onFailure(exception)
            })
      } catch (e: Exception) {
        onFailure(e)
      }
    }
  }
}
