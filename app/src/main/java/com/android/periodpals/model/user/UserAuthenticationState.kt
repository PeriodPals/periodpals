package com.android.periodpals.model.user

sealed class UserAuthenticationState {
  data object Loading : UserAuthenticationState()

  data class Success(val message: String) : UserAuthenticationState()

  data class Error(val message: String) : UserAuthenticationState()
}
