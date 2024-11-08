package com.android.periodpals.ui.authentication

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.UserAuthenticationState
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignUpScreen
import com.android.periodpals.ui.components.AuthenticationEmailInput
import com.android.periodpals.ui.components.AuthenticationPasswordInput
import com.android.periodpals.ui.components.AuthenticationSubmitButton
import com.android.periodpals.ui.components.AuthenticationWelcomeText
import com.android.periodpals.ui.components.GradedBackground
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.theme.dimens

private const val DEFAULT_EMAIL = ""
private const val DEFAULT_PASSWORD = ""
private const val DEFAULT_CONFIRMED_PASSWORD = ""
private const val DEFAULT_EMAIL_INVALID_MESSAGE = ""
private const val DEFAULT_PASSWORD_INVALID_MESSAGE = ""
private const val DEFAULT_CONFIRMED_PASSWORD_INVALID_MESSAGE = ""
private const val DEFAULT_PASSWORD_VISIBLE = false
private const val DEFAULT_CONFIRMED_PASSWORD_VISIBLE = false

private const val SIGN_UP_INSTRUCTION = "Create your account"
private const val CONFIRM_PASSWORD_INSTRUCTION = "Confirm your password"
private const val SIGN_UP_BUTTON_TEXT = "Sign up"
private const val MIN_PASSWORD_LENGTH = 8

private const val EMPTY_EMAIL_ERROR_MESSAGE = "Email cannot be empty"
private const val NO_AT_EMAIL_ERROR_MESSAGE = "Email must contain @"
private const val EMPTY_PASSWORD_ERROR_MESSAGE = "Password cannot be empty"
private const val TOO_SHORT_PASSWORD_ERROR_MESSAGE = "Password must be at least 8 characters long"
private const val NO_CAPITAL_PASSWORD_ERROR_MESSAGE =
    "Password must contain at least one capital letter"
private const val NO_LOWER_CASE_PASSWORD_ERROR_MESSAGE =
    "Password must contain at least one lower case letter"
private const val NO_NUMBER_PASSWORD_ERROR_MESSAGE = "Password must contain at least one number"
private const val NO_SPECIAL_CHAR_PASSWORD_ERROR_MESSAGE =
    "Password must contain at least one special character"
private const val NOT_MATCHING_PASSWORD_ERROR_MESSAGE = "Passwords do not match"

private const val SUCCESSFUL_SIGN_UP_TOAST = "Account Creation Successful"
private const val FAILED_SIGN_UP_TOAST = "Account Creation Failed"
private const val INVALID_ATTEMPT_TOAST = "Invalid email or password"

/**
 * A composable function that displays the sign-up screen.
 *
 * @param authenticationViewModel The ViewModel that handles authentication logic.
 * @param navigationActions The navigation actions to navigate between screens.
 */
@Composable
fun SignUpScreen(
    authenticationViewModel: AuthenticationViewModel,
    navigationActions: NavigationActions,
) {
  val context = LocalContext.current
  val userState: UserAuthenticationState by authenticationViewModel.userAuthenticationState

  var email by remember { mutableStateOf(DEFAULT_EMAIL) }
  var password by remember { mutableStateOf(DEFAULT_PASSWORD) }
  var confirmedPassword by remember { mutableStateOf(DEFAULT_CONFIRMED_PASSWORD) }
  val (emailErrorMessage, setEmailErrorMessage) =
      remember { mutableStateOf(DEFAULT_EMAIL_INVALID_MESSAGE) }
  val (passwordErrorMessage, setPasswordErrorMessage) =
      remember { mutableStateOf(DEFAULT_PASSWORD_INVALID_MESSAGE) }
  val (confirmedPasswordErrorMessage, setConfirmedPasswordErrorMessage) =
      remember { mutableStateOf(DEFAULT_CONFIRMED_PASSWORD_INVALID_MESSAGE) }
  var passwordVisible by remember { mutableStateOf(DEFAULT_PASSWORD_VISIBLE) }
  var confirmedPasswordVisible by remember { mutableStateOf(DEFAULT_CONFIRMED_PASSWORD_VISIBLE) }

  LaunchedEffect(Unit) { authenticationViewModel.isUserLoggedIn() }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(SignUpScreen.SCREEN),
      content = { padding ->
        GradedBackground()

        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .padding(
                        horizontal = MaterialTheme.dimens.large,
                        vertical = MaterialTheme.dimens.medium3,
                    ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(MaterialTheme.dimens.medium1, Alignment.CenterVertically),
        ) {
          item { AuthenticationWelcomeText() }

          item {
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .wrapContentHeight()
                        .border(MaterialTheme.dimens.borderLine, Color.Gray, RectangleShape)
                        .background(Color.White)
                        .padding(
                            horizontal = MaterialTheme.dimens.medium1,
                            vertical = MaterialTheme.dimens.small3,
                        )) {
                  Column(
                      modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement =
                          Arrangement.spacedBy(
                              MaterialTheme.dimens.small2, Alignment.CenterVertically),
                  ) {
                    Text(
                        modifier =
                            Modifier.fillMaxWidth()
                                .wrapContentHeight()
                                .testTag(SignUpScreen.INSTRUCTION_TEXT),
                        text = SIGN_UP_INSTRUCTION,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    AuthenticationEmailInput(
                        email = email,
                        onEmailChange = { email = it },
                        emailErrorMessage = emailErrorMessage,
                    )

                    AuthenticationPasswordInput(
                        password = password,
                        onPasswordChange = { password = it },
                        passwordVisible = passwordVisible,
                        onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                        passwordErrorMessage,
                    )
                    Text(
                        modifier =
                            Modifier.fillMaxWidth()
                                .wrapContentHeight()
                                .testTag(SignUpScreen.CONFIRM_PASSWORD_TEXT),
                        text = CONFIRM_PASSWORD_INSTRUCTION,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    AuthenticationPasswordInput(
                        password = confirmedPassword,
                        onPasswordChange = { confirmedPassword = it },
                        passwordVisible = confirmedPasswordVisible,
                        onPasswordVisibilityChange = {
                          confirmedPasswordVisible = !confirmedPasswordVisible
                        },
                        passwordErrorMessage = confirmedPasswordErrorMessage,
                        passwordErrorTestTag = SignUpScreen.CONFIRM_PASSWORD_ERROR_TEXT,
                        testTag = SignUpScreen.CONFIRM_PASSWORD_FIELD,
                        visibilityTestTag = SignUpScreen.CONFIRM_PASSWORD_VISIBILITY_BUTTON,
                    )

                    AuthenticationSubmitButton(
                        text = SIGN_UP_BUTTON_TEXT,
                        onClick = {
                          attemptSignUp(
                              email = email,
                              password = password,
                              confirmedPassword = confirmedPassword,
                              setEmailErrorMessage = setEmailErrorMessage,
                              setPasswordErrorMessage = setPasswordErrorMessage,
                              setConfirmedPasswordErrorMessage = setConfirmedPasswordErrorMessage,
                              authenticationViewModel = authenticationViewModel,
                              userState = userState,
                              context = context,
                              navigationActions = navigationActions,
                          )
                        },
                        testTag = SignUpScreen.SIGN_UP_BUTTON,
                    )
                  }
                }
          }
        }
      },
  )
}

/**
 * Attempts to sign up the user with the provided email, password, and confirmed password.
 *
 * @param email The email entered by the user.
 * @param password The password entered by the user.
 * @param confirmedPassword The confirmed password entered by the user.
 * @param setEmailErrorMessage A function to set the error message for the email field.
 * @param setPasswordErrorMessage A function to set the error message for the password field.
 * @param setConfirmedPasswordErrorMessage A function to set the error message for the confirmed
 *   password field.
 * @param authenticationViewModel The ViewModel that handles authentication logic.
 * @param userState The current state of the user authentication.
 * @param context The context used to show Toast messages.
 * @param navigationActions The navigation actions to navigate between screens.
 */
private fun attemptSignUp(
    email: String,
    password: String,
    confirmedPassword: String,
    setEmailErrorMessage: (String) -> Unit,
    setPasswordErrorMessage: (String) -> Unit,
    setConfirmedPasswordErrorMessage: (String) -> Unit,
    authenticationViewModel: AuthenticationViewModel,
    userState: UserAuthenticationState,
    context: Context,
    navigationActions: NavigationActions,
) {
  val isEmailValid = isEmailValid(email, setEmailErrorMessage)
  val isPasswordValid = isPasswordValid(password, setPasswordErrorMessage)
  val isConfirmedPasswordValid =
      isConfirmedPasswordValid(password, confirmedPassword, setConfirmedPasswordErrorMessage)

  if (!isEmailValid || !isPasswordValid || !isConfirmedPasswordValid) {
    Toast.makeText(context, INVALID_ATTEMPT_TOAST, Toast.LENGTH_SHORT).show()
    return
  }

  authenticationViewModel.signUpWithEmail(email, password)
  authenticationViewModel.isUserLoggedIn()

  val loginSuccess = userState is UserAuthenticationState.Success
  if (!loginSuccess) {
    Toast.makeText(context, FAILED_SIGN_UP_TOAST, Toast.LENGTH_SHORT).show()
    return
  }

  Toast.makeText(context, SUCCESSFUL_SIGN_UP_TOAST, Toast.LENGTH_SHORT).show()
  navigationActions.navigateTo(Screen.CREATE_PROFILE)
  return
}

/**
 * Validates the email and returns an error message if the email is invalid.
 *
 * @param email The email to validate.
 * @param setErrorMessage A function to set the error message for the email field.
 * @return True if the email is valid, false otherwise.
 */
private fun isEmailValid(email: String, setErrorMessage: (String) -> Unit): Boolean {
  return when {
    email.isEmpty() -> {
      setErrorMessage(EMPTY_EMAIL_ERROR_MESSAGE)
      false
    }
    !email.contains("@") -> {
      setErrorMessage(NO_AT_EMAIL_ERROR_MESSAGE)
      false
    }
    else -> {
      setErrorMessage(DEFAULT_EMAIL_INVALID_MESSAGE)
      true
    }
  }
}

/**
 * Validates the password and returns an error message if the password is invalid.
 *
 * @param password The password to validate.
 * @param setErrorMessage A function to set the error message for the password field.
 * @return True if the password is valid, false otherwise.
 */
private fun isPasswordValid(password: String, setErrorMessage: (String) -> Unit): Boolean {
  val capitalLetter = Regex(".*[A-Z].*")
  val minusculeLetter = Regex(".*[a-z].*")
  val number = Regex(".*[0-9].*")
  val specialChar = Regex(".*[!@#\$%^&*(),.?\":{}|<>].*")

  return when {
    password.isEmpty() -> {
      setErrorMessage(EMPTY_PASSWORD_ERROR_MESSAGE)
      false
    }
    password.length < MIN_PASSWORD_LENGTH -> {
      setErrorMessage(TOO_SHORT_PASSWORD_ERROR_MESSAGE)
      false
    }
    !capitalLetter.containsMatchIn(password) -> {
      setErrorMessage(NO_CAPITAL_PASSWORD_ERROR_MESSAGE)
      false
    }
    !minusculeLetter.containsMatchIn(password) -> {
      setErrorMessage(NO_LOWER_CASE_PASSWORD_ERROR_MESSAGE)
      false
    }
    !number.containsMatchIn(password) -> {
      setErrorMessage(NO_NUMBER_PASSWORD_ERROR_MESSAGE)
      false
    }
    !specialChar.containsMatchIn(password) -> {
      setErrorMessage(NO_SPECIAL_CHAR_PASSWORD_ERROR_MESSAGE)
      false
    }
    else -> {
      setErrorMessage(DEFAULT_EMAIL_INVALID_MESSAGE)
      true
    }
  }
}

/**
 * Validates the confirmed password and returns an error message if the passwords do not match.
 *
 * @param password The original password.
 * @param confirm The confirmed password.
 * @param setErrorMessage A function to set the error message for the confirmed password field.
 * @return True if the confirmed password matches the original password, false otherwise.
 */
private fun isConfirmedPasswordValid(
    password: String,
    confirm: String,
    setErrorMessage: (String) -> Unit,
): Boolean {
  return if (password != confirm) {
    setErrorMessage(NOT_MATCHING_PASSWORD_ERROR_MESSAGE)
    false
  } else {
    setErrorMessage(DEFAULT_CONFIRMED_PASSWORD_INVALID_MESSAGE)
    true
  }
}
