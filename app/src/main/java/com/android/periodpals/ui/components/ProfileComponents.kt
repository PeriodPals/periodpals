package com.android.periodpals.ui.components

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.ComponentColor.getFilledPrimaryContainerButtonColors
import com.android.periodpals.resources.ComponentColor.getOutlinedTextFieldColors
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.theme.dimens
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.dsc.form_builder.TextFieldState

/** Shared constants for the profile screen. */
const val MANDATORY_TEXT = "Mandatory"
private const val NAME_LABEL = "Name"
private const val NAME_PLACEHOLDER = "Enter your name"
private const val DOB_LABEL = "Date of Birth"
private const val DOB_PLACEHOLDER = "DD/MM/YYYY"
const val PROFILE_TEXT = "Your Profile"
private const val DESCRIPTION_LABEL = "Description"
private const val DESCRIPTION_PLACEHOLDER = "Describe yourself"
private const val SAVE_BUTTON_TEXT = "Save"
const val LOG_TAG = "CreateProfileScreen"
const val LOG_FAILURE = "Failed to save profile"
const val LOG_SAVING_PROFILE = "Saving user profile"
const val LOG_SUCCESS = "Profile saved"
const val TOAST_FAILURE = "Failed to save profile"
const val TOAST_SUCCESS = "Profile saved"

/** A composable that displays a profile picture with [model] and [testTag] for testing purposes. */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfilePicture(model: Any?, onClick: (() -> Unit)? = null) {
  GlideImage(
      model = model,
      contentDescription = "profile picture",
      contentScale = ContentScale.Crop,
      modifier =
          Modifier.size(MaterialTheme.dimens.profilePictureSize)
              .clip(shape = CircleShape)
              .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
              .testTag(ProfileScreens.PROFILE_PICTURE),
  )
}

/**
 * A composable function that displays a section's text in the profile screen.
 *
 * @param text The section's text to be displayed.
 * @param testTag A tag used for testing purposes.
 */
@Composable
fun ProfileSection(text: String, testTag: String) {
  Text(
      modifier =
          Modifier.fillMaxWidth()
              .padding(top = MaterialTheme.dimens.small2)
              .wrapContentHeight()
              .testTag(testTag),
      text = text,
      color = MaterialTheme.colorScheme.onSurface,
      textAlign = TextAlign.Start,
      style = MaterialTheme.typography.titleSmall,
  )
}

/**
 * A composable function that displays an outlined text field for the name input.
 *
 * @param name The current value of the name input.
 * @param onValueChange A lambda function to handle changes to the name input.
 */
@Composable
fun ProfileInputName(name: String, onValueChange: (String) -> Unit) {
  var isFocused by remember { mutableStateOf(false) }
  OutlinedTextField(
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .testTag(ProfileScreens.NAME_INPUT_FIELD)
              .onFocusEvent { focusState -> isFocused = focusState.isFocused },
      value = name,
      onValueChange = onValueChange,
      textStyle = MaterialTheme.typography.labelLarge,
      label = {
        Text(
            text = NAME_LABEL,
            style =
                if (isFocused || name.isNotEmpty()) MaterialTheme.typography.labelMedium
                else MaterialTheme.typography.labelLarge,
        )
      },
      placeholder = { Text(text = NAME_PLACEHOLDER, style = MaterialTheme.typography.labelLarge) },
      colors = getOutlinedTextFieldColors(),
  )
}

/**
 * A composable function that displays an outlined text field for the date of birth input.
 *
 * @param dob The current value of the date of birth input.
 * @param onValueChange A lambda function to handle changes to the date of birth input.
 */
@Composable
fun ProfileInputDob(dob: String, onValueChange: (String) -> Unit) {
  var isFocused by remember { mutableStateOf(false) }
  OutlinedTextField(
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .testTag(ProfileScreens.DOB_INPUT_FIELD)
              .onFocusEvent { focusState -> isFocused = focusState.isFocused },
      value = dob,
      onValueChange = onValueChange,
      textStyle = MaterialTheme.typography.labelLarge,
      label = {
        Text(
            text = DOB_LABEL,
            style =
                if (isFocused || dob.isNotEmpty()) MaterialTheme.typography.labelMedium
                else MaterialTheme.typography.labelLarge,
        )
      },
      placeholder = { Text(text = DOB_PLACEHOLDER, style = MaterialTheme.typography.labelLarge) },
  )
}

/**
 * A composable function that displays an outlined text field for the description input.
 *
 * @param description The current value of the description input.
 * @param onValueChange A lambda function to handle changes to the description input.
 */
@Composable
fun ProfileInputDescription(description: String, onValueChange: (String) -> Unit) {
  var isFocused by remember { mutableStateOf(false) }
  OutlinedTextField(
      modifier =
          Modifier.fillMaxWidth()
              .wrapContentHeight()
              .testTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
              .onFocusEvent { focusState -> isFocused = focusState.isFocused },
      value = description,
      onValueChange = onValueChange,
      textStyle = MaterialTheme.typography.labelLarge,
      label = {
        Text(
            text = DESCRIPTION_LABEL,
            style =
                if (isFocused || description.isNotEmpty()) MaterialTheme.typography.labelMedium
                else MaterialTheme.typography.labelLarge,
        )
      },
      placeholder = {
        Text(text = DESCRIPTION_PLACEHOLDER, style = MaterialTheme.typography.labelLarge)
      },
      minLines = 3,
  )
}

/**
 * A composable function that displays a save button and attempts to save the user data
 *
 * @param nameState The name entered by the user.
 * @param dobState The date of birth entered by the user.
 * @param descriptionState The description entered by the user.
 * @param profileImageState The URI of the profile image selected by the user.
 * @param context The context used to show Toast messages.
 * @param userViewModel The ViewModel that handles user data.
 * @param navigationActions The navigation actions to navigate between screens.
 */
@Composable
fun ProfileSaveButton(
    nameState: TextFieldState,
    dobState: TextFieldState,
    descriptionState: TextFieldState,
    profileImageState: TextFieldState,
    context: Context,
    userViewModel: UserViewModel,
    navigationActions: NavigationActions,
) {

  Button(
      modifier = Modifier.wrapContentSize().testTag(ProfileScreens.SAVE_BUTTON),
      onClick = {
        val errorMessage =
            when {
              !dobState.validate() -> dobState.errorMessage
              !nameState.validate() -> nameState.errorMessage
              !descriptionState.validate() -> descriptionState.errorMessage
              else -> null
            }
        if (errorMessage != null) {
          Log.d(LOG_TAG, "$LOG_FAILURE: $errorMessage")
          Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
          return@Button
        }

        Log.d(LOG_TAG, LOG_SAVING_PROFILE)
        val newUser =
            User(
                name = nameState.value,
                dob = dobState.value,
                description = descriptionState.value,
                imageUrl = profileImageState.value,
            )
        userViewModel.saveUser(
            user = newUser,
            onSuccess = {
              Handler(Looper.getMainLooper()).post { // used to show the Toast on the main thread
                Toast.makeText(context, TOAST_SUCCESS, Toast.LENGTH_SHORT).show()
              }
              Log.d(LOG_TAG, LOG_SUCCESS)
              navigationActions.navigateTo(Screen.PROFILE)
            },
            onFailure = {
              Handler(Looper.getMainLooper()).post { // used to show the Toast on the main thread
                Toast.makeText(context, TOAST_FAILURE, Toast.LENGTH_SHORT).show()
              }
              Log.d(LOG_TAG, LOG_FAILURE)
            },
        )
      },
      colors = getFilledPrimaryContainerButtonColors(),
  ) {
    Text(text = SAVE_BUTTON_TEXT, style = MaterialTheme.typography.bodyMedium)
  }
}
