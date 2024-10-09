package com.android.periodpals.model.auth

interface AuthModel {

  suspend fun login(userEmail: String, userPassword: String)

  suspend fun register(userEmail: String, userPassword: String)

  suspend fun logout()
}
