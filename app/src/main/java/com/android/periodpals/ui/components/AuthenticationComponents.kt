package com.android.periodpals.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.text.style.TextDecoration
import com.android.periodpals.resources.C.Tag.AuthenticationScreens
import com.android.periodpals.resources.ComponentColor.getFilledPrimaryContainerButtonColors
import com.android.periodpals.resources.ComponentColor.getOutlinedTextFieldColors
import com.android.periodpals.ui.navigation.NavigationActions
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
    gradeFrom: Color = MaterialTheme.colorScheme.tertiaryContainer,
    gradeTo: Color = MaterialTheme.colorScheme.tertiary,
    background: Color = MaterialTheme.colorScheme.secondaryContainer,
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
 */
@Composable
fun AuthenticationWelcomeText(text: String = "Welcome to PeriodPals") {
  Text(
      modifier =
          Modifier.fillMaxWidth().wrapContentHeight().testTag(AuthenticationScreens.WELCOME_TEXT),
      text = text,
      color = MaterialTheme.colorScheme.onTertiaryContainer,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.titleLarge,
  )
}

/**
 * A composable function that displays a card with authentication content.
 *
 * The card is a rounded rectangle with a shadow. The content is displayed inside the card.
 *
 * @param content The content to display inside the card.
 */
@Composable
fun AuthenticationCard(
    content: @Composable () -> Unit,
) {
  Card(
      modifier = Modifier.fillMaxWidth().wrapContentHeight(),
      shape = RoundedCornerShape(size = MaterialTheme.dimens.cardRoundedSize),
      colors =
          CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
              contentColor = MaterialTheme.colorScheme.onSurface,
          ),
      elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.dimens.cardElevation),
  ) {
    Column(
        modifier =
            Modifier.fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    horizontal = MaterialTheme.dimens.medium1,
                    vertical = MaterialTheme.dimens.small3,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterVertically),
    ) {
      content()
    }
  }
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
      colors = getOutlinedTextFieldColors(),
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
      colors = getOutlinedTextFieldColors(),
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
      colors = getFilledPrimaryContainerButtonColors(),
  ) {
    Text(
        text = text,
        modifier = Modifier.wrapContentSize(),
        style = MaterialTheme.typography.bodyMedium,
    )
  }
}

/**
 * A composable function that displays a navigation row between authentication screens.
 *
 * @param questionText The question text to display.
 * @param navigateToText The text for the navigation link.
 * @param screen The screen to navigate to when the link is clicked.
 * @param testTag The test tag for the navigation link.
 * @param navigationActions The navigation actions to handle screen navigation.
 */
@Composable
fun NavigateBetweenAuthScreens(
    questionText: String,
    navigateToText: String,
    screen: String,
    testTag: String,
    navigationActions: NavigationActions
) {
  Row(
      modifier = Modifier.fillMaxWidth().wrapContentHeight(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
        modifier = Modifier.wrapContentSize(),
        text = questionText,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        style = MaterialTheme.typography.bodyMedium,
    )

    Text(
        modifier =
            Modifier.wrapContentSize()
                .clickable { navigationActions.navigateTo(screen) }
                .testTag(testTag),
        text = navigateToText,
        textDecoration = TextDecoration.Underline,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        style = MaterialTheme.typography.bodyMedium,
    )
  }
}
