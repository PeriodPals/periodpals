package com.android.periodpals.ui.authentication

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.android.periodpals.R
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.UserAuthenticationState
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignInScreen
import com.android.periodpals.ui.components.AuthenticationCard
import com.android.periodpals.ui.components.AuthenticationEmailInput
import com.android.periodpals.ui.components.AuthenticationPasswordInput
import com.android.periodpals.ui.components.AuthenticationSubmitButton
import com.android.periodpals.ui.components.AuthenticationWelcomeText
import com.android.periodpals.ui.components.GradedBackground
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.theme.ComponentColor.getFilledPrimaryContainerButtonColors
import com.android.periodpals.ui.theme.dimens

private const val DEFAULT_PASSWORD = ""
private const val DEFAULT_EMAIL = ""
private const val DEFAULT_EMAIL_INVALID_MESSAGE = ""
private const val DEFAULT_PASSWORD_INVALID_MESSAGE = ""
private const val DEFAULT_PASSWORD_VISIBILITY = false

private const val SIGN_IN_INSTRUCTION = "Sign in to your account"
private const val SIGN_IN_BUTTON_TEXT = "Sign in"
private const val CONTINUE_WITH_TEXT = "Or continue with"
private const val SIGN_UP_WITH_GOOGLE = "Sign in with Google"
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
  val userState: UserAuthenticationState by authenticationViewModel.userAuthenticationState
  var email by remember { mutableStateOf(DEFAULT_EMAIL) }
  var password by remember { mutableStateOf(DEFAULT_PASSWORD) }
  val (emailErrorMessage, setEmailErrorMessage) =
      remember { mutableStateOf(DEFAULT_EMAIL_INVALID_MESSAGE) }
  val (passwordErrorMessage, setPasswordErrorMessage) =
      remember { mutableStateOf(DEFAULT_PASSWORD_INVALID_MESSAGE) }
  var passwordVisible by remember { mutableStateOf(DEFAULT_PASSWORD_VISIBILITY) }

  LaunchedEffect(Unit) { authenticationViewModel.isUserLoggedIn() }

  Scaffold(modifier = Modifier.fillMaxSize().testTag(SignInScreen.SCREEN)) { paddingValues ->
    GradedBackground()

    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(
                    horizontal = MaterialTheme.dimens.large,
                    vertical = MaterialTheme.dimens.medium3,
                )
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(MaterialTheme.dimens.medium1, Alignment.CenterVertically),
    ) {
      AuthenticationWelcomeText()

      AuthenticationCard {
        Text(
            modifier =
                Modifier.fillMaxWidth().wrapContentHeight().testTag(SignInScreen.INSTRUCTION_TEXT),
            text = SIGN_IN_INSTRUCTION,
            color = MaterialTheme.colorScheme.onSurface,
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
            modifier =
                Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .testTag(SignInScreen.CONTINUE_WITH_TEXT),
            text = CONTINUE_WITH_TEXT,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )

        AuthenticationGoogleButton(context)
      }

      Row(
          modifier = Modifier.fillMaxWidth().wrapContentHeight(),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
            modifier = Modifier.wrapContentSize(),
            text = NO_ACCOUNT_TEXT,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.bodyMedium)

        Text(
            modifier =
                Modifier.wrapContentSize()
                    .clickable { navigationActions.navigateTo(Screen.SIGN_UP) }
                    .testTag(SignInScreen.NOT_REGISTERED_BUTTON),
            text = SIGN_UP_TEXT,
            textDecoration = TextDecoration.Underline,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            style = MaterialTheme.typography.bodyMedium,
        )
      }
    }
  }
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
      colors = getFilledPrimaryContainerButtonColors(),
  ) {
    Row(
        modifier = Modifier.wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement =
            Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterHorizontally),
    ) {
      Image(
          painter = painterResource(id = R.drawable.google_logo),
          contentDescription = "Google Logo",
          modifier = Modifier.size(MaterialTheme.dimens.iconSize),
      )
      Text(
          modifier = Modifier.wrapContentSize(),
          text = SIGN_UP_WITH_GOOGLE,
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
    userState: UserAuthenticationState,
    context: Context,
    navigationActions: NavigationActions,
) {
  val isEmailValid = isEmailValid(email, setEmailErrorMessage)
  val isPasswordValid = isPasswordValid(password, setPasswordErrorMessage)

  if (!isEmailValid || !isPasswordValid) {
    Toast.makeText(context, INVALID_ATTEMPT_TOAST, Toast.LENGTH_SHORT).show()
    return
  }

  authenticationViewModel.logInWithEmail(
      userEmail = email,
      userPassword = password,
      onSuccess = {
        Handler(Looper.getMainLooper()).post {
          Toast.makeText(context, SUCCESSFUL_SIGN_IN_TOAST, Toast.LENGTH_SHORT).show()
        }
        navigationActions.navigateTo(Screen.PROFILE)
      },
      onFailure = {
        Handler(Looper.getMainLooper()).post {
          Toast.makeText(context, FAILED_SIGN_IN_TOAST, Toast.LENGTH_SHORT).show()
        }
      })
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
