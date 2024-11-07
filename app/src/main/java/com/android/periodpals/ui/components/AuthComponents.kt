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
import com.android.periodpals.ui.theme.Purple40
import com.android.periodpals.ui.theme.dimens

/**
 * A composable that displays a graded background with [gradeFrom] and [gradeTo] colors and
 * [background] color and [testTag] for testing purposes.
 */
@Composable
fun GradedBackground(gradeFrom: Color, gradeTo: Color, background: Color, testTag: String) {
  Box(modifier = Modifier.fillMaxSize().background(background).testTag(testTag)) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val gradientBrush =
          Brush.verticalGradient(
              colors = listOf(gradeFrom, gradeTo), startY = 0f, endY = size.height * 2 / 3)

      drawRect(brush = gradientBrush, size = Size(size.width, size.height / 2))

      drawArc(
          brush = gradientBrush,
          startAngle = 0f,
          sweepAngle = 180f,
          useCenter = true,
          topLeft = Offset(0f, size.height / 3),
          size = Size(size.width, size.height / 3))
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
      modifier = Modifier.fillMaxWidth().wrapContentHeight().testTag(testTag),
      text = text,
      textAlign = TextAlign.Center,
      color = color,
      style = MaterialTheme.typography.headlineLarge)
}

/**
 * A composable that displays an instruction text with [text] and [testTag] for testing purposes.
 */
@Composable
fun AuthInstruction(text: String, testTag: String) {
  Text(
      modifier = Modifier.fillMaxWidth().wrapContentHeight().testTag(testTag),
      text = text,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.bodyLarge)
}

/**
 * A composable that displays a second instruction text with [text] and [testTag] for testing
 * purposes.
 */
@Composable
fun AuthSecondInstruction(text: String, testTag: String) {
  Text(
      modifier = Modifier.fillMaxWidth().wrapContentHeight().testTag(testTag),
      text = text,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.bodyLarge)
}

/**
 * A composable that displays an email input with [email] and [onEmailChange] action and [testTag]
 */
@Composable
fun AuthEmailInput(email: String, onEmailChange: (String) -> Unit, testTag: String) {
  var isFocused by remember { mutableStateOf(false) }

  OutlinedTextField(
      modifier =
          Modifier.fillMaxWidth().wrapContentHeight().testTag(testTag).onFocusEvent { focusState ->
            isFocused = focusState.isFocused
          },
      value = email,
      onValueChange = onEmailChange,
      textStyle = MaterialTheme.typography.labelLarge,
      label = {
        Text(
            text = "Email",
            style =
                if (isFocused || email.isNotEmpty()) MaterialTheme.typography.labelMedium
                else MaterialTheme.typography.labelLarge)
      })
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
    testTag: String,
    visibilityTestTag: String
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
                else MaterialTheme.typography.labelLarge)
      },
      visualTransformation =
          if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
      trailingIcon = {
        val image = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
        IconButton(
            onClick = onPasswordVisibilityChange, modifier = Modifier.testTag(visibilityTestTag)) {
              Icon(
                  imageVector = image,
                  contentDescription = if (passwordVisible) "Hide password" else "Show password",
                  modifier = Modifier.size(MaterialTheme.dimens.iconSize))
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
        Text(text = text, color = Color.White, style = MaterialTheme.typography.bodyMedium)
      }
}
