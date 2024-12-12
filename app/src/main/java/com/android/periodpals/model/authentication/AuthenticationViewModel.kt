package com.android.periodpals.model.authentication

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.periodpals.model.user.AuthenticationUserData
import com.android.periodpals.model.user.UserAuthenticationState
import com.dsc.form_builder.FormState
import com.dsc.form_builder.TextFieldState
import com.dsc.form_builder.Validators
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.launch
import java.security.MessageDigest

private const val TAG = "AuthenticationViewModel"

private const val PASSWORD_MIN_LENGTH = 8
private const val INPUT_MAX_LENGTH = 128
private const val EMPTY_EMAIL_ERROR_MESSAGE = "Email cannot be empty"
private const val TOO_LONG_EMAIL_ERROR_MESSAGE =
    "Email must be at most $INPUT_MAX_LENGTH characters long"
private const val EMPTY_PASSWORD_ERROR_MESSAGE = "Password cannot be empty"
private const val TOO_SHORT_PASSWORD_ERROR_MESSAGE =
    "Password must be at least $PASSWORD_MIN_LENGTH characters long"
private const val TOO_LONG_PASSWORD_ERROR_MESSAGE =
    "Password must be at most $INPUT_MAX_LENGTH characters long"
private const val NO_CAPITAL_PASSWORD_ERROR_MESSAGE =
    "Password must contain at least one capital letter"
private const val NO_LOWER_CASE_PASSWORD_ERROR_MESSAGE =
    "Password must contain at least one lower case letter"
private const val NO_NUMBER_PASSWORD_ERROR_MESSAGE = "Password must contain at least one number"
private const val NO_SPECIAL_CHAR_PASSWORD_ERROR_MESSAGE =
    "Password must contain at least one special character"

private val emailValidators =
    listOf(
        Validators.Email(),
        Validators.Required(message = EMPTY_EMAIL_ERROR_MESSAGE),
        Validators.Max(message = TOO_LONG_EMAIL_ERROR_MESSAGE, limit = INPUT_MAX_LENGTH),
    )
private val passwordLoginValidators =
    listOf(
        Validators.Required(message = EMPTY_PASSWORD_ERROR_MESSAGE),
        Validators.Max(message = TOO_LONG_PASSWORD_ERROR_MESSAGE, limit = INPUT_MAX_LENGTH),
    )
private val passwordSignupValidators =
    listOf(
        Validators.Min(message = TOO_SHORT_PASSWORD_ERROR_MESSAGE, limit = PASSWORD_MIN_LENGTH),
        Validators.Max(message = TOO_LONG_PASSWORD_ERROR_MESSAGE, limit = INPUT_MAX_LENGTH),
        Validators.Custom(
            message = NO_CAPITAL_PASSWORD_ERROR_MESSAGE,
            function = { Regex(".*[A-Z].*").containsMatchIn(it as String) },
        ),
        Validators.Custom(
            message = NO_LOWER_CASE_PASSWORD_ERROR_MESSAGE,
            function = { Regex(".*[a-z].*").containsMatchIn(it as String) },
        ),
        Validators.Custom(
            message = NO_NUMBER_PASSWORD_ERROR_MESSAGE,
            function = { Regex(".*[0-9].*").containsMatchIn(it as String) },
        ),
        Validators.Custom(
            message = NO_SPECIAL_CHAR_PASSWORD_ERROR_MESSAGE,
            function = { Regex(".*[!@#\$%^&*(),.?\":{}|<>].*").containsMatchIn(it as String) },
        ),
        Validators.Required(message = EMPTY_PASSWORD_ERROR_MESSAGE),
    )

/**
 * ViewModel for handling authentication-related operations.
 *
 * @property authenticationModel The authentication model used for performing auth operations.
 */
class AuthenticationViewModel(private val authenticationModel: AuthenticationModel) : ViewModel() {

  companion object {
    const val EMAIL_STATE_NAME = "email"
    const val PASSWORD_SIGNUP_STATE_NAME = "password_signup"
    const val CONFIRM_PASSWORD_STATE_NAME = "confirm_password_signup"
    const val PASSWORD_LOGIN_STATE_NAME = "password_login"
  }

  private val _userAuthenticationState =
      mutableStateOf<UserAuthenticationState>(UserAuthenticationState.Loading)
  val userAuthenticationState: State<UserAuthenticationState> = _userAuthenticationState

  private val _authUserData = mutableStateOf<AuthenticationUserData?>(null)
  val authUserData: State<AuthenticationUserData?> = _authUserData

  val formState =
      FormState(
          fields =
              listOf(
                  TextFieldState(name = EMAIL_STATE_NAME, validators = emailValidators),
                  TextFieldState(
                      name = PASSWORD_SIGNUP_STATE_NAME, validators = passwordSignupValidators),
                  TextFieldState(
                      name = CONFIRM_PASSWORD_STATE_NAME, validators = passwordSignupValidators),
                  TextFieldState(
                      name = PASSWORD_LOGIN_STATE_NAME, validators = passwordLoginValidators),
              ))

    init {
        isUserLoggedIn()
    }

    /**
   * Registers a new user with the provided email and password.
   *
   * @param userEmail The email of the user.
   * @param userPassword The password of the user.
   * @param onSuccess Callback to be invoked when the registration is successful.
   * @param onFailure Callback to be invoked when the registration fails.
   */
  fun signUpWithEmail(
      userEmail: String,
      userPassword: String,
      onSuccess: () -> Unit = { Log.d(TAG, "signUp success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "signUp failure callback: $e")
      },
  ) {
    _userAuthenticationState.value = UserAuthenticationState.Loading
    viewModelScope.launch {
      authenticationModel.register(
          userEmail = userEmail,
          userPassword = userPassword,
          onSuccess = {
            Log.d(TAG, "signUpWithEmail: registered user successfully")
            _userAuthenticationState.value =
                UserAuthenticationState.Success("Registered user successfully")
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "signUpWithEmail: failed to register user: $e")
            _userAuthenticationState.value = UserAuthenticationState.Error("Error: $e")
            onFailure(e)
          },
      )
    }
  }

  /**
   * Logs in a user with the provided email and password.
   *
   * @param userEmail The email of the user.
   * @param userPassword The password of the user.
   * @param onSuccess Callback to be invoked when the login is successful.
   * @param onFailure Callback to be invoked when the login fails.
   */
  fun logInWithEmail(
      userEmail: String,
      userPassword: String,
      onSuccess: () -> Unit = { Log.d(TAG, "signIn success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "signIn failure callback: $e")
      },
  ) {
    _userAuthenticationState.value = UserAuthenticationState.Loading
    viewModelScope.launch {
      authenticationModel.login(
          userEmail = userEmail,
          userPassword = userPassword,
          onSuccess = {
            Log.d(TAG, "logInWithEmail: logged in successfully")
            _userAuthenticationState.value =
                UserAuthenticationState.Success("Logged in successfully")
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "logInWithEmail: failed to log in: $e")
            _userAuthenticationState.value = UserAuthenticationState.Error("Error: $e")
            onFailure(e)
          },
      )
    }
  }

  /**
   * Logs out the current user.
   *
   * @param onSuccess Callback to be invoked when the logout is successful.
   * @param onFailure Callback to be invoked when the logout fails.
   */
  fun logOut(
      onSuccess: () -> Unit = { Log.d(TAG, "logOut success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "logOut failure callback: $e")
      },
  ) {
    _userAuthenticationState.value = UserAuthenticationState.Loading
    viewModelScope.launch {
      authenticationModel.logout(
          onSuccess = {
            Log.d(TAG, "logOut: logged out successfully")
            _userAuthenticationState.value =
                UserAuthenticationState.SuccessLogOut("Logged out successfully")
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "logOut: failed to log out: $e")
            _userAuthenticationState.value = UserAuthenticationState.Error("Error: $e")
            onFailure(e)
          },
      )
    }
  }

  /**
   * Checks if a user is logged in.
   *
   * @param onSuccess Callback to be invoked when the user is confirmed to be logged in.
   * @param onFailure Callback to be invoked when the user is not logged in.
   */
  fun isUserLoggedIn(
      onSuccess: () -> Unit = { Log.d(TAG, "isUserLoggedIn success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "isUserLoggedIn failure callback: $e")
      },
  ) {
    viewModelScope.launch {
      authenticationModel.isUserLoggedIn(
          onSuccess = {
            Log.d(TAG, "isUserLoggedIn: user is confirmed logged in")
              _userAuthenticationState.value =
                  UserAuthenticationState.SuccessIsLoggedIn("User is logged in")
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "isUserLoggedIn: user is not logged in")
            _userAuthenticationState.value = UserAuthenticationState.Error("User is not logged in")
            onFailure(e)
          },
      )
    }
  }

  /**
   * Loads AuthenticationUserData to local state.
   *
   * @param onSuccess Callback to be invoked when the user data is successfully loaded.
   * @param onFailure Callback to be invoked when the user data fails to load.
   */
  fun loadAuthenticationUserData(
      onSuccess: () -> Unit = { Log.d(TAG, "loadAuthenticationUserData success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "loadAuthenticationUserData failure callback: $e")
      },
  ) {
    viewModelScope.launch {
      authenticationModel.currentAuthenticationUser(
          onSuccess = {
            Log.d(TAG, "loadAuthUserData: user data successfully loaded")
            _authUserData.value = it.asAuthenticationUserData()
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "loadAuthUserData: failed to load user data")
            _authUserData.value = null
            onFailure(e)
          },
      )
    }
  }

  /**
   * Logs in a user with the provided Google ID token.
   *
   * @param googleIdToken The Google ID token.
   * @param rawNonce The raw nonce.
   * @param onSuccess Callback to be invoked when the login is successful.
   * @param onFailure Callback to be invoked when the login fails.
   */
  fun loginWithGoogle(
      googleIdToken: String,
      rawNonce: String?,
      onSuccess: () -> Unit = { Log.d(TAG, "loginWithGoogle success callback") },
      onFailure: (Exception) -> Unit = { e: Exception ->
        Log.d(TAG, "loginWithGoogle failure callback: $e")
      },
  ) {
    _userAuthenticationState.value = UserAuthenticationState.Loading
    viewModelScope.launch {
      authenticationModel.loginGoogle(
          googleIdToken,
          rawNonce,
          onSuccess = {
            Log.d(TAG, "loginWithGoogle: logged in successfully")
            _userAuthenticationState.value =
                UserAuthenticationState.Success("Logged in successfully")
            onSuccess()
          },
          onFailure = { e: Exception ->
            Log.d(TAG, "loginWithGoogle: failed to log in: $e")
            _userAuthenticationState.value = UserAuthenticationState.Error("Error: $e")
            onFailure(e)
          },
      )
    }
  }

  /**
   * Generates a hash code from a raw nonce.
   *
   * @param rawNonce The raw nonce.
   * @return The hash code.
   */
  fun generateHashCode(rawNonce: String): String {
    val bytes = rawNonce.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
  }

  /** Convert UserInfo into AuthenticationUserData */
  private fun UserInfo.asAuthenticationUserData(): AuthenticationUserData {
    return AuthenticationUserData(uid = this.id, email = this.email)
  }
}
