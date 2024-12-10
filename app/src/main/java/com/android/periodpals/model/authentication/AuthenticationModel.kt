package com.android.periodpals.model.authentication

import io.getstream.chat.android.models.User
import io.github.jan.supabase.auth.user.UserInfo

/** Interface representing the authentication model. */
interface AuthenticationModel {

  /**
   * Logs in a user with the provided email and password.
   *
   * @param userEmail The email of the user.
   * @param userPassword The password of the user.
   * @param onSuccess Callback function to be called on successful login.
   * @param onFailure Callback function to be called on login failure, with the exception as a
   *   parameter.
   */
  suspend fun login(
      userEmail: String,
      userPassword: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit,
  )

  /**
   * Registers a new user with the provided email and password.
   *
   * @param userEmail The email of the user.
   * @param userPassword The password of the user.
   * @param onSuccess Callback function to be called on successful registration.
   * @param onFailure Callback function to be called on registration failure, with the exception as
   *   a parameter.
   */
  suspend fun register(
      userEmail: String,
      userPassword: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit,
  )

  /**
   * Logs out the current user.
   *
   * @param onSuccess Callback function to be called on successful logout.
   * @param onFailure Callback function to be called on logout failure, with the exception as a
   *   parameter.
   */
  suspend fun logout(onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Checks if a user is logged in.
   *
   * @param onSuccess Callback function to be called if the user is logged in.
   * @param onFailure Callback function to be called if the user is not logged in, with the
   *   exception as a parameter.
   */
  suspend fun isUserLoggedIn(onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Fetches the current user's authentication data.
   *
   * @param onSuccess Callback function to be called if user's data is successfully fetched
   * @param onFailure Callback function to be called if exception is raised
   */
  suspend fun currentAuthenticationUser(
      onSuccess: (UserInfo) -> Unit,
      onFailure: (Exception) -> Unit,
  )

  /**
   * Logs in a user using Google authentication.
   *
   * This function uses the Google Sign-In SDK to authenticate the user with their Google account.
   * It retrieves the user's Google ID token and uses it to sign in with Supabase.
   *
   * @param googleIdToken The Google ID token of the user.
   * @param rawNonce The raw nonce used to sign the user in.
   * @param onSuccess Callback function to be called on successful login.
   * @param onFailure Callback function to be called on login failure, with the exception as a
   *   parameter.
   */
  suspend fun loginGoogle(
      googleIdToken: String,
      rawNonce: String?,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Logs in a user using Google authentication.
   *
   * @param onSuccess Callback function to be called on successful login.
   * @param onFailure Callback function to be called on login failure, with the exception as a
   *   parameter.
   *
   * TODO: Delete ? Who wrote this?
   */
  suspend fun getCurrentUser(onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit): User?

  /**
   * Fetches the current user's authentication data.
   *
   * @param onSuccess Callback function to be called if user's data is successfully fetched
   * @param onFailure Callback function to be called if exception is raised
   */
  suspend fun getJwtToken(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit)
}
