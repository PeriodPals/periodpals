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
      authModel.login(
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

  private fun saveAccessToken(currentContext: Context) {
    viewModelScope.launch {
      val accessToken = authModel.getAccessToken()
      val sharedPreferences = SharedPreferenceHelper(currentContext)
      sharedPreferences.saveStringData("accessToken", accessToken)
    }
  }
}
