package com.android.periodpals.model.authentication

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.periodpals.model.user.UserAuthenticationState
import kotlinx.coroutines.launch

private const val TAG = "AuthenticationViewModel"

/**
 * ViewModel for handling authentication-related operations.
 *
 * @property authenticationModel The authentication model used for performing auth operations.
 */
class AuthenticationViewModel(private val authenticationModel: AuthenticationModel) : ViewModel() {

  private val _userAuthenticationState =
      mutableStateOf<UserAuthenticationState>(UserAuthenticationState.Loading)
  val userAuthenticationState: State<UserAuthenticationState> = _userAuthenticationState

  /**
   * Registers a new user with the provided email and password.
   *
   * @param userEmail The email of the user.
   * @param userPassword The password of the user.
   */
  fun signUpWithEmail(userEmail: String, userPassword: String) {
    _userAuthenticationState.value = UserAuthenticationState.Loading
    viewModelScope.launch {
      authenticationModel.register(
          userEmail = userEmail,
          userPassword = userPassword,
          onSuccess = {
            Log.d(TAG, "signUpWithEmail: registered user successfully")
            _userAuthenticationState.value =
                UserAuthenticationState.Success("Registered user successfully")
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "signUpWithEmail: failed to register user: $e")
            _userAuthenticationState.value = UserAuthenticationState.Error("Error: $e")
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
    _userAuthenticationState.value = UserAuthenticationState.Loading
    viewModelScope.launch {
      authenticationModel.login(
          userEmail = userEmail,
          userPassword = userPassword,
          onSuccess = {
            Log.d(TAG, "logInWithEmail: logged in successfully")
            _userAuthenticationState.value =
                UserAuthenticationState.Success("Logged in successfully")
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "logInWithEmail: failed to log in: $e")
            _userAuthenticationState.value = UserAuthenticationState.Error("Error: $e")
          },
      )
    }
  }

  /** Logs out the current user. */
  fun logOut() {
    _userAuthenticationState.value = UserAuthenticationState.Loading
    viewModelScope.launch {
      authenticationModel.logout(
          onSuccess = {
            Log.d(TAG, "logOut: logged out successfully")
            _userAuthenticationState.value =
                UserAuthenticationState.Success("Logged out successfully")
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "logOut: failed to log out: $e")
            _userAuthenticationState.value = UserAuthenticationState.Error("Error: $e")
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
            _userAuthenticationState.value = UserAuthenticationState.Success("User is logged in")
          },
          onFailure = {
            Log.d(TAG, "isUserLoggedIn: user is not logged in")
            _userAuthenticationState.value = UserAuthenticationState.Error("User is not logged in")
          },
      )
    }
  }
}
