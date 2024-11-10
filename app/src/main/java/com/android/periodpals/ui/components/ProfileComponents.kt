package com.android.periodpals.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

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
const val ERROR_INVALID_DATE = "Invalid date"
const val ERROR_INVALID_NAME = "Please enter a name"
const val ERROR_INVALID_DESCRIPTION = "Please enter a description"

/**
 * A composable that displays a profile picture with [profileImageUri] and [testTag] for testing
 * purposes.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfilePicture(profileImageUri: Uri?, onClick: (() -> Unit)? = null) {
  GlideImage(
      model = profileImageUri,
      contentDescription = "profile picture",
      modifier =
          Modifier.size(190.dp)
              .clip(shape = CircleShape)
              .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
              .testTag(ProfileScreens.PROFILE_PICTURE))
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
      modifier = Modifier.fillMaxWidth().testTag(testTag),
      text = text,
      textAlign = TextAlign.Start,
      style =
          MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium),
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
  OutlinedTextField(
      modifier = Modifier.testTag(ProfileScreens.NAME_INPUT_FIELD),
      value = name,
      onValueChange = onValueChange,
      label = { Text(NAME_LABEL) },
      placeholder = { Text(NAME_PLACEHOLDER) })
}

/**
 * A composable function that displays an outlined text field for the date of birth input.
 *
 * @param dob The current value of the date of birth input.
 * @param onValueChange A lambda function to handle changes to the date of birth input.
 */
@Composable
fun ProfileInputDob(dob: String, onValueChange: (String) -> Unit) {
  OutlinedTextField(
      value = dob,
      onValueChange = onValueChange,
      label = { Text(DOB_LABEL) },
      placeholder = { Text(DOB_PLACEHOLDER) },
      modifier = Modifier.testTag(ProfileScreens.DOB_INPUT_FIELD))
}

/**
 * A composable function that displays an outlined text field for the description input.
 *
 * @param description The current value of the description input.
 * @param onValueChange A lambda function to handle changes to the description input.
 */
@Composable
fun ProfileInputDescription(description: String, onValueChange: (String) -> Unit) {
  OutlinedTextField(
      value = description,
      onValueChange = onValueChange,
      label = { Text(DESCRIPTION_LABEL) },
      placeholder = { Text(DESCRIPTION_PLACEHOLDER) },
      minLines = 3,
      modifier = Modifier.testTag(ProfileScreens.DESCRIPTION_INPUT_FIELD))
}

/**
 * A composable function that displays a save button with [onClick] as the action to be executed
 * when the button is clicked.
 *
 * @param onClick The action to be executed when the button is clicked.
 */
@Composable
fun ProfileSaveButton(onClick: () -> Unit) {
  Button(
      onClick = onClick,
      enabled = true,
      modifier = Modifier.wrapContentSize().testTag(ProfileScreens.SAVE_BUTTON),
  ) {
    Text(SAVE_BUTTON_TEXT)
  }
}
