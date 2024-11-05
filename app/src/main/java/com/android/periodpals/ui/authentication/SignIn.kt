package com.android.periodpals.ui.authentication

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.periodpals.R
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.UserAuthState
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignInScreen
import com.android.periodpals.ui.components.AuthenticationEmailInput
import com.android.periodpals.ui.components.AuthenticationPasswordInput
import com.android.periodpals.ui.components.AuthenticationSubmitButton
import com.android.periodpals.ui.components.AuthenticationWelcomeText
import com.android.periodpals.ui.components.GradedBackground
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen

private const val DEFAULT_PASSWORD = ""
private const val DEFAULT_EMAIL = ""
private const val DEFAULT_EMAIL_INVALID_MESSAGE = ""
private const val DEFAULT_PASSWORD_INVALID_MESSAGE = ""
private const val DEFAULT_PASSWORD_VISIBILITY = false

private const val SIGN_IN_INSTRUCTION = "Sign in to your account"
private const val SIGN_IN_BUTTON_TEXT = "Sign in"
private const val CONTINUE_WITH_TEXT = "Or continue with"
private const val NO_ACCOUNT_TEXT = "Not registered yet? "
private const val SIGN_UP_TEXT = "Sign up here!"

private const val SUCCESSFUL_SIGN_IN_TOAST = "Login Successful"
private const val FAILED_SIGN_IN_TOAST = "Login Failed"
private const val INVALID_ATTEMPT_TOAST = "Invalid email or password."

private const val NO_AT_EMAIL_ERROR_MESSAGE = "Email must contain @"
private const val EMPTY_EMAIL_ERROR_MESSAGE = "Email cannot be empty"
private const val EMPTY_PASSWORD_ERROR_MESSAGE = "Password cannot be empty"

/**
 * Composable function that displays the Sign In screen.
 *
 * @param authenticationViewModel The ViewModel that handles authentication logic.
 * @param navigationActions The navigation actions to navigate between screens.
 */
@Composable
fun SignInScreen(
    authenticationViewModel: AuthenticationViewModel,
    navigationActions: NavigationActions,
) {
  val context = LocalContext.current
  val userState: UserAuthState by authenticationViewModel.userAuthState
  var email by remember { mutableStateOf(DEFAULT_EMAIL) }
  var password by remember { mutableStateOf(DEFAULT_PASSWORD) }
  val (emailErrorMessage, setEmailErrorMessage) =
      remember { mutableStateOf(DEFAULT_EMAIL_INVALID_MESSAGE) }
  val (passwordErrorMessage, setPasswordErrorMessage) =
      remember { mutableStateOf(DEFAULT_PASSWORD_INVALID_MESSAGE) }
  var passwordVisible by remember { mutableStateOf(DEFAULT_PASSWORD_VISIBILITY) }

  LaunchedEffect(Unit) { authenticationViewModel.isUserLoggedIn() }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(SignInScreen.SCREEN),
      content = { padding ->
        GradedBackground()
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(48.dp, Alignment.CenterVertically),
        ) {
          AuthenticationWelcomeText()
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .border(1.dp, Color.Gray, RectangleShape)
                      .background(Color.White)
                      .padding(24.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                ) {
                  Text(
                      modifier = Modifier.testTag(SignInScreen.INSTRUCTION_TEXT),
                      text = SIGN_IN_INSTRUCTION,
                      style =
                          MaterialTheme.typography.bodyLarge.copy(
                              fontSize = 20.sp,
                              fontWeight = FontWeight.Medium,
                          ),
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
                      passwordErrorMessage = passwordErrorMessage,
                  )
                  AuthenticationSubmitButton(
                      text = SIGN_IN_BUTTON_TEXT,
                      onClick = {
                        attemptSignIn(
                            email = email,
                            setEmailErrorMessage = setEmailErrorMessage,
                            password = password,
                            setPasswordErrorMessage = setPasswordErrorMessage,
                            authenticationViewModel = authenticationViewModel,
                            userState = userState,
                            context = context,
                            navigationActions = navigationActions,
                        )
                      },
                      testTag = SignInScreen.SIGN_IN_BUTTON,
                  )
                  Text(
                      modifier = Modifier.testTag(SignInScreen.CONTINUE_WITH_TEXT),
                      text = CONTINUE_WITH_TEXT,
                      style =
                          MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                  )
                  AuthenticationGoogleButton(context)
                }
              }
          Row(modifier = Modifier) {
            Text(NO_ACCOUNT_TEXT)
            Text(
                text = SIGN_UP_TEXT,
                modifier =
                    Modifier.clickable { navigationActions.navigateTo(Screen.SIGN_UP) }
                        .testTag(SignInScreen.NOT_REGISTERED_BUTTON),
                color = Color.Blue,
            )
          }
        }
      },
  )
}

/**
 * Composable function that displays a button for Google sign-in.
 *
 * @param context The context used to show Toast messages.
 * @param modifier The modifier to be applied to the button.
 */
@Composable
fun AuthenticationGoogleButton(context: Context, modifier: Modifier = Modifier) {
  Button(
      modifier = modifier.wrapContentSize().testTag(SignInScreen.GOOGLE_BUTTON),
      onClick = {
        // TODO: implement Google sign in
        Toast.makeText(context, "Use other login method for now, thanks!", Toast.LENGTH_SHORT)
            .show()
      },
      colors = ButtonDefaults.buttonColors(containerColor = Color.White),
      shape = RoundedCornerShape(50),
      border = BorderStroke(1.dp, Color.LightGray),
  ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
      Image(
          painter = painterResource(id = R.drawable.google_logo),
          contentDescription = "Google Logo",
          modifier = Modifier.size(24.dp),
      )
      Spacer(modifier = Modifier.size(8.dp))
      Text(
          text = "Sign in with Google",
          color = Color.Black,
          fontWeight = FontWeight.Medium,
          style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}

/**
 * Attempts to sign in the user with the provided email and password.
 *
 * @param email The email entered by the user.
 * @param setEmailErrorMessage A function to set the error message for the email field.
 * @param password The password entered by the user.
 * @param setPasswordErrorMessage A function to set the error message for the password field.
 * @param authenticationViewModel The ViewModel that handles authentication logic.
 * @param userState The current state of the user authentication.
 * @param context The context used to show Toast messages.
 * @param navigationActions The navigation actions to navigate between screens.
 * @return A lambda function to be called on button click.
 */
private fun attemptSignIn(
    email: String,
    setEmailErrorMessage: (String) -> Unit,
    password: String,
    setPasswordErrorMessage: (String) -> Unit,
    authenticationViewModel: AuthenticationViewModel,
    userState: UserAuthState,
    context: Context,
    navigationActions: NavigationActions,
) {
  val isEmailValid = isEmailValid(email, setEmailErrorMessage)
  val isPasswordValid = isPasswordValid(password, setPasswordErrorMessage)

  if (!isEmailValid || !isPasswordValid) {
    Toast.makeText(context, INVALID_ATTEMPT_TOAST, Toast.LENGTH_SHORT).show()
    return
  }

  authenticationViewModel.logInWithEmail(email, password)
  authenticationViewModel.isUserLoggedIn()

  val loginSuccess = userState is UserAuthState.Success
  if (!loginSuccess) {
    Toast.makeText(context, FAILED_SIGN_IN_TOAST, Toast.LENGTH_SHORT).show()
    return
  }

  Toast.makeText(context, SUCCESSFUL_SIGN_IN_TOAST, Toast.LENGTH_SHORT).show()
  navigationActions.navigateTo(Screen.PROFILE)
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
  return when {
    password.isEmpty() -> {
      setErrorMessage(EMPTY_PASSWORD_ERROR_MESSAGE)
      false
    }
    else -> {
      setErrorMessage(DEFAULT_PASSWORD_INVALID_MESSAGE)
      true
    }
  }
}
