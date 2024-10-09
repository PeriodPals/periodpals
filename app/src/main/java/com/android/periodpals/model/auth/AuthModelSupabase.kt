package com.android.periodpals.model.auth

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

private const val TAG = "AuthModelSupabase"

class AuthModelSupabase(private val supabase: SupabaseClient) : AuthModel {

  override suspend fun login(
    userEmail: String,
    userPassword: String,
    onSuccess: () -> Unit,
    onFailure: (Error) -> Unit,
  ) {
    try {
      supabase.auth.signInWith(Email) {
        email = userEmail
        password = userPassword
      }
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "Error: ${e.message}")
      onFailure(Error(e.message))
    }
  }

  override suspend fun register(
    userEmail: String,
    userPassword: String,
    onSuccess: () -> Unit,
    onFailure: (Error) -> Unit,
  ) {
    // TODO: try catch block
    supabase.auth.signUpWith(Email) {
      email = userEmail
      password = userPassword
    }
    onSuccess()
  }

  override suspend fun logout(onSuccess: () -> Unit, onFailure: (Error) -> Unit) {
    // TODO: try catch block
    supabase.auth.signOut(SignOutScope.LOCAL)
    onSuccess()
  }
}
