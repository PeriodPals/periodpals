package com.android.periodpals.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.periodpals.R
import com.android.periodpals.resources.C.Tag.EditProfileScreen
import com.android.periodpals.ui.components.ERROR_INVALID_DATE
import com.android.periodpals.ui.components.ERROR_INVALID_DESCRIPTION
import com.android.periodpals.ui.components.ERROR_INVALID_NAME
import com.android.periodpals.ui.components.MANDATORY_TEXT
import com.android.periodpals.ui.components.PROFILE_TEXT
import com.android.periodpals.ui.components.ProfileInputDescription
import com.android.periodpals.ui.components.ProfileInputDob
import com.android.periodpals.ui.components.ProfileInputName
import com.android.periodpals.ui.components.ProfilePicture
import com.android.periodpals.ui.components.ProfileSaveButton
import com.android.periodpals.ui.components.ProfileSection
import com.android.periodpals.ui.components.TOAST_SUCCESS
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi

private const val SCREEN_TITLE = "Edit Your Profile"

/* Placeholder Screen, waiting for implementation */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EditProfileScreen(navigationActions: NavigationActions) {
  // State variables, to remplace it with the real data
  var name by remember { mutableStateOf("Emilia Jones") }
  var dob by remember { mutableStateOf("20/01/2001") }
  var description by remember {
    mutableStateOf(
        "Hello guys :) I’m Emilia, I’m a student " +
            "at EPFL and I’m here to participate and contribute to this amazing community !")
  }

  var profileImageUri by remember {
    mutableStateOf<Uri?>(
        Uri.parse("android.resource://com.android.periodpals/" + R.drawable.generic_avatar))
  }

  val launcher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
              profileImageUri = result.data?.data
            }
          }

  val context = LocalContext.current

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(EditProfileScreen.SCREEN),
      topBar = {
        TopAppBar(
            title = SCREEN_TITLE,
            true,
            onBackButtonClick = { navigationActions.navigateTo(Screen.PROFILE) })
      },
      content = { pd ->
        Column(
            modifier = Modifier.padding(pd).padding(24.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        ) {
          // Profile image and its edit icon
          Box(modifier = Modifier.size(190.dp)) {
            ProfilePicture(
                profileImageUri,
                testTag = EditProfileScreen.PROFILE_PICTURE,
            )

            IconButton(
                onClick = {
                  val pickImageIntent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
                  launcher.launch(pickImageIntent)
                },
                colors =
                    IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary,
                    ),
                modifier =
                    Modifier.align(Alignment.TopEnd)
                        .size(40.dp)
                        .testTag(EditProfileScreen.EDIT_ICON),
            ) {
              Icon(
                  imageVector = Icons.Outlined.Edit,
                  contentDescription = "edit icon",
              )
            }
          }

          // Section title
          ProfileSection(MANDATORY_TEXT, EditProfileScreen.MANDATORY_SECTION)

          // Name input field
          ProfileInputName(
              name = name,
              onValueChange = { name = it },
              testTag = EditProfileScreen.NAME_FIELD,
          )

          // Date of Birth input field
          ProfileInputDob(
              dob = dob,
              onValueChange = { dob = it },
              testTag = EditProfileScreen.DOB_FIELD,
          )

          // Your profile section
          ProfileSection(PROFILE_TEXT, EditProfileScreen.YOUR_PROFILE_SECTION)

          // Description input field
          ProfileInputDescription(
              description = description,
              onValueChange = { description = it },
              testTag = EditProfileScreen.DESCRIPTION_FIELD,
          )

          // Save Changes button
          ProfileSaveButton(
              onClick = {
                val errorMessage = validateFields(name, dob, description)
                if (errorMessage != null) {
                  Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                } else {
                  // TODO: Save the profile (future implementation)
                  Toast.makeText(context, TOAST_SUCCESS, Toast.LENGTH_SHORT).show()
                  navigationActions.navigateTo(Screen.PROFILE)
                }
              },
              testTag = EditProfileScreen.SAVE_BUTTON,
          )
        }
      })
}

/** Validates the fields of the profile screen. */
private fun validateFields(name: String, dob: String, description: String): String? {
  return when {
    name.isEmpty() -> ERROR_INVALID_NAME
    !validateDate(dob) -> ERROR_INVALID_DATE
    description.isEmpty() -> ERROR_INVALID_DESCRIPTION
    else -> null
  }
}
