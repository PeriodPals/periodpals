package com.android.periodpals.ui.authentication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.android.periodpals.model.authentication.AuthenticationViewModel
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
import com.android.periodpals.ui.theme.Purple40
import com.android.periodpals.ui.theme.PurpleGrey80

@Composable
fun SignUpScreen(
  authenticationViewModel: AuthenticationViewModel,
  navigationActions: NavigationActions,
) {
  val context = LocalContext.current
  val userState: UserAuthState by authenticationViewModel.userAuthState

  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirm by remember { mutableStateOf("") }

  var emailErrorMessage by remember { mutableStateOf("") }
  var passwordErrorMessage by remember { mutableStateOf("") }
  var confirmErrorMessage by remember { mutableStateOf("") }

  var passwordVisible by remember { mutableStateOf(false) }
  var confirmVisible by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) { authenticationViewModel.isUserLoggedIn() }

  // Screen
  Scaffold(
    modifier = Modifier.fillMaxSize().testTag("signUpScreen"),
    content = { padding ->
      // Purple-ish background
      GradedBackground(Pink40, Purple40, PurpleGrey80, "signUpBackground")

      Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(48.dp, Alignment.CenterVertically),
      ) {
        // Welcome text
        AuthWelcomeText(
          text = "Welcome to PeriodPals",
          color = Color.White,
          testTag = "signUpTitle",
        )

        // Rectangle with login fields and button
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .border(1.dp, Color.Gray, RectangleShape)
              .background(Color.White)
              .padding(24.dp)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
          ) {
            // Sign up instruction
            AuthInstruction(text = "Create your account", testTag = "signUpInstruction")

            // Email input and error message
            AuthEmailInput(email = email, onEmailChange = { email = it }, testTag = "signUpEmail")
            if (emailErrorMessage.isNotEmpty()) {
              ErrorText(message = emailErrorMessage, testTag = "signUpEmailError")
            }

            // Password input and error message
            AuthPasswordInput(
              password = password,
              onPasswordChange = {
                password = it
                passwordErrorMessage = validatePassword(password)
              },
              passwordVisible = passwordVisible,
              onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
              testTag = "signUpPassword",
              visibilityTestTag = "signUpPasswordVisibility",
            )
            if (passwordErrorMessage.isNotEmpty()) {
              ErrorText(message = passwordErrorMessage, testTag = "signUpPasswordError")
            }

            // Confirm password text
            AuthSecondInstruction(text = "Confirm your password", testTag = "signUpConfirmText")

            // Confirm password input and error message
            AuthPasswordInput(
              password = confirm,
              onPasswordChange = { confirm = it },
              passwordVisible = confirmVisible,
              onPasswordVisibilityChange = { confirmVisible = !confirmVisible },
              testTag = "signUpConfirmPassword",
              visibilityTestTag = "signUpConfirmVisibility",
            )
            if (confirmErrorMessage.isNotEmpty()) {
              ErrorText(message = confirmErrorMessage, testTag = "signUpConfirmError")
            }

            // Sign up button
            AuthButton(
              text = "Sign up",
              onClick = {
                emailErrorMessage = validateEmail(email)
                passwordErrorMessage = validatePassword(password)
                confirmErrorMessage = validateConfirmPassword(password, confirm)

                if (
                  emailErrorMessage.isEmpty() &&
                    passwordErrorMessage.isEmpty() &&
                    confirmErrorMessage.isEmpty()
                ) {
                  if (email.isNotEmpty() && password.isNotEmpty()) {
                    authenticationViewModel.signUpWithEmail(email, password)
                    authenticationViewModel.isUserLoggedIn()
                    val loginSuccess = userState is UserAuthState.Success
                    if (loginSuccess) {
                      Toast.makeText(context, "Account Creation Successful", Toast.LENGTH_SHORT)
                        .show()
                      navigationActions.navigateTo(Screen.CREATE_PROFILE)
                    } else {
                      Toast.makeText(context, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                    }
                  } else {
                    Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                  }
                } else {
                  Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
              },
              testTag = "signUpButton",
            )
          }
        }
      }
    },
  )
}

/** Validates the email field is not empty, contains an '@' character and is not already used. */
private fun validateEmail(email: String): String {
  return when {
    email.isEmpty() -> "Email cannot be empty"
    !email.contains("@") -> "Email must contain @"
    else -> {
      // TODO: Check non-existing email from Supabase
      ""
    }
  }
}

/**
 * Validates the password field meets the following requirements:
 * - At least 8 characters long,
 * - Contains at least one capital letter,
 * - Contains at least one minuscule letter,
 * - Contains at least one number,
 * - Contains at least one special character.
 */
private fun validatePassword(password: String): String {
  val capitalLetter = Regex(".*[A-Z].*")
  val minusculeLetter = Regex(".*[a-z].*")
  val number = Regex(".*[0-9].*")
  val specialChar = Regex(".*[!@#\$%^&*(),.?\":{}|<>].*")

  return when {
    password.isEmpty() -> "Password cannot be empty"
    password.length < 8 -> "Password must be at least 8 characters long"
    !capitalLetter.containsMatchIn(password) -> "Password must contain at least one capital letter"
    !minusculeLetter.containsMatchIn(password) ->
      "Password must contain at least one lower case letter"
    !number.containsMatchIn(password) -> "Password must contain at least one number"
    !specialChar.containsMatchIn(password) -> "Password must contain at least one special character"
    else -> ""
  }
}

/** Validates if the password and confirm password fields match. */
private fun validateConfirmPassword(password: String, confirm: String): String {
  return if (password != confirm) "Passwords do not match" else ""
}
