package com.android.periodpals.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.android.periodpals.R
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.C.Tag.ProfileScreens.EditProfileScreen
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
import com.android.periodpals.ui.theme.dimens

private const val SCREEN_TITLE = "Edit Your Profile"
private const val TAG = "EditProfile"
private const val DEFAULT_NAME = "Error loading name, try again later."
private const val DEFAULT_DOB = "Error loading date of birth, try again later."
private const val DEFAULT_DESCRIPTION = "Error loading description, try again later."
private val DEFAULT_PROFILE_PICTURE =
    Uri.parse("android.resource://com.android.periodpals/${R.drawable.generic_avatar}")

/**
 * A composable function that displays the Edit Profile screen, where users can edit their profile
 * information.
 *
 * This screen includes the user's profile picture, name, date of birth, and description. It also
 * includes a save button to save the changes and a top app bar with a back button.
 *
 * @param navigationActions The navigation actions that can be performed in the app.
 *
 * TODO: Replace the state variables with the real data when implementing profile VM.
 */
@Composable
fun EditProfileScreen(userViewModel: UserViewModel, navigationActions: NavigationActions) {
  // TODO: State variables, to replace it with the real data when the implementing profile VM

  val context = LocalContext.current
  userViewModel.loadUser()
  val userState = userViewModel.user
  if (userState.value == null) {
    Log.d(TAG, "User data is null")
    Toast.makeText(context, "Error loading your data! Try again later.", Toast.LENGTH_SHORT).show()
  }

  var name = userState.value?.name ?: DEFAULT_NAME
  var dob = userState.value?.dob ?: DEFAULT_DOB
  var description = userState.value?.description ?: DEFAULT_DESCRIPTION

  var profileImageUri = userState.value?.imageUrl ?: DEFAULT_PROFILE_PICTURE

  val launcher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
              profileImageUri = result.data?.data
            }
          }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(EditProfileScreen.SCREEN),
      topBar = {
        TopAppBar(
            title = SCREEN_TITLE,
            true,
            onBackButtonClick = { navigationActions.navigateTo(Screen.PROFILE) })
      },
      content = { pd ->
        LazyColumn(
            modifier =
                Modifier.fillMaxSize()
                    .padding(pd)
                    .padding(
                        horizontal = MaterialTheme.dimens.medium3,
                        vertical = MaterialTheme.dimens.small3),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterVertically),
        ) {
          // Profile image and its edit icon
          item {
            Box(modifier = Modifier.size(MaterialTheme.dimens.profilePictureSize)) {
              ProfilePicture(profileImageUri)

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
                          .size(MaterialTheme.dimens.iconButtonSize)
                          .testTag(EditProfileScreen.EDIT_PROFILE_PICTURE),
              ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "edit icon",
                    modifier = Modifier.align(Alignment.Center).size(MaterialTheme.dimens.iconSize),
                )
              }
            }
          }

          // Mandatory section title
          item { ProfileSection(MANDATORY_TEXT, ProfileScreens.MANDATORY_SECTION) }

          // Name input field
          item { ProfileInputName(name = name, onValueChange = { name = it }) }

          // Date of Birth input field
          item { ProfileInputDob(dob = dob, onValueChange = { dob = it }) }

          // Your profile section title
          item { ProfileSection(PROFILE_TEXT, ProfileScreens.YOUR_PROFILE_SECTION) }

          // Description input field
          item {
            ProfileInputDescription(description = description, onValueChange = { description = it })
          }

          // Save Changes button
          item {
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
            )
          }
        }
      })
}

/**
 * Validates the fields of the profile screen.
 *
 * @param name The name of the user.
 * @param dob The date of birth of the user.
 * @param description The description of the user.
 */
private fun validateFields(name: String, dob: String, description: String): String? {
  return when {
    name.isEmpty() -> ERROR_INVALID_NAME
    !validateDate(dob) -> ERROR_INVALID_DATE
    description.isEmpty() -> ERROR_INVALID_DESCRIPTION
    else -> null
  }
}
