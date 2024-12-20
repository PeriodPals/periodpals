package com.android.periodpals.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.android.periodpals.R
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.C.Tag.ProfileScreens.EditProfileScreen
import com.android.periodpals.resources.ComponentColor.getFilledIconButtonColors
import com.android.periodpals.ui.components.MANDATORY_TEXT
import com.android.periodpals.ui.components.PROFILE_TEXT
import com.android.periodpals.ui.components.ProfileInputDescription
import com.android.periodpals.ui.components.ProfileInputDob
import com.android.periodpals.ui.components.ProfileInputName
import com.android.periodpals.ui.components.ProfilePicture
import com.android.periodpals.ui.components.ProfileSaveButton
import com.android.periodpals.ui.components.ProfileSection
import com.android.periodpals.ui.components.uriToByteArray
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens
import com.dsc.form_builder.TextFieldState

private val DEFAULT_PROFILE_PICTURE =
    "android.resource://com.android.periodpals/${R.drawable.generic_avatar}"

/**
 * A composable function that displays the Edit Profile screen, where users can edit their profile
 * information.
 *
 * This screen includes the user's profile picture, name, date of birth, and description. It also
 * includes a save button to save the changes and a top app bar with a back button.
 *
 * @param userViewModel The ViewModel that handles user data.
 * @param navigationActions The navigation actions that can be performed in the app.
 */
@Composable
fun EditProfileScreen(userViewModel: UserViewModel, navigationActions: NavigationActions) {
  val context = LocalContext.current

  val userState = userViewModel.user
  val userAvatar = userViewModel.avatar

  val formState = remember { userViewModel.formState }
  formState.reset()

  val nameState = formState.getState<TextFieldState>(UserViewModel.NAME_STATE_NAME)
  val dobState = formState.getState<TextFieldState>(UserViewModel.DOB_STATE_NAME)
  val descriptionState = formState.getState<TextFieldState>(UserViewModel.DESCRIPTION_STATE_NAME)
  val profileImageState = formState.getState<TextFieldState>(UserViewModel.PROFILE_IMAGE_STATE_NAME)

  userState.value?.let {
    nameState.change(it.name)
    dobState.change(it.dob)
    descriptionState.change(it.description)
    profileImageState.change(it.imageUrl)
  }

  var userAvatarState by remember {
    mutableStateOf(userAvatar.value ?: Uri.parse(DEFAULT_PROFILE_PICTURE).uriToByteArray(context))
  }

  val launcher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if ((result.resultCode == Activity.RESULT_OK) && (result.data != null)) {
              profileImageState.change(result.data!!.data.toString())
              userAvatarState = result.data!!.data?.uriToByteArray(context)
            }
          }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(EditProfileScreen.SCREEN),
      topBar = {
        TopAppBar(
            title = context.getString(R.string.edit_profile_screen_title),
            true,
            onBackButtonClick = { navigationActions.navigateTo(Screen.PROFILE) },
        )
      },
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
  ) { paddingValues ->
    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(
                    horizontal = MaterialTheme.dimens.medium3,
                    vertical = MaterialTheme.dimens.small3,
                )
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterVertically),
    ) {
      // Profile image and its edit icon
      Box(modifier = Modifier.size(MaterialTheme.dimens.profilePictureSize)) {
        ProfilePicture(userAvatarState)

        // Edit profile picture icon button
        IconButton(
            onClick = {
              val pickImageIntent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
              launcher.launch(pickImageIntent)
            },
            modifier =
                Modifier.align(Alignment.TopEnd)
                    .size(MaterialTheme.dimens.iconButtonSize)
                    .testTag(EditProfileScreen.EDIT_PROFILE_PICTURE),
            colors = getFilledIconButtonColors(),
        ) {
          Icon(
              imageVector = Icons.Outlined.Edit,
              contentDescription = "edit icon",
              modifier = Modifier.align(Alignment.Center).size(MaterialTheme.dimens.iconSize),
          )
        }
      }

      // Mandatory section title
      ProfileSection(MANDATORY_TEXT, ProfileScreens.MANDATORY_SECTION)

      // Name input field
      ProfileInputName(name = nameState.value, onValueChange = { nameState.change(it) })

      // Date of Birth input field
      ProfileInputDob(dob = dobState.value, onValueChange = { dobState.change(it) })

      // Your profile section title
      ProfileSection(PROFILE_TEXT, ProfileScreens.YOUR_PROFILE_SECTION)

      // Description input field
      ProfileInputDescription(
          description = descriptionState.value,
          onValueChange = { descriptionState.change(it) },
      )

      ProfileSaveButton(
          nameState,
          dobState,
          descriptionState,
          profileImageState,
          userAvatarState,
          userViewModel.user.value?.preferredDistance,
          context,
          userViewModel,
          navigationActions,
      )
    }
  }
}
