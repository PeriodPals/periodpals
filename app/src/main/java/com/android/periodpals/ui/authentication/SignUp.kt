package com.android.periodpals.ui.authentication

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.R
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignUpScreen
import com.android.periodpals.services.PushNotificationsServiceImpl
import com.android.periodpals.ui.components.AuthenticationCard
import com.android.periodpals.ui.components.AuthenticationEmailInput
import com.android.periodpals.ui.components.AuthenticationPasswordInput
import com.android.periodpals.ui.components.AuthenticationSubmitButton
import com.android.periodpals.ui.components.AuthenticationWelcomeText
import com.android.periodpals.ui.components.GradedBackground
import com.android.periodpals.ui.components.NavigateBetweenAuthScreens
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.theme.dimens
import com.dsc.form_builder.TextFieldState

private const val DEFAULT_IS_PASSWORD_VISIBLE = false

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
  val formState = remember { authenticationViewModel.formState }
  formState.reset()

  val emailState = formState.getState<TextFieldState>(AuthenticationViewModel.EMAIL_STATE_NAME)
  val passwordState =
      formState.getState<TextFieldState>(AuthenticationViewModel.PASSWORD_SIGNUP_STATE_NAME)
  val confirmPasswordState =
      formState.getState<TextFieldState>(AuthenticationViewModel.CONFIRM_PASSWORD_STATE_NAME)
  var isPasswordVisible by remember { mutableStateOf(DEFAULT_IS_PASSWORD_VISIBLE) }
  var isConfirmedPasswordVisible by remember { mutableStateOf(DEFAULT_IS_PASSWORD_VISIBLE) }

  LaunchedEffect(Unit) { authenticationViewModel.isUserLoggedIn() }

  Scaffold(modifier = Modifier.fillMaxSize().testTag(SignUpScreen.SCREEN)) { paddingValues ->
    GradedBackground()

    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(
                    horizontal = MaterialTheme.dimens.large,
                    vertical = MaterialTheme.dimens.medium3)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(MaterialTheme.dimens.medium1, Alignment.CenterVertically),
    ) {
      AuthenticationWelcomeText()

      AuthenticationCard {
        Text(
            modifier =
                Modifier.fillMaxWidth().wrapContentHeight().testTag(SignUpScreen.INSTRUCTION_TEXT),
            text = context.getString(R.string.sign_up_instruction),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )

        AuthenticationEmailInput(
            email = emailState.value,
            onEmailChange = { emailState.change(it) },
            emailErrorMessage = emailState.errorMessage,
        )

        AuthenticationPasswordInput(
            password = passwordState.value,
            onPasswordChange = { passwordState.change(it) },
            passwordVisible = isPasswordVisible,
            onPasswordVisibilityChange = { isPasswordVisible = !isPasswordVisible },
            passwordErrorMessage = passwordState.errorMessage,
        )

        Text(
            modifier =
                Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .testTag(SignUpScreen.CONFIRM_PASSWORD_TEXT),
            text = context.getString(R.string.sign_up_confirm_password_instruction),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )

        AuthenticationPasswordInput(
            password = confirmPasswordState.value,
            onPasswordChange = { confirmPasswordState.change(it) },
            passwordVisible = isConfirmedPasswordVisible,
            onPasswordVisibilityChange = {
              isConfirmedPasswordVisible = !isConfirmedPasswordVisible
            },
            passwordErrorMessage = confirmPasswordState.errorMessage,
            passwordErrorTestTag = SignUpScreen.CONFIRM_PASSWORD_ERROR_TEXT,
            testTag = SignUpScreen.CONFIRM_PASSWORD_FIELD,
            visibilityTestTag = SignUpScreen.CONFIRM_PASSWORD_VISIBILITY_BUTTON,
        )

        AuthenticationSubmitButton(
            text = context.getString(R.string.sign_up_button_text),
            onClick = {
              attemptSignUp(
                  emailState = emailState,
                  passwordState = passwordState,
                  confirmPasswordState = confirmPasswordState,
                  authenticationViewModel = authenticationViewModel,
                  context = context,
                  navigationActions = navigationActions,
              )
            },
            testTag = SignUpScreen.SIGN_UP_BUTTON,
        )
      }

      NavigateBetweenAuthScreens(
          context.getString(R.string.sign_up_already_account_text),
          context.getString(R.string.sign_up_sign_in_text),
          Screen.SIGN_IN,
          SignUpScreen.ALREADY_REGISTERED_NAV_LINK,
          navigationActions)
    }
  }
}

/**
 * Attempts to sign up the user with the provided email, password, and confirmed password.
 *
 * @param emailState The email entered by the user.
 * @param passwordState The password entered by the user.
 * @param confirmPasswordState The confirmed password entered by the user.
 * @param authenticationViewModel The ViewModel that handles authentication logic.
 * @param context The context used to display error messages.
 * @param navigationActions The navigation actions to navigate between screens.
 */
private fun attemptSignUp(
    emailState: TextFieldState,
    passwordState: TextFieldState,
    confirmPasswordState: TextFieldState,
    authenticationViewModel: AuthenticationViewModel,
    context: Context,
    navigationActions: NavigationActions,
) {
  // strange if statements, but necessary to show the proper error messages
  if (!emailState.validate() || !passwordState.validate()) {
    return
  }
  if (!confirmPasswordState.validate() || passwordState.value != confirmPasswordState.value) {
    confirmPasswordState.errorMessage =
        context.getString(R.string.sign_up_not_matching_password_error_message)
    return
  }

  authenticationViewModel.signUpWithEmail(
      userEmail = emailState.value,
      userPassword = passwordState.value,
      onSuccess = {
        PushNotificationsServiceImpl().createDeviceToken()
        navigationActions.navigateTo(Screen.CREATE_PROFILE)
      },
      onFailure = {},
  )
}
