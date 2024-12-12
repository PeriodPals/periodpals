package com.android.periodpals.model.user

/**
 * Represents the authentication state of a user in the application.
 *
 * This sealed class is used to handle different states of user authentication throughout the app.
 * It helps in managing the UI state based on whether the authentication process is loading,
 * successful, or has encountered an error.
 */
sealed class UserAuthenticationState {

  /**
   * Represents the loading state of the authentication process. Indicates that the authentication
   * process is currently in progress.
   */
  data object Loading : UserAuthenticationState()

  /**
   * Represents the success state of the authentication process. Indicates that the authentication
   * process has completed successfully.
   *
   * @property message A message with additional information about the successful authentication.
   */
  data class Success(val message: String) : UserAuthenticationState()

    /**
     * Represents the success state of the authentication process. Indicates that the user is logged
     * in.
     *
     * @property message A message with additional information about the successful login.
     */
    data class SuccessIsLoggedIn(val message: String) : UserAuthenticationState()

    /**
     * Represents the success state of the authentication process. Indicates that the user is logged
     * out.
     *
     * @property message A message with additional information about the successful logout.
     */
    data class SuccessLogOut(val message: String) : UserAuthenticationState()

    /**
   * Represents the error state of the authentication process. Indicates that there was an error
   * during the authentication process.
   *
   * @property message A message describing the error encountered during authentication.
   */
  data class Error(val message: String) : UserAuthenticationState()
}
