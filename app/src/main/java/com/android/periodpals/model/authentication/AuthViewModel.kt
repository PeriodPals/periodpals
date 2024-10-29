package com.android.periodpals.model.authentication

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.periodpals.model.user.UserAuthState
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"

class AuthViewModel(private val authModel: AuthModel) : ViewModel() {

  private val _userAuthState = mutableStateOf<UserAuthState>(UserAuthState.Loading)
  val userAuthState: State<UserAuthState> = _userAuthState

  fun signUpWithEmail(context: Context, userEmail: String, userPassword: String) {
    _userAuthState.value = UserAuthState.Loading
    viewModelScope.launch {
      authModel.register(
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

  fun logInWithEmail(context: Context, userEmail: String, userPassword: String) {
    _userAuthState.value = UserAuthState.Loading
    viewModelScope.launch {
      authModel.login(
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

  fun logOut(context: Context) {
    // val sharedPreferenceHelper = SharedPreferenceHelper(context)
    _userAuthState.value = UserAuthState.Loading
    viewModelScope.launch {
      authModel.logout(
        onSuccess = {
          Log.d(TAG, "logOut: logged out successfully")
          // sharedPreferenceHelper.clearPreferences()
          _userAuthState.value = UserAuthState.Success("Logged out successfully")
        },
        onFailure = { e: Exception ->
          Log.d(TAG, "logOut: failed to log out: $e")
          _userAuthState.value = UserAuthState.Error("Error: $e")
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
