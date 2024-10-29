package com.android.periodpals.model.authentication

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.periodpals.model.user.UserAuthState
import kotlinx.coroutines.launch

private const val TAG = "AuthenticationViewModel"

/**
 * ViewModel for handling authentication-related operations.
 *
 * @property authenticationModel The authentication model used for performing auth operations.
 */
class AuthenticationViewModel(private val authenticationModel: AuthenticationModel) : ViewModel() {

  private val _userAuthState = mutableStateOf<UserAuthState>(UserAuthState.Loading)
  val userAuthState: State<UserAuthState> = _userAuthState

  /**
   * Registers a new user with the provided email and password.
   *
   * @param userEmail The email of the user.
   * @param userPassword The password of the user.
   */
  fun signUpWithEmail(userEmail: String, userPassword: String) {
    _userAuthState.value = UserAuthState.Loading
    viewModelScope.launch {
      authenticationModel.register(
          userEmail = userEmail,
          userPassword = userPassword,
          onSuccess = {
            Log.d(TAG, "signUpWithEmail: registered user successfully")
            _userAuthState.value = UserAuthState.Success("Registered user successfully")
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "signUpWithEmail: failed to register user: $e")
            _userAuthState.value = UserAuthState.Error("Error: $e")
          },
      )
    }
  }

  /**
   * Logs in a user with the provided email and password.
   *
   * @param userEmail The email of the user.
   * @param userPassword The password of the user.
   */
  fun logInWithEmail(userEmail: String, userPassword: String) {
    _userAuthState.value = UserAuthState.Loading
    viewModelScope.launch {
      authenticationModel.login(
          userEmail = userEmail,
          userPassword = userPassword,
          onSuccess = {
            Log.d(TAG, "logInWithEmail: logged in successfully")
            _userAuthState.value = UserAuthState.Success("Logged in successfully")
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "logInWithEmail: failed to log in: $e")
            _userAuthState.value = UserAuthState.Error("Error: $e")
          },
      )
    }
  }

  /** Logs out the current user. */
  fun logOut() {
    _userAuthState.value = UserAuthState.Loading
    viewModelScope.launch {
      authenticationModel.logout(
          onSuccess = {
            Log.d(TAG, "logOut: logged out successfully")
            _userAuthState.value = UserAuthState.Success("Logged out successfully")
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "logOut: failed to log out: $e")
            _userAuthState.value = UserAuthState.Error("Error: $e")
          },
      )
    }
  }

  /** Checks if a user is logged in. */
  fun isUserLoggedIn() {
    viewModelScope.launch {
      authenticationModel.isUserLoggedIn(
          onSuccess = {
            Log.d(TAG, "isUserLoggedIn: user is confirmed logged in")
            _userAuthState.value = UserAuthState.Success("User is logged in")
          },
          onFailure = {
            Log.d(TAG, "isUserLoggedIn: user is not logged in")
            _userAuthState.value = UserAuthState.Error("User is not logged in")
          },
      )
    }
  }
}
