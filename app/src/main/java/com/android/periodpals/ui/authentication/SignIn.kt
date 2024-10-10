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
        GradedBackground(Purple80, Pink40, PurpleGrey80)

        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(64.dp, Alignment.CenterVertically),
        ) {
          // Welcome text
          Text(
              modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).testTag("signInTitle"),
              text = "Welcome to PeriodPals",
              textAlign = TextAlign.Center,
              color = Color.Black,
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
                      // Sign in instruction
                      Text(
                          modifier = Modifier.testTag("signInInstruction"),
                          text = "Sign in to your account",
                          style =
                              MaterialTheme.typography.bodyLarge.copy(
                                  fontSize = 20.sp, fontWeight = FontWeight.Medium))

                      // Email input
                      OutlinedTextField(
                          modifier =
                              Modifier.fillMaxWidth().wrapContentSize().testTag("signInEmail"),
                          value = email,
                          onValueChange = { email = it },
                          label = { Text("Email") })

                      // Password input
                      OutlinedTextField(
                          modifier = Modifier.fillMaxWidth().testTag("signInPassword"),
                          value = password,
                          onValueChange = { password = it },
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

                      // Sign in button
                      Button(
                          modifier = Modifier.wrapContentSize().testTag("signInButton"),
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
                          colors = ButtonDefaults.buttonColors(containerColor = Purple40),
                          shape = RoundedCornerShape(50)) {
                            Text(
                                text = "Sign in",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium)
                          }

                      // Or continue with text
                      Text(
                          modifier = Modifier.testTag("signInOrText"),
                          text = "Or continue with",
                          style =
                              MaterialTheme.typography.bodyLarge.copy(
                                  fontWeight = FontWeight.Medium))

                      // Google sign in button
                      GoogleButton(
                          text = "Sign in with Google",
                          onClick = {
                            /* TODO : with google sign in */
                            Toast.makeText(
                                    context, "Use other login method for now", Toast.LENGTH_SHORT)
                                .show()
                          })

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

@Composable
fun GradedBackground(gradeFrom: Color, gradeTo: Color, background: Color) {
  Box(modifier = Modifier.fillMaxSize().background(Color.Transparent).testTag("authBackground")) {
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

@Composable
fun GoogleButton(text: String, onClick: () -> Unit) {
  Button(
      modifier = Modifier.wrapContentSize().testTag("signInGoogleButton"),
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
