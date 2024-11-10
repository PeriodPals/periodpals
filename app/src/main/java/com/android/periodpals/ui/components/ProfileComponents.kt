package com.android.periodpals.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

/** Shared constants for the profile screen. */
const val MANDATORY_TEXT = "Mandatory"
const val NAME_LABEL = "Name"
const val NAME_PLACEHOLDER = "Enter your name"
const val DOB_LABEL = "Date of Birth"
const val DOB_PLACEHOLDER = "DD/MM/YYYY"
const val PROFILE_TEXT = "Your Profile"
const val DESCRIPTION_LABEL = "Description"
const val DESCRIPTION_PLACEHOLDER = "Describe yourself"
const val SAVE_BUTTON_TEXT = "Save"
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
fun ProfilePicture(
    profileImageUri: Uri?,
    onClick: (() -> Unit)? = null,
    testTag: String
) {
    GlideImage(
        model = profileImageUri,
        contentDescription = "profile picture",
        modifier = Modifier
            .size(190.dp)
            .clip(shape = CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .testTag(testTag),
    )
}
/**
 * A composable that displays an instruction text with [text] and [testTag] for testing purposes.
 */
@Composable
fun ProfileSection(text: String, testTag: String) {
  Text(
      modifier = Modifier.fillMaxWidth().testTag(testTag),
      text = text,
      textAlign = TextAlign.Start,
      style =
          MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.Medium))
}
