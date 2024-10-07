package com.android.sample.ui.authentication

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.sample.R
import com.android.sample.ui.theme.Pink80
import com.android.sample.ui.theme.Purple80
import com.android.sample.ui.theme.PurpleGrey80

// TODO : describe function and implement navigation action and supabase
@Preview
@Composable
fun SignInScreen() {
  val context = LocalContext.current // TODO: Toasts messages on failure or success
  //  val launcher = //TODO: Implement Supabase Auth
  //  val token = //TODO : Implement Supabase Auth
  var username by remember { mutableStateOf("") } // TODO: Implement Supabase retrieve username
  var password by remember { mutableStateOf("") } // TODO: Implement Supabase retrieve password
  var passwordVisible by remember { mutableStateOf(false) }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      content = { paddding ->
        // Background gradient
        GradedBackground()
        // TODO: Add app logo

        Column(
            modifier = Modifier.fillMaxSize().padding(paddding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
          // Welcome text
          Text(
              modifier = Modifier.testTag("loginTitle"),
              text = "Welcome to PeriodPals",
              textAlign = TextAlign.Center,
              style =
                  MaterialTheme.typography.headlineLarge.copy(
                      fontSize = 40.sp, lineHeight = 64.sp, fontWeight = FontWeight.Bold))
          Spacer(modifier = Modifier.padding(16.dp))

          // Rectangle with login fields and button
          Box(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(16.dp)
                      .border(1.dp, Color.Gray, RectangleShape)
                      .background(Color.White)
                      .padding(16.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                      // Login instruction
                      Text(
                          modifier = Modifier.testTag("loginInstruction"),
                          text = "Sign in to your account",
                          style =
                              MaterialTheme.typography.bodyMedium.copy(
                                  fontSize = 20.sp, fontWeight = FontWeight.Medium))

                      // Google sign in button
                      GoogleSignInButton { /* TODO : with google sign in */}

                      // Other option text
                      Text(
                          modifier = Modifier.testTag("loginOr"),
                          text = "or continue with",
                          style =
                              MaterialTheme.typography.bodyMedium.copy(
                                  fontSize = 16.sp, fontWeight = FontWeight.Medium))

                      // Username field
                      OutlinedTextField(
                          value = "",
                          onValueChange = { username = it }, // TODO : with supabase
                          label = { Text("Username") },
                          modifier = Modifier.fillMaxWidth().testTag("loginUsername"))

                      // Password field
                      OutlinedTextField(
                          value = "",
                          onValueChange = { password = it }, // TODO : with supabase
                          label = { Text("Password") },
                          visualTransformation =
                              if (passwordVisible) VisualTransformation.None
                              else PasswordVisualTransformation(),
                          trailingIcon = {
                            val icon =
                                if (passwordVisible) "\uD83D\uDC41\uFE0F"
                                else "\uD83D\uDC41\u200D\uD83D\uDDE8"
                            Text(
                                text = icon,
                                modifier =
                                    Modifier.testTag("loginPasswordVisibility")
                                        .padding(8.dp)
                                        .clickable { passwordVisible = !passwordVisible })
                          },
                          modifier = Modifier.fillMaxWidth().testTag("loginPassword"))

                      // Login button
                      Button(
                          onClick = {
                            if (username.isNotEmpty() && password.isNotEmpty()) {
                              // TODO: Implement username and password login logic
                              val loginSuccess = true // Replace with actual login logic
                              if (loginSuccess) {
                                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT)
                                    .show()
                              } else {
                                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                              }
                            } else {
                              Toast.makeText(
                                      context,
                                      "Username and Password cannot be empty",
                                      Toast.LENGTH_SHORT)
                                  .show()
                            }
                          },
                          colors = ButtonDefaults.buttonColors(containerColor = PurpleGrey80),
                          shape = RoundedCornerShape(50),
                          modifier =
                              Modifier.padding(8.dp)
                                  .height(48.dp)
                                  .wrapContentWidth()
                                  .testTag("loginButton")) {
                            Text(
                                text = "Login",
                                color = Color.Black,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium)
                          }
                    }
              }
        }
      })
}

@Preview
@Composable
private fun GradedBackground() {
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .height(800.dp)
              .background(Color.Transparent)
              .testTag("loginBackground")) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val gradientBrush =
              Brush.verticalGradient(
                  colors = listOf(Purple80, Pink80), startY = 0f, endY = size.minDimension * 3 / 2)

          // Draw the square purple box
          drawRect(
              brush = gradientBrush,
              topLeft = Offset((size.width - size.minDimension) / 2, 0f),
              size = Size(size.width, size.minDimension))

          // Draw the filled purple arc below the square box
          drawArc(
              brush = gradientBrush,
              startAngle = 0f,
              sweepAngle = 180f,
              useCenter = true,
              topLeft = Offset(0f, size.minDimension / 2),
              size = Size(size.width, size.minDimension)) // Adjusted height of the arc
        }
      }
}

// TODO: if no google, remove logo from resources
@Composable
private fun GoogleSignInButton(onSignInClick: () -> Unit) {
  Button(
      onClick = onSignInClick,
      colors = ButtonDefaults.buttonColors(containerColor = Color.White),
      shape = RoundedCornerShape(50),
      border = BorderStroke(1.dp, Color.LightGray),
      modifier =
          Modifier.padding(8.dp).height(48.dp).wrapContentWidth().testTag("loginGoogleButton"),
  ) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
          // Load the Google logo from resources
          Image(
              painter = painterResource(id = R.drawable.google_logo),
              contentDescription = "Google Logo",
              modifier = Modifier.size(30.dp).padding(end = 8.dp))

          // Text for the button
          Text(
              text = "Sign in with Google",
              color = Color.DarkGray,
              fontSize = 16.sp,
              fontWeight = FontWeight.Medium)
        }
  }
}
