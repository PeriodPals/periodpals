package com.android.periodpals.ui.authentication

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.android.periodpals.R
import com.android.periodpals.model.auth.AuthViewModel
import com.android.periodpals.model.user.UserAuthState
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

@Composable
fun SignInScreen(authViewModel: AuthViewModel, navigationActions: NavigationActions) {
  val context = LocalContext.current
  val userState: UserAuthState by authViewModel.userAuthState

  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  var emailErrorMessage by remember { mutableStateOf("") }
  var passwordErrorMessage by remember { mutableStateOf("") }

  var passwordVisible by remember { mutableStateOf(false) }

  var currentUserAuthState by remember { mutableStateOf("") }
  LaunchedEffect(Unit) { authViewModel.isUserLoggedIn(context) }

  // Screen
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("signInScreen"),
      content = { padding ->
        // Purple-ish background
        GradedBackground(Purple80, Pink40, PurpleGrey80, "signInBackground")

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(48.dp, Alignment.CenterVertically),
        ) {
          // Welcome text
          AuthWelcomeText(
              text = "Welcome to PeriodPals",
              color = Color.Black,
              testTag = "signInTitle",
          )

          // Rectangle with login fields and button
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .border(1.dp, Color.Gray, RectangleShape)
                      .background(Color.White)
                      .padding(24.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                ) {
                  // Sign in instruction
                  AuthInstruction(text = "Sign in to your account", testTag = "signInInstruction")

                  // Email input and error message
                  AuthEmailInput(
                      email = email, onEmailChange = { email = it }, testTag = "signInEmail")
                  if (emailErrorMessage.isNotEmpty()) {
                    ErrorText(emailErrorMessage, "signInEmailError")
                  }

                  // Password input and error message
                  AuthPasswordInput(
                      password = password,
                      onPasswordChange = { password = it },
                      passwordVisible = passwordVisible,
                      onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                      testTag = "signInPassword",
                      visibilityTestTag = "signInPasswordVisibility",
                  )
                  if (passwordErrorMessage.isNotEmpty()) {
                    ErrorText(passwordErrorMessage, "signInPasswordError")
                  }

                  // Sign in button
                  AuthButton(
                      text = "Sign in",
                      onClick = {
                        emailErrorMessage = validateEmail(email)
                        passwordErrorMessage = validatePassword(password)

                        if (emailErrorMessage.isEmpty() && passwordErrorMessage.isEmpty()) {
                          authViewModel.logInWithEmail(context, email, password)
                          authViewModel.isUserLoggedIn(context)
                          val loginSuccess = userState is UserAuthState.Success
                          if (loginSuccess) {
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                            navigationActions.navigateTo(Screen.PROFILE)
                          } else {
                            Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                          }
                        } else {
                          Toast.makeText(context, "Invalid email or password.", Toast.LENGTH_SHORT)
                              .show()
                        }
                      },
                      testTag = "signInButton",
                  )

                  // Or continue with text
                  AuthSecondInstruction(text = "Or continue with", testTag = "signInOrText")

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
                      testTag = "signInGoogleButton",
                  )
                }
              }
          // Not registered yet? Sign up here!
          val annotatedText = buildAnnotatedString {
            append("Not registered yet? ")
            pushStringAnnotation(tag = "SignUp", annotation = "SignUp")
            withStyle(style = SpanStyle(color = Color.Blue)) { append("Sign up here!") }
            pop()
          }
          ClickableText(
              modifier = Modifier.testTag("signInNotRegistered"),
              text = annotatedText,
              onClick = { offset ->
                annotatedText
                    .getStringAnnotations(tag = "SignUp", start = offset, end = offset)
                    .firstOrNull()
                    ?.let { navigationActions.navigateTo(Screen.SIGN_UP) }
              },
          )
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
