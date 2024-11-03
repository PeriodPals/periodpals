package com.android.periodpals.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.periodpals.resources.C.Tag.AuthenticationScreens
import com.android.periodpals.ui.theme.Pink40
import com.android.periodpals.ui.theme.Purple40
import com.android.periodpals.ui.theme.Purple80
import com.android.periodpals.ui.theme.PurpleGrey80

/**
 * A composable function that displays a graded background with a gradient.
 *
 * @param gradeFrom The starting color of the gradient.
 * @param gradeTo The ending color of the gradient.
 * @param background The background color.
 */
@Composable
fun GradedBackground(
    gradeFrom: Color = Purple80,
    gradeTo: Color = Pink40,
    background: Color = PurpleGrey80,
) {
  Box(
      modifier =
          Modifier.fillMaxSize()
              .background(Color.Transparent)
              .testTag(AuthenticationScreens.BACKGROUND)) {
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
 * A composable function that displays a welcome text.
 *
 * @param text The welcome text to display.
 * @param color The color of the text.
 */
@Composable
fun AuthenticationWelcomeText(text: String = "Welcome to PeriodPals", color: Color = Color.Black) {
  Text(
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 16.dp)
              .testTag(AuthenticationScreens.WELCOME_TEXT),
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
 * A composable function that displays an email input field with error message handling.
 *
 * @param email The current email value.
 * @param onEmailChange A callback to handle changes to the email input.
 * @param emailErrorMessage The error message to display if the email is invalid.
 */
@Composable
fun AuthenticationEmailInput(
    email: String,
    onEmailChange: (String) -> Unit,
    emailErrorMessage: String,
) {
  OutlinedTextField(
      modifier =
          Modifier.fillMaxWidth().wrapContentSize().testTag(AuthenticationScreens.EMAIL_FIELD),
      value = email,
      onValueChange = onEmailChange,
      label = { Text("Email") },
  )
  if (emailErrorMessage.isNotEmpty()) {
    ErrorText(message = emailErrorMessage, testTag = AuthenticationScreens.EMAIL_ERROR_TEXT)
  }
}

/**
 * A composable function that displays a password input field with visibility toggle and error
 * message handling. By default, it uses the test tags for the password field and visibility toggle.
 *
 * @param password The current password value.
 * @param onPasswordChange A callback to handle changes to the password input.
 * @param passwordVisible A boolean indicating whether the password is visible.
 * @param onPasswordVisibilityChange A callback to handle the visibility toggle of the password.
 * @param passwordErrorMessage The error message to display if the password is invalid.
 * @param testTag The test tag for the password input field.
 * @param visibilityTestTag The test tag for the password visibility toggle.
 */
@Composable
fun AuthenticationPasswordInput(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    passwordErrorMessage: String,
    testTag: String = AuthenticationScreens.PASSWORD_FIELD,
    passwordErrorTestTag: String = AuthenticationScreens.PASSWORD_ERROR_TEXT,
    visibilityTestTag: String = AuthenticationScreens.PASSWORD_VISIBILITY_BUTTON,
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
    ErrorText(passwordErrorMessage, passwordErrorTestTag)
  }
}

/**
 * A composable function that displays a submit button.
 *
 * @param text The text to display on the button.
 * @param onClick A callback to handle the button click.
 * @param testTag The test tag for the button.
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
