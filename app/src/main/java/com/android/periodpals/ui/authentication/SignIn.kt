package com.android.periodpals.ui.authentication

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.android.periodpals.R
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignInScreen
import com.android.periodpals.resources.ComponentColor.getFilledPrimaryContainerButtonColors
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
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
            text = context.getString(R.string.sign_in_continue_with_text),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )

        AuthenticationGoogleButton(context, authenticationViewModel, navigationActions)
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
 * Composable function that displays a button for Google sign-in.
 *
 * @param context The context used to show Toast messages.
 * @param authenticationViewModel The ViewModel that handles authentication logic.
 * @param modifier The modifier to be applied to the button.
 */
@Composable
fun AuthenticationGoogleButton(
    context: Context,
    authenticationViewModel: AuthenticationViewModel,
    navigationActions: NavigationActions,
    modifier: Modifier = Modifier,
) {
  val coroutineScope = rememberCoroutineScope()
  Button(
      modifier = modifier.wrapContentSize().testTag(SignInScreen.GOOGLE_BUTTON),
      onClick = {
        attemptAuthenticateWithGoogle(
            context = context,
            authenticationViewModel = authenticationViewModel,
            navigationActions = navigationActions,
            coroutineScope = coroutineScope,
        )
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
          text = context.getString(R.string.sign_in_sign_up_with_google),
          fontWeight = FontWeight.Medium,
          style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}

/**
 * Attempts to sign in the user with the provided email and password.
 *
 * @param emailState The email entered by the user.
 * @param passwordState The password entered by the user.
 * @param authenticationViewModel The ViewModel that handles authentication logic.
 * @param context The context used to show Toast messages.
 * @param navigationActions The navigation actions to navigate between screens.
 * @return A lambda function to be called on button click.
 */
private fun attemptSignIn(
    emailState: TextFieldState,
    passwordState: TextFieldState,
    authenticationViewModel: AuthenticationViewModel,
    context: Context,
    navigationActions: NavigationActions,
) {
  if (!emailState.validate() || !passwordState.validate()) {
    Toast.makeText(
            context, context.getString(R.string.sign_in_toast_invalid_attempt), Toast.LENGTH_SHORT)
        .show()
    return
  }

  authenticationViewModel.logInWithEmail(
      userEmail = emailState.value,
      userPassword = passwordState.value,
      onSuccess = {
        Handler(Looper.getMainLooper()).post {
          Toast.makeText(
                  context,
                  context.getString(R.string.sign_in_toast_successful_sign_in),
                  Toast.LENGTH_SHORT)
              .show()
        }
        PushNotificationsServiceImpl().createDeviceToken()
        navigationActions.navigateTo(Screen.PROFILE)
      },
      onFailure = {
        Handler(Looper.getMainLooper()).post {
          Toast.makeText(
                  context,
                  context.getString(R.string.sign_in_toast_failed_sign_in),
                  Toast.LENGTH_SHORT)
              .show()
        }
      },
  )
}

/**
 * Attempts to authenticate the user with Google.
 *
 * @param context The context used to show Toast messages.
 * @param authenticationViewModel The ViewModel that handles authentication logic.
 * @param navigationActions The navigation actions to navigate between screens.
 * @param coroutineScope The coroutine scope to launch the authentication process.
 * @return A lambda function to be called on button click.
 */
private fun attemptAuthenticateWithGoogle(
    context: Context,
    authenticationViewModel: AuthenticationViewModel,
    navigationActions: NavigationActions,
    coroutineScope: CoroutineScope,
) {
  // Create a CredentialManager instance
  val credentialManager = CredentialManager.create(context)

  val rawNonce = UUID.randomUUID().toString()

  // Configure Google ID option
  val googleIdOption: GetGoogleIdOption =
      GetGoogleIdOption.Builder()
          .setFilterByAuthorizedAccounts(false)
          .setServerClientId(context.getString(R.string.google_client_id))
          .setNonce(authenticationViewModel.generateHashCode(rawNonce))
          .build()

  // Create a GetCredentialRequest
  val request: GetCredentialRequest =
      GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

  // Retrieve the credential
  coroutineScope.launch {
    try {
      val result = credentialManager.getCredential(request = request, context = context)
      val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
      authenticationViewModel.loginWithGoogle(googleIdTokenCredential.idToken, rawNonce)
      navigationActions.navigateTo(Screen.EDIT_PROFILE)
      Toast.makeText(context, "Successful login", Toast.LENGTH_SHORT).show()
    } catch (e: GetCredentialException) {
      Toast.makeText(context, "Failed to get Google ID token", Toast.LENGTH_SHORT).show()
    } catch (e: GoogleIdTokenParsingException) {
      Toast.makeText(context, "Failed to parse Google ID token", Toast.LENGTH_SHORT).show()
    }
  }
}
