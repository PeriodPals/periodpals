package com.android.periodpals.model.auth

interface AuthModel {

  suspend fun login(
    userEmail: String,
    userPassword: String,
    onSuccess: () -> Unit,
    onFailure: (Error) -> Unit,
  )

  suspend fun register(
    userEmail: String,
    userPassword: String,
    onSuccess: () -> Unit,
    onFailure: (Error) -> Unit,
  )

  suspend fun logout(onSuccess: () -> Unit, onFailure: (Error) -> Unit)
}
