package com.android.periodpals.model.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing user data.
 *
 * @property userRepository The repository used for loading and saving user profiles.
 */
private const val TAG = "UserViewModel"

class UserViewModel(private val userRepository: UserRepositorySupabase) : ViewModel() {

  private val _user = MutableStateFlow<User?>(null)
  val user: StateFlow<User?> = _user

  /** Loads the user profile and updates the user state. */
  fun loadUser() {
    viewModelScope.launch {
      userRepository.loadUserProfile(
          onSuccess = { userDto ->
            Log.d(TAG, "loadUserProfile: Succesful")
            _user.value = userDto.asUser()
          },
          onFailure = {
            Log.d(TAG, "loadUserProfile: fail to load user profile: ${it.message}")
            _user.value = null
          })
    }
  }

  /**
   * Saves the user profile.
   *
   * @param user The user profile to be saved.
   */
  fun saveUser(user: User) {
    viewModelScope.launch {
      userRepository.createUserProfile(
          user,
          onSuccess = {
            Log.d(TAG, "saveUser: Success")
            _user.value = user
          },
          onFailure = {
            Log.d(TAG, "saveUser: fail to save user: ${it.message}")
            _user.value = null
          })
    }
  }
}