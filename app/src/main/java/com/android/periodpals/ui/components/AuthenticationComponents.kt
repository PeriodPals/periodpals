package com.android.periodpals.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.resources.C.Tag.AuthenticationScreens
import com.android.periodpals.ui.theme.Pink40
import com.android.periodpals.ui.theme.Purple40
import com.android.periodpals.ui.theme.Purple80
import com.android.periodpals.ui.theme.PurpleGrey80
import com.android.periodpals.ui.theme.dimens

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
          Modifier.fillMaxSize().background(background).testTag(AuthenticationScreens.BACKGROUND)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val gradientBrush =
              Brush.verticalGradient(
                  colors = listOf(gradeFrom, gradeTo),
                  startY = 0f,
                  endY = size.height * 2 / 3,
              )

          drawRect(brush = gradientBrush, size = Size(size.width, size.height / 2))

          drawArc(
              brush = gradientBrush,
              startAngle = 0f,
              sweepAngle = 180f,
              useCenter = true,
              topLeft = Offset(0f, size.height / 3),
              size = Size(size.width, size.height / 3),
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
          Modifier.fillMaxWidth().wrapContentHeight().testTag(AuthenticationScreens.WELCOME_TEXT),
      text = text,
      textAlign = TextAlign.Center,
      color = color,
      style = MaterialTheme.typography.titleLarge,
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
  var isFocused by remember { mutableStateOf(false) }

  OutlinedTextField(
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .testTag(AuthenticationScreens.EMAIL_FIELD)
              .onFocusEvent { focusState -> isFocused = focusState.isFocused },
      value = email,
      onValueChange = onEmailChange,
      textStyle = MaterialTheme.typography.labelLarge,
      label = {
        Text(
            text = "Email",
            style =
                if (isFocused || email.isNotEmpty()) MaterialTheme.typography.labelMedium
                else MaterialTheme.typography.labelLarge,
        )
      },
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
  var isFocused by remember { mutableStateOf(false) }

  OutlinedTextField(
      modifier =
          Modifier.fillMaxWidth().wrapContentHeight().testTag(testTag).onFocusEvent { focusState ->
            isFocused = focusState.isFocused
          },
      value = password,
      onValueChange = onPasswordChange,
      textStyle = MaterialTheme.typography.labelLarge,
      label = {
        Text(
            "Password",
            style =
                if (isFocused || password.isNotEmpty()) MaterialTheme.typography.labelMedium
                else MaterialTheme.typography.labelLarge,
        )
      },
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
              modifier = Modifier.size(MaterialTheme.dimens.iconSize),
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
    Text(text = text, color = Color.White, style = MaterialTheme.typography.bodyMedium)
  }
}
