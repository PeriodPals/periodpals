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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.periodpals.ui.theme.Pink40
import com.android.periodpals.ui.theme.Purple40
import com.android.periodpals.ui.theme.PurpleGrey80

@Preview
@Composable
fun SignUpScreen() {
  val context = LocalContext.current
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var confirm by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var confirmVisible by remember { mutableStateOf(false) }
  var passwordErrorMessage by remember { mutableStateOf("") }
  var confirmErrorMessage by remember { mutableStateOf("") }

  // Screen
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      content = { padding ->
        // Purple-ish background
        GradedBackground(Pink40, Purple40, PurpleGrey80, "signUpBackground")

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(64.dp, Alignment.CenterVertically),
        ) {
          // Welcome text
          AuthWelcomeText(
              text = "Welcome to PeriodPals", color = Color.White, testTag = "signUpTitle")

          // Rectangle with login fields and button
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .border(1.dp, Color.Gray, RectangleShape)
                      .background(Color.White)
                      .padding(24.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)) {
                      // Sign up instruction
                      AuthInstruction(text = "Create your account", testTag = "signUpInstruction")

                      // Email input
                      AuthEmailInput(
                          email = email, onEmailChange = { email = it }, testTag = "signUpEmail")

                      // Password input
                      AuthPasswordInput(
                          password = password,
                          onPasswordChange = {
                            password = it
                            passwordErrorMessage = validatePassword(password)
                          },
                          passwordVisible = passwordVisible,
                          onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                          testTag = "signUpPassword")

                      // Password validation error message
                      if (passwordErrorMessage.isNotEmpty()) {
                        Text(
                            modifier = Modifier.testTag("signUpPasswordErrorMessage"),
                            text = passwordErrorMessage,
                            color = Color.Red,
                            style =
                                MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 16.sp, fontWeight = FontWeight.Medium))
                      }

                      // Confirm password text
                      AuthSecondInstruction(
                          text = "Confirm your password", testTag = "signUpConfirmText")

                      // Confirm password input
                      OutlinedTextField(
                          modifier = Modifier.fillMaxWidth().testTag("signUpPassword"),
                          value = confirm,
                          onValueChange = { confirm = it },
                          label = { Text("Confirm Password") },
                          visualTransformation =
                              if (confirmVisible) VisualTransformation.None
                              else PasswordVisualTransformation(),
                          trailingIcon = {
                            val image =
                                if (confirmVisible) Icons.Outlined.Visibility
                                else Icons.Outlined.VisibilityOff
                            IconButton(onClick = { confirmVisible = !confirmVisible }) {
                              Icon(
                                  imageVector = image,
                                  contentDescription =
                                      if (confirmVisible) "Hide password" else "Show password")
                            }
                          })

                      // Confirm password error message
                      if (confirmErrorMessage.isNotEmpty()) {
                        Text(
                            modifier = Modifier.testTag("signUpConfirmErrorMessage"),
                            text = confirmErrorMessage,
                            color = Color.Red,
                            style =
                                MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 16.sp, fontWeight = FontWeight.Medium))
                      }

                      // Sign up button
                      AuthButton(
                          text = "Sign up",
                          onClick = {
                            confirmErrorMessage = validateConfirmPassword(password, confirm)
                            if (passwordErrorMessage.isEmpty() && confirmErrorMessage.isEmpty()) {
                              if (email.isNotEmpty()) {
                                // TODO: Check duplicate emails from Supabase and existing accounts
                                val loginSuccess = true // Replace with actual logic
                                if (loginSuccess) {
                                  Toast.makeText(
                                          context,
                                          "Account Creation Successful",
                                          Toast.LENGTH_SHORT)
                                      .show()
                                } else {
                                  Toast.makeText(
                                          context, "Account Creation Failed", Toast.LENGTH_SHORT)
                                      .show()
                                }
                              } else {
                                Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_SHORT)
                                    .show()
                              }
                            }
                          },
                          testTag = "signUpButton",
                      )
                    }
              }
        }
      })
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
        "Password must contain at least one minuscule letter"
    !number.containsMatchIn(password) -> "Password must contain at least one number"
    !specialChar.containsMatchIn(password) -> "Password must contain at least one special character"
    else -> ""
  }
}

/** Validates if the password and confirm password fields match. */
private fun validateConfirmPassword(password: String, confirm: String): String {
  return if (password != confirm) "Passwords do not match" else ""
}
