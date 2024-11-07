package com.android.periodpals.ui.authentication

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
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
import com.android.periodpals.R
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.UserAuthState
import com.android.periodpals.resources.C.Tag.SignInScreen
import com.android.periodpals.ui.components.AuthButton
import com.android.periodpals.ui.components.AuthEmailInput
import com.android.periodpals.ui.components.AuthInstruction
import com.android.periodpals.ui.components.AuthPasswordInput
import com.android.periodpals.ui.components.AuthSecondInstruction
import com.android.periodpals.ui.components.AuthWelcomeText
import com.android.periodpals.ui.components.ErrorText
import com.android.periodpals.ui.components.GradedBackground
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.theme.Pink40
import com.android.periodpals.ui.theme.Purple80
import com.android.periodpals.ui.theme.PurpleGrey80
import com.android.periodpals.ui.theme.dimens

@Composable
fun SignInScreen(
    authenticationViewModel: AuthenticationViewModel,
    navigationActions: NavigationActions,
) {
  val context = LocalContext.current
  val userState: UserAuthState by authenticationViewModel.userAuthState

  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  var emailErrorMessage by remember { mutableStateOf("") }
  var passwordErrorMessage by remember { mutableStateOf("") }

  var passwordVisible by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) { authenticationViewModel.isUserLoggedIn() }

  // Screen
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(SignInScreen.SCREEN),
      content = { padding ->
        // Purple-ish background
        GradedBackground(Purple80, Pink40, PurpleGrey80, SignInScreen.BACKGROUND)

        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .padding(
                        horizontal = MaterialTheme.dimens.large,
                        vertical = MaterialTheme.dimens.medium3),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(MaterialTheme.dimens.medium1, Alignment.CenterVertically)) {
              // Welcome text
              item {
                AuthWelcomeText(
                    text = "Welcome to PeriodPals",
                    color = Color.Black,
                    testTag = SignInScreen.TITLE_TEXT,
                )
              }

              // Rectangle with login fields and button
              item {
                Box(
                    modifier =
                        Modifier.fillMaxWidth()
                            .wrapContentHeight()
                            .border(MaterialTheme.dimens.borderLine, Color.Gray, RectangleShape)
                            .background(Color.White)
                            .padding(
                                horizontal = MaterialTheme.dimens.medium1,
                                vertical = MaterialTheme.dimens.small3)) {
                      Column(
                          modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                          horizontalAlignment = Alignment.CenterHorizontally,
                          verticalArrangement =
                              Arrangement.spacedBy(
                                  MaterialTheme.dimens.small2, Alignment.CenterVertically),
                      ) {
                        // Sign in instruction
                        AuthInstruction(
                            text = "Sign in to your account",
                            testTag = SignInScreen.INSTRUCTION_TEXT,
                        )

                        // Email input and error message
                        AuthEmailInput(
                            email = email,
                            onEmailChange = { email = it },
                            testTag = SignInScreen.EMAIL_FIELD,
                        )
                        if (emailErrorMessage.isNotEmpty()) {
                          ErrorText(emailErrorMessage, SignInScreen.EMAIL_ERROR_TEXT)
                        }

                        // Password input and error message
                        AuthPasswordInput(
                            password = password,
                            onPasswordChange = { password = it },
                            passwordVisible = passwordVisible,
                            onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                            testTag = SignInScreen.PASSWORD_FIELD,
                            visibilityTestTag = SignInScreen.PASSWORD_VISIBILITY_BUTTON,
                        )
                        if (passwordErrorMessage.isNotEmpty()) {
                          ErrorText(passwordErrorMessage, SignInScreen.PASSWORD_ERROR_TEXT)
                        }

                        // Sign in button
                        AuthButton(
                            text = "Sign in",
                            onClick = {
                              emailErrorMessage = validateEmail(email)
                              passwordErrorMessage = validatePassword(password)

                              if (emailErrorMessage.isEmpty() && passwordErrorMessage.isEmpty()) {
                                authenticationViewModel.logInWithEmail(email, password)
                                authenticationViewModel.isUserLoggedIn()
                                val loginSuccess = userState is UserAuthState.Success
                                if (loginSuccess) {
                                  // with supabase
                                  Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT)
                                      .show()
                                  navigationActions.navigateTo(Screen.PROFILE)
                                } else {
                                  Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                                }
                              } else {
                                Toast.makeText(
                                        context, "Invalid email or password.", Toast.LENGTH_SHORT)
                                    .show()
                              }
                            },
                            testTag = SignInScreen.SIGN_IN_BUTTON,
                        )

                        // Or continue with text
                        AuthSecondInstruction(
                            text = "Or continue with",
                            testTag = SignInScreen.CONTINUE_WITH_TEXT,
                        )

                        // Google sign in button
                        GoogleButton(
                            onClick = {
                              Toast.makeText(
                                      context,
                                      "Use other login method for now, thanks!",
                                      Toast.LENGTH_SHORT,
                                  )
                                  .show()
                            },
                            testTag = SignInScreen.GOOGLE_BUTTON,
                        )
                      }
                    }
              }

              item {
                Row(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                      Text(
                          text = "Not registered yet? ",
                          style = MaterialTheme.typography.bodyMedium)
                      Text(
                          text = "Sign up here!",
                          modifier =
                              Modifier.clickable { navigationActions.navigateTo(Screen.SIGN_UP) }
                                  .testTag(SignInScreen.NOT_REGISTERED_BUTTON),
                          color = Color.Blue,
                          style = MaterialTheme.typography.bodyMedium)
                    }
              }
            }
      },
  )
}

/** Validates the email and returns an error message if the email is invalid. */
private fun validateEmail(email: String): String {
  return when {
    email.isEmpty() -> "Email cannot be empty"
    !email.contains("@") -> "Email must contain @"
    else -> {
      // TODO: Check existing email from Supabase
      ""
    }
  }
}

/** Validates the password and returns an error message if the password is invalid. */
private fun validatePassword(password: String): String {
  return when {
    password.isEmpty() -> "Password cannot be empty"
    else -> {
      // TODO: Check password with Supabase
      ""
    }
  }
}

/** A composable that displays a Google sign in button. */
@Composable
fun GoogleButton(onClick: () -> Unit, modifier: Modifier = Modifier, testTag: String) {
  Button(
      modifier = modifier.wrapContentSize().testTag(testTag),
      onClick = onClick,
      colors = ButtonDefaults.buttonColors(containerColor = Color.White),
      shape = RoundedCornerShape(50),
      border = BorderStroke(MaterialTheme.dimens.borderLine, Color.LightGray),
  ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
      Image(
          painter = painterResource(id = R.drawable.google_logo),
          contentDescription = "Google Logo",
          modifier = Modifier.size(MaterialTheme.dimens.iconSize),
      )
      Spacer(modifier = Modifier.size(MaterialTheme.dimens.small2))
      Text(
          text = "Sign in with Google",
          color = Color.Black,
          fontWeight = FontWeight.Medium,
          style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}
