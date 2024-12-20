package com.android.periodpals.ui.authentication

import android.content.Context
import android.widget.Toast
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
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignInScreen
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
  val formState = remember { authenticationViewModel.formState }
  formState.reset()

  val emailState = formState.getState<TextFieldState>(AuthenticationViewModel.EMAIL_STATE_NAME)
  val passwordState =
      formState.getState<TextFieldState>(AuthenticationViewModel.PASSWORD_LOGIN_STATE_NAME)
  var isPasswordVisible by remember { mutableStateOf(DEFAULT_IS_PASSWORD_VISIBLE) }

  LaunchedEffect(Unit) { authenticationViewModel.isUserLoggedIn() }

  Scaffold(modifier = Modifier.fillMaxSize().testTag(SignInScreen.SCREEN)) { paddingValues ->
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
                Modifier.fillMaxWidth().wrapContentHeight().testTag(SignInScreen.INSTRUCTION_TEXT),
            text = context.getString(R.string.sign_in_instruction),
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

        AuthenticationSubmitButton(
            text = context.getString(R.string.sign_in_button_text),
            onClick = {
              attemptSignIn(
                  emailState = emailState,
                  passwordState = passwordState,
                  authenticationViewModel = authenticationViewModel,
                  navigationActions = navigationActions,
              )
            },
            testTag = SignInScreen.SIGN_IN_BUTTON,
        )
      }

      NavigateBetweenAuthScreens(
          context.getString(R.string.sign_in_no_account_text),
          context.getString(R.string.sign_in_sign_up_text),
          Screen.SIGN_UP,
          SignInScreen.NOT_REGISTERED_NAV_LINK,
          navigationActions)
    }
  }
}

/**
 * Attempts to sign in the user with the provided email and password.
 *
 * @param emailState The email entered by the user.
 * @param passwordState The password entered by the user.
 * @param authenticationViewModel The ViewModel that handles authentication logic.
 * @param navigationActions The navigation actions to navigate between screens.
 * @return A lambda function to be called on button click.
 */
private fun attemptSignIn(
    emailState: TextFieldState,
    passwordState: TextFieldState,
    authenticationViewModel: AuthenticationViewModel,
    navigationActions: NavigationActions,
) {
  if (!emailState.validate() || !passwordState.validate()) {
    return
  }

  authenticationViewModel.logInWithEmail(
      userEmail = emailState.value,
      userPassword = passwordState.value,
      onSuccess = {
        PushNotificationsServiceImpl().createDeviceToken()
        navigationActions.navigateTo(Screen.PROFILE)
      },
      onFailure = {},
  )
}
