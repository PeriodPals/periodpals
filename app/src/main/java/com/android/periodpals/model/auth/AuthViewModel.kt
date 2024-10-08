package com.android.periodpals.model.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(private val authModel: AuthModel) : ViewModel() {

  fun signUpWithEmail(userEmail: String, userPassword: String) {
    viewModelScope.launch { authModel.login(userEmail, userPassword) }
  }
}
