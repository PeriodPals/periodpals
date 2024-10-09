package com.android.periodpals.model.auth

interface AuthModel {

  suspend fun login(
    userEmail: String,
    userPassword: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit,
  )

  suspend fun register(
    userEmail: String,
    userPassword: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit,
  )

  suspend fun logout(onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  suspend fun isUserLoggedIn(token: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
