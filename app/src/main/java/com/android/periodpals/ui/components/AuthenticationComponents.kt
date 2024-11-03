package com.android.periodpals.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.periodpals.R
import com.android.periodpals.resources.C.Tag.SignInScreen
import com.android.periodpals.ui.theme.Purple40

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
          colors = listOf(gradeFrom, gradeTo),
          startY = 0f,
          endY = size.minDimension * 3 / 2,
        )

      drawRect(
        color = background,
        topLeft = Offset(0f, size.minDimension),
        size = Size(size.width, size.height - size.minDimension),
      )

      drawRect(
        brush = gradientBrush,
        topLeft = Offset((size.width - size.minDimension) / 2, 0f),
        size = Size(size.width, size.minDimension),
      )

      drawArc(
        brush = gradientBrush,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(0f, size.minDimension / 2),
        size = Size(size.width, size.minDimension),
      )
    }
  }
}

/**
 * A composable that displays a welcome text with [text] and [color] and [testTag] for testing
 * purposes.
 */
@Composable
fun AuthenticationWelcomeText(text: String, color: Color, testTag: String) {
  Text(
    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).testTag(testTag),
    text = text,
    textAlign = TextAlign.Center,
    color = color,
    style =
      MaterialTheme.typography.headlineLarge.copy(
        fontSize = 40.sp,
        lineHeight = 64.sp,
        fontWeight = FontWeight.SemiBold,
      ),
  )
}

/**
 * A composable that displays an email input with [email] and [onEmailChange] action and [testTag]
 */
@Composable
fun AuthenticationEmailInput(
  email: String,
  onEmailChange: (String) -> Unit,
  emailErrorMessage: String,
  testTag: String,
) {
  OutlinedTextField(
    modifier = Modifier.fillMaxWidth().wrapContentSize().testTag(testTag),
    value = email,
    onValueChange = onEmailChange,
    label = { Text("Email") },
  )
  if (emailErrorMessage.isNotEmpty()) {
    ErrorText(message = emailErrorMessage, testTag = SignInScreen.EMAIL_ERROR_TEXT)
  }
}

/**
 * A composable that displays a password input with [password], [onPasswordChange] action,
 * [passwordVisible] and [onPasswordVisibilityChange] action and [testTag] for testing purposes.
 */
@Composable
fun AuthenticationPasswordInput(
  password: String,
  onPasswordChange: (String) -> Unit,
  passwordVisible: Boolean,
  onPasswordVisibilityChange: () -> Unit,
  passwordErrorMessage: String,
  testTag: String,
  visibilityTestTag: String,
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
      IconButton(
        onClick = onPasswordVisibilityChange,
        modifier = Modifier.testTag(visibilityTestTag),
      ) {
        Icon(
          imageVector = image,
          contentDescription = if (passwordVisible) "Hide password" else "Show password",
        )
      }
    },
  )
  if (passwordErrorMessage.isNotEmpty()) {
    ErrorText(passwordErrorMessage, SignInScreen.PASSWORD_ERROR_TEXT)
  }
}

/**
 * A composable that displays an authentication button with [text] and [onClick] action and
 * [testTag] for testing purposes.
 */
@Composable
fun AuthenticationSubmitButton(text: String, onClick: () -> Unit, testTag: String) {
  Button(
    modifier = Modifier.wrapContentSize().testTag(testTag),
    onClick = onClick,
    colors = ButtonDefaults.buttonColors(containerColor = Purple40),
    shape = RoundedCornerShape(50),
  ) {
    Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
  }
}

/**
 * A composable that displays a Google sign in button.
 *
 * @param onClick The action to perform when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param testTag The test tag for the button.
 */
@Composable
fun AuthenticationGoogleButton(context: Context, modifier: Modifier = Modifier) {
  Button(
    modifier = modifier.wrapContentSize().testTag(SignInScreen.GOOGLE_BUTTON),
    onClick = {
      // TODO: implement Google sign in
      Toast.makeText(context, "Use other login method for now, thanks!", Toast.LENGTH_SHORT).show()
    },
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
