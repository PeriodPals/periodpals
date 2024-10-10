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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
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
        GradedBackground(Pink40, Purple40, PurpleGrey80)

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(64.dp, Alignment.CenterVertically),
        ) {
          // Welcome text
          Text(
              modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).testTag("signUpTitle"),
              text = "Welcome to PeriodPals",
              textAlign = TextAlign.Center,
              color = Color.White,
              style =
                  MaterialTheme.typography.headlineLarge.copy(
                      fontSize = 40.sp, lineHeight = 64.sp, fontWeight = FontWeight.SemiBold))

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
                      Text(
                          modifier = Modifier.testTag("signUpInstruction"),
                          text = "Create your account",
                          style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp))

                      // Email input
                      OutlinedTextField(
                          modifier =
                              Modifier.fillMaxWidth().wrapContentSize().testTag("signUpEmail"),
                          value = email,
                          onValueChange = { email = it },
                          label = { Text("Email") })

                      // Password input
                      OutlinedTextField(
                          modifier = Modifier.fillMaxWidth().testTag("signUpPassword"),
                          value = password,
                          onValueChange = {
                            password = it
                            passwordErrorMessage = validatePassword(password)
                          },
                          label = { Text("Password") },
                          visualTransformation =
                              if (passwordVisible) VisualTransformation.None
                              else PasswordVisualTransformation(),
                          trailingIcon = {
                            val image =
                                if (passwordVisible) Icons.Outlined.Visibility
                                else Icons.Outlined.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                              Icon(
                                  imageVector = image,
                                  contentDescription =
                                      if (passwordVisible) "Hide password" else "Show password")
                            }
                          })

                      // Password error message
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
                      Text(
                          modifier = Modifier.testTag("signUpConfirmText"),
                          text = "Confirm your password",
                          style =
                              MaterialTheme.typography.bodyLarge.copy(
                                  fontWeight = FontWeight.Medium))

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
                      Button(
                          modifier = Modifier.wrapContentSize().testTag("signUpButton"),
                          onClick = {
                            confirmErrorMessage = validateConfirmPassword(password, confirm)
                            if (passwordErrorMessage.isEmpty() && confirmErrorMessage.isEmpty()) {
                              if (email.isNotEmpty() && password.isNotEmpty()) {
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
                                Toast.makeText(
                                        context,
                                        "Email and Password cannot be empty",
                                        Toast.LENGTH_SHORT)
                                    .show()
                              }
                            }
                          },
                          colors = ButtonDefaults.buttonColors(containerColor = Purple40),
                          shape = RoundedCornerShape(50)) {
                            Text(
                                text = "Sign up",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium)
                          }
                    }
              }
        }
      })
}

fun validatePassword(password: String): String {
    val capitalLetter = Regex(".*[A-Z].*")
    val minusculeLetter = Regex(".*[a-z].*")
    val number = Regex(".*[0-9].*")
    val specialChar = Regex(".*[!@#\$%^&*(),.?\":{}|<>].*")

    return when {
        password.isEmpty() -> "Password cannot be empty"
        password.length < 8 -> "Password must be at least 8 characters long"
        !capitalLetter.containsMatchIn(password) -> "Password must contain at least one capital letter"
        !minusculeLetter.containsMatchIn(password) -> "Password must contain at least one minuscule letter"
        !number.containsMatchIn(password) -> "Password must contain at least one number"
        !specialChar.containsMatchIn(password) -> "Password must contain at least one special character"
        else -> ""
    }
}

fun validateConfirmPassword(password: String, confirm: String): String {
  return if (password != confirm) "Passwords do not match" else ""
}
