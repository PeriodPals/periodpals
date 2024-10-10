package com.android.periodpals.model.auth

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.providers.builtin.Email

private const val TAG = "AuthModelSupabase"

class AuthModelSupabase(
  private val supabase: SupabaseClient,
  private val pluginManagerWrapper: PluginManagerWrapper =
    PluginManagerWrapperImpl(supabase.pluginManager),
) : AuthModel {

  private val supabaseAuth: Auth = pluginManagerWrapper.getAuthPlugin()

  override suspend fun register(
    userEmail: String,
    userPassword: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit,
  ) {
    try {
      supabaseAuth.signUpWith(Email) {
        email = userEmail
        password = userPassword
      }
      Log.d(TAG, "register: successfully registered the user")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "register: failed to register the user: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun login(
    userEmail: String,
    userPassword: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit,
  ) {
    try {
      supabaseAuth.signInWith(Email) {
        email = userEmail
        password = userPassword
      }
      Log.d(TAG, "login: successfully logged in the user")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "login: failed to log in the user: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun logout(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    try {
      supabaseAuth.signOut(SignOutScope.LOCAL)
      Log.d(TAG, "logout: successfully logged out the user")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "logout: failed to log out the user: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun isUserLoggedIn(
    token: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit,
  ) {
    try {
      supabaseAuth.retrieveUser(token)
      supabaseAuth.refreshCurrentSession() // will throw an error if the user is not logged in
      Log.d(TAG, "isUserLoggedIn: user is logged in")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "isUserLoggedIn: user is not logged in: ${e.message}")
      onFailure(e)
    }
  }
}
