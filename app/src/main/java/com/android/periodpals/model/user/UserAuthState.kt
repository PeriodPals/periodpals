package com.android.periodpals.model.user

sealed class UserAuthState {
  data object Loading : UserAuthState()

  data class Success(val message: String) : UserAuthState()

  data class Error(val message: String) : UserAuthState()
}
