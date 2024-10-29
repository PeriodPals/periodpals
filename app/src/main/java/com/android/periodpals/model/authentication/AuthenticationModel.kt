package com.android.periodpals.model.authentication

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
}
