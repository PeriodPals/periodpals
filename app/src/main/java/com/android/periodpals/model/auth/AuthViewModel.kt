package com.android.periodpals.model.auth

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.periodpals.model.user.UserAuthenticationState
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"

class AuthViewModel(private val authModel: AuthModel) : ViewModel() {

  private val _userAuthenticationState =
      mutableStateOf<UserAuthenticationState>(UserAuthenticationState.Loading)
  val userAuthenticationState: State<UserAuthenticationState> = _userAuthenticationState

  fun signUpWithEmail(context: Context, userEmail: String, userPassword: String) {
    _userAuthenticationState.value = UserAuthenticationState.Loading
    viewModelScope.launch {
      authModel.register(
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

  fun logInWithEmail(context: Context, userEmail: String, userPassword: String) {
    _userAuthenticationState.value = UserAuthenticationState.Loading
    viewModelScope.launch {
      authModel.login(
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

  fun logOut(context: Context) {
    // val sharedPreferenceHelper = SharedPreferenceHelper(context)
    _userAuthenticationState.value = UserAuthenticationState.Loading
    viewModelScope.launch {
      authModel.logout(
          onSuccess = {
            Log.d(TAG, "logOut: logged out successfully")
            // sharedPreferenceHelper.clearPreferences()
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

  fun isUserLoggedIn(context: Context) {
    viewModelScope.launch {
      // call model for this ofc
      authModel.isUserLoggedIn(
          token = "",
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
