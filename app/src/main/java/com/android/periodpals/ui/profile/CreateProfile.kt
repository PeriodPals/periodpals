package com.android.periodpals.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.icu.util.GregorianCalendar
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.android.periodpals.R
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.C.Tag.ProfileScreens.CreateProfileScreen
import com.android.periodpals.ui.components.ERROR_INVALID_DATE
import com.android.periodpals.ui.components.ERROR_INVALID_DESCRIPTION
import com.android.periodpals.ui.components.ERROR_INVALID_NAME
import com.android.periodpals.ui.components.LOG_FAILURE
import com.android.periodpals.ui.components.LOG_SAVING_PROFILE
import com.android.periodpals.ui.components.LOG_SUCCESS
import com.android.periodpals.ui.components.LOG_TAG
import com.android.periodpals.ui.components.MANDATORY_TEXT
import com.android.periodpals.ui.components.PROFILE_TEXT
import com.android.periodpals.ui.components.ProfileInputDescription
import com.android.periodpals.ui.components.ProfileInputDob
import com.android.periodpals.ui.components.ProfileInputName
import com.android.periodpals.ui.components.ProfilePicture
import com.android.periodpals.ui.components.ProfileSaveButton
import com.android.periodpals.ui.components.ProfileSection
import com.android.periodpals.ui.components.TOAST_FAILURE
import com.android.periodpals.ui.components.TOAST_SUCCESS
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi

private const val SCREEN_TITLE = "Create Your Account"

/**
 * Composable function for the Create Profile screen.
 *
 * @param navigationActions Actions to handle navigation events.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CreateProfileScreen(userViewModel: UserViewModel, navigationActions: NavigationActions) {
  var name by remember { mutableStateOf("") }
  var age by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var profileImageUri by remember {
    mutableStateOf<Uri?>(
        Uri.parse("android.resource://com.android.periodpals/" + R.drawable.generic_avatar))
  }
  val userState = userViewModel.user
  val context = LocalContext.current

  val launcher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
              profileImageUri = result.data?.data
            }
          }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(CreateProfileScreen.SCREEN),
      topBar = { TopAppBar(title = SCREEN_TITLE) },
  ) { padding ->
    LazyColumn(
        modifier =
            Modifier.fillMaxSize()
                .padding(padding)
                .padding(
                    horizontal = MaterialTheme.dimens.medium3,
                    vertical = MaterialTheme.dimens.small3),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterVertically),
    ) {
      // Profile picture
      item {
        ProfilePicture(
            model = profileImageUri,
            onClick = {
              val pickImageIntent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
              launcher.launch(pickImageIntent)
            },
        )
      }

      // Mandatory section title
      item {
        ProfileSection(
            text = MANDATORY_TEXT,
            testTag = ProfileScreens.MANDATORY_SECTION,
        )
      }

      // Name field
      item { ProfileInputName(name = name, onValueChange = { name = it }) }

      // Date of birth field
      item { ProfileInputDob(dob = age, onValueChange = { age = it }) }

      // Your profile section title
      item {
        ProfileSection(
            text = PROFILE_TEXT,
            testTag = ProfileScreens.YOUR_PROFILE_SECTION,
        )
      }

      // Description field
      item {
        ProfileInputDescription(description = description, onValueChange = { description = it })
      }

      // Save button
      item {
        ProfileSaveButton(
            onClick = {
              attemptSaveUserData(
                  name = name,
                  age = age,
                  description = description,
                  profileImageUri = profileImageUri,
                  context = context,
                  userViewModel = userViewModel,
                  userState = userState,
                  navigationActions = navigationActions,
              )
            },
        )
      }
    }
  }
}

/**
 * Attempts to save the user data entered in the Create Profile screen.
 *
 * @param name The name entered by the user.
 * @param age The date of birth entered by the user.
 * @param description The description entered by the user.
 * @param context The context used to show Toast messages.
 * @param profileImageUri The URI of the profile image selected by the user.
 * @param userViewModel The ViewModel that handles user data.
 * @param userState The current state of the user.
 * @param navigationActions The navigation actions to navigate between screens.
 */
private fun attemptSaveUserData(
    name: String,
    age: String,
    description: String,
    profileImageUri: Uri?,
    context: Context,
    userViewModel: UserViewModel,
    userState: State<User?>,
    navigationActions: NavigationActions,
) {
  val errorMessage = validateFields(name, age, description)
  if (errorMessage != null) {
    Log.d(LOG_TAG, "$LOG_FAILURE: $errorMessage")
    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    return
  }

  Log.d(LOG_TAG, LOG_SAVING_PROFILE)
  val newUser =
      User(name = name, dob = age, description = description, imageUrl = profileImageUri.toString())
  userViewModel.saveUser(newUser)
  if (userState.value == null) {
    Log.d(LOG_TAG, LOG_FAILURE)
    Toast.makeText(context, TOAST_FAILURE, Toast.LENGTH_SHORT).show()
    return
  }

  Log.d(LOG_TAG, LOG_SUCCESS)
  Toast.makeText(context, TOAST_SUCCESS, Toast.LENGTH_SHORT).show()
  navigationActions.navigateTo(Screen.PROFILE)
}

/**
 * Validates the fields of the profile screen.
 *
 * @param name The name entered by the user.
 * @param dob The date of birth entered by the user.
 * @param description The description entered by the user.
 * @return An error message if validation fails, otherwise null.
 */
private fun validateFields(name: String, dob: String, description: String): String? {
  return when {
    !validateDate(dob) -> ERROR_INVALID_DATE
    name.isEmpty() -> ERROR_INVALID_NAME
    description.isEmpty() -> ERROR_INVALID_DESCRIPTION
    else -> null
  }
}

/**
 * Validates the date is in the format DD/MM/YYYY and is a valid date.
 *
 * @param date The date string to validate.
 * @return True if the date is valid, otherwise false.
 */
fun validateDate(date: String): Boolean {
  val parts = date.split("/")
  val calendar = GregorianCalendar.getInstance()
  calendar.isLenient = false
  if (parts.size == 3) {
    return try {
      calendar.set(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
      calendar.time
      true
    } catch (e: Exception) {
      false
    }
  }
  return false
}
