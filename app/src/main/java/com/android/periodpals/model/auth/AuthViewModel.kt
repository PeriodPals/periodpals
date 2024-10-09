package com.android.periodpals.model.auth

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.periodpals.model.user.UserState
import com.android.periodpals.utils.SharedPreferenceHelper
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModel"

class AuthViewModel(private val authModel: AuthModel) : ViewModel() {

  private val _userState = mutableStateOf<UserState>(UserState.Loading)
  val userState: State<UserState> = _userState

  fun signUpWithEmail(context: Context, userEmail: String, userPassword: String) {
    viewModelScope.launch {
      authModel.register(
        email = userEmail,
        password = userPassword,
        onSuccess = {
          saveAccessToken(context)
          Log.d(TAG, "signUpWithEmail: registered user successfully")
          _userState.value = UserState.Success("Registered user successfully")
        },
        onFailure = { e: Exception ->
          Log.d(TAG, "signUpWithEmail: failed to register user: $e")
          _userState.value = UserState.Error("Error: $e")
        },
      )
    }
  }

  fun logInWithEmail(context: Context, userEmail: String, userPassword: String) {
    viewModelScope.launch {
      authModel.login(
        email = userEmail,
        password = userPassword,
        onSuccess = {
          saveAccessToken(context)
          Log.d(TAG, "logInWithEmail: logged in successfully")
          _userState.value = UserState.Success("Logged in successfully")
        },
        onFailure = { e: Exception ->
          Log.d(TAG, "logInWithEmail: failed to log in: $e")
          _userState.value = UserState.Error("Error: $e")
        },
      )
    }
  }

  fun logOut() {
    viewModelScope.launch {
      authModel.logout(
        onSuccess = {
          Log.d(TAG, "logOut: logged out successfully")
          _userState.value = UserState.Success("Logged out successfully")
        },
        onFailure = { e: Exception ->
          Log.d(TAG, "logOut: failed to log out: $e")
          _userState.value = UserState.Error("Error: $e")
        },
      )
    }
  }

  fun isUserLoggedIn(context: Context) {
    viewModelScope.launch {
      authModel.isUserLoggedIn(
        token = getTokens(context),
        onSuccess = {
          Log.d(TAG, "isUserLoggedIn: user is logged in")
          saveAccessToken(context)
          _userState.value = UserState.Success("User is logged in")
        },
        onFailure = {
          Log.d(TAG, "isUserLoggedIn: user is not logged in")
          _userState.value = UserState.Error("User is not logged in")
          val loggedIn = false
        },
      )
    }
  }

  private fun saveAccessToken(context: Context) {
    viewModelScope.launch {
      val accessToken = authModel.getAccessToken()
      val sharedPreferences = SharedPreferenceHelper(context)
      sharedPreferences.saveStringData("accessToken", accessToken)
    }
  }

  private fun getTokens(context: Context): String? {
    val sharedPreferences = SharedPreferenceHelper(context)
    return sharedPreferences.getStringData("accessToken")
  }
}
