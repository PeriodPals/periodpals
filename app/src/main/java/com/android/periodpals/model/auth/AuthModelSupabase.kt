package com.android.periodpals.model.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthModelSupabase(private val supabase: SupabaseClient) : AuthModel {

  override suspend fun signup(userEmail: String, userPassword: String) {
    supabase.auth.signUpWith(Email) {
      email = userEmail
      password = userPassword
    }
  }

  override suspend fun login(userEmail: String, userPassword: String) {
    supabase.auth.signInWith(Email) {
      email = userEmail
      password = userPassword
    }
  }

  override suspend fun register(userEmail: String, userPassword: String) {
    supabase.auth.signUpWith(Email) {
      email = userEmail
      password = userPassword
    }
  }

  override suspend fun logout() {
    supabase.auth.signOut(SignOutScope.LOCAL)
  }
}
