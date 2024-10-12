package com.android.periodpals.ui.authentication

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.periodpals.R
import com.android.periodpals.ui.theme.Pink40
import com.android.periodpals.ui.theme.Purple40
import com.android.periodpals.ui.theme.Purple80
import com.android.periodpals.ui.theme.PurpleGrey80

@Preview
@Composable
fun SignInScreen() {
  val context = LocalContext.current
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }

  // Screen
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      content = { padding ->
        // Purple-ish background
        GradedBackground(Purple80, Pink40, PurpleGrey80, "signInBackground")

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(64.dp, Alignment.CenterVertically),
        ) {
          // Welcome text
          AuthWelcomeText(
              text = "Welcome to PeriodPals", color = Color.Black, testTag = "signInTitle")

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
                      // Sign in instruction
                      AuthInstruction(
                          text = "Sign in to your account", testTag = "signInInstruction")

                      // Email input
                      AuthEmailInput(
                          email = email, onEmailChange = { email = it }, testTag = "signInEmail")

                      // Password input
                      AuthPasswordInput(
                          password = password,
                          onPasswordChange = { password = it },
                          passwordVisible = passwordVisible,
                          onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                          testTag = "signInPassword")

                      // Sign in button
                      AuthButton(
                          text = "Sign in",
                          onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                              // TODO: Implement email and password login logic
                              val loginSuccess = true
                              if (loginSuccess) {
                                // with supabase
                                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT)
                                    .show()
                              } else {
                                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                              }
                            } else {
                              Toast.makeText(
                                      context,
                                      "Email and Password cannot be empty",
                                      Toast.LENGTH_SHORT)
                                  .show()
                            }
                          },
                          testTag = "signInButton")

                      // Or continue with text
                      AuthSecondInstruction(text = "Or continue with", testTag = "signInOrText")

                      // Google sign in button
                      Button(
                          modifier = Modifier.wrapContentSize().testTag("signInGoogleButton"),
                          onClick = {
                            Toast.makeText(
                                    context, "Use other login method for now", Toast.LENGTH_SHORT)
                                .show()
                          },
                          colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                          shape = RoundedCornerShape(50),
                          border = BorderStroke(1.dp, Color.LightGray)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center) {
                                  Image(
                                      painter = painterResource(id = R.drawable.google_logo),
                                      contentDescription = "Google Logo",
                                      modifier = Modifier.size(24.dp))
                                  Spacer(modifier = Modifier.size(8.dp))
                                  Text(
                                      text = "Sign in with Google",
                                      color = Color.Black,
                                      fontWeight = FontWeight.Medium,
                                      style = MaterialTheme.typography.bodyMedium)
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
                                ?.let {
                                  /* TODO: Implement navigation action */
                                  Toast.makeText(
                                          context,
                                          "Yay! I'm waiting for navigation",
                                          Toast.LENGTH_SHORT)
                                      .show()
                                }
                          })
                    }
              }
        }
      })
}

/** Composable functions for the sign-in and sign-up screen */

/**
 * A composable that displays a graded background with [gradeFrom] and [gradeTo] colors and
 * [background] color and [testTag] for testing purposes.
 */
@Composable
fun GradedBackground(gradeFrom: Color, gradeTo: Color, background: Color, testTag: String) {
  Box(modifier = Modifier.fillMaxSize().background(Color.Transparent).testTag(testTag)) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val gradientBrush =
          Brush.verticalGradient(
              colors = listOf(gradeFrom, gradeTo), startY = 0f, endY = size.minDimension * 3 / 2)

      drawRect(
          color = background,
          topLeft = Offset(0f, size.minDimension),
          size = Size(size.width, size.height - size.minDimension))

      drawRect(
          brush = gradientBrush,
          topLeft = Offset((size.width - size.minDimension) / 2, 0f),
          size = Size(size.width, size.minDimension))

      drawArc(
          brush = gradientBrush,
          startAngle = 0f,
          sweepAngle = 180f,
          useCenter = true,
          topLeft = Offset(0f, size.minDimension / 2),
          size = Size(size.width, size.minDimension))
    }
  }
}

/**
 * A composable that displays a welcome text with [text] and [color] and [testTag] for testing
 * purposes.
 */
@Composable
fun AuthWelcomeText(text: String, color: Color, testTag: String) {
  Text(
      modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).testTag(testTag),
      text = text,
      textAlign = TextAlign.Center,
      color = color,
      style =
          MaterialTheme.typography.headlineLarge.copy(
              fontSize = 40.sp, lineHeight = 64.sp, fontWeight = FontWeight.SemiBold))
}

/**
 * A composable that displays an instruction text with [text] and [testTag] for testing purposes.
 */
@Composable
fun AuthInstruction(text: String, testTag: String) {
  Text(
      modifier = Modifier.testTag(testTag),
      text = text,
      style =
          MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium))
}

/**
 * A composable that displays a second instruction text with [text] and [testTag] for testing
 * purposes.
 */
@Composable
fun AuthSecondInstruction(text: String, testTag: String) {
  Text(
      modifier = Modifier.testTag(testTag),
      text = text,
      style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
}

/**
 * A composable that displays an email input with [email] and [onEmailChange] action and [testTag]
 */
@Composable
fun AuthEmailInput(email: String, onEmailChange: (String) -> Unit, testTag: String) {
  OutlinedTextField(
      modifier = Modifier.fillMaxWidth().wrapContentSize().testTag(testTag),
      value = email,
      onValueChange = onEmailChange,
      label = { Text("Email") })
}

/**
 * A composable that displays a password input with [password], [onPasswordChange] action,
 * [passwordVisible] and [onPasswordVisibilityChange] action and [testTag] for testing purposes.
 */
@Composable
fun AuthPasswordInput(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    testTag: String
) {
  OutlinedTextField(
      modifier = Modifier.fillMaxWidth().testTag(testTag),
      value = password,
      onValueChange = onPasswordChange,
      label = { Text("Password") },
      visualTransformation =
          if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
      trailingIcon = {
        val image = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
        IconButton(onClick = onPasswordVisibilityChange) {
          Icon(
              imageVector = image,
              contentDescription = if (passwordVisible) "Hide password" else "Show password")
        }
      })
}

/**
 * A composable that displays an authentication button with [text] and [onClick] action and
 * [testTag] for testing purposes.
 */
@Composable
fun AuthButton(text: String, onClick: () -> Unit, testTag: String) {
  Button(
      modifier = Modifier.wrapContentSize().testTag(testTag),
      onClick = onClick,
      colors = ButtonDefaults.buttonColors(containerColor = Purple40),
      shape = RoundedCornerShape(50)) {
        Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
      }
}

/**
 * A composable that displays a Google sign-in button with [text] and [onClick] action and [testTag]
 * for testing purposes.
 */
@Composable
private fun GoogleButton(text: String, onClick: () -> Unit, testTag: String) {
  Button(
      modifier = Modifier.wrapContentSize().testTag(testTag),
      onClick = onClick,
      colors = ButtonDefaults.buttonColors(containerColor = Color.White),
      shape = RoundedCornerShape(50),
      border = BorderStroke(1.dp, Color.LightGray)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
              Image(
                  painter = painterResource(id = R.drawable.google_logo),
                  contentDescription = "Google Logo",
                  modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.size(8.dp))
              Text(
                  text = text,
                  color = Color.Black,
                  fontWeight = FontWeight.Medium,
                  style = MaterialTheme.typography.bodyMedium)
            }
      }
}
