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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.periodpals.R
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.CreateProfileScreen
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

private const val SCREEN_TITLE = "Create Your Account"

private const val TAG = "CreateProfileScreen"

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
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).padding(padding),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Profile picture
      GlideImage(
          model = profileImageUri,
          contentDescription = "profile picture",
          contentScale = ContentScale.Crop,
          modifier =
              Modifier.size(190.dp)
                  .clip(shape = CircleShape)
                  .testTag(CreateProfileScreen.PROFILE_PICTURE)
                  .clickable {
                    val pickImageIntent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
                    launcher.launch(pickImageIntent)
                  })

      // Mandatory fields
      Text(
          text = "Mandatory",
          style =
              TextStyle(
                  fontSize = 20.sp,
                  lineHeight = 20.sp,
                  fontWeight = FontWeight(500),
                  letterSpacing = 0.2.sp,
              ),
          modifier = Modifier.align(Alignment.Start).testTag(CreateProfileScreen.MANDATORY_TEXT),
      )
      // Name field
      OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Name") },
          placeholder = { Text("Enter your name") },
          modifier = Modifier.testTag(CreateProfileScreen.NAME_FIELD),
      )

      // Date of birth field
      OutlinedTextField(
          value = age,
          onValueChange = { age = it },
          label = { Text("Date of Birth") },
          placeholder = { Text("DD/MM/YYYY") },
          modifier = Modifier.testTag(CreateProfileScreen.DOB_FIELD),
      )

      // Profile field
      Text(
          text = "Your profile",
          style =
              TextStyle(
                  fontSize = 20.sp,
                  lineHeight = 20.sp,
                  fontWeight = FontWeight(500),
                  letterSpacing = 0.2.sp,
              ),
          modifier = Modifier.align(Alignment.Start).testTag(CreateProfileScreen.PROFILE_TEXT),
      )

      // Description field
      OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description") },
          placeholder = { Text("Describe yourself") },
          modifier = Modifier.height(124.dp).testTag(CreateProfileScreen.DESCRIPTION_FIELD),
      )

      // Save button
      Button(
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
          enabled = true,
          modifier =
              Modifier.wrapContentSize()
                  .testTag(CreateProfileScreen.SAVE_BUTTON)
                  .background(color = Color(0xFF65558F), shape = RoundedCornerShape(size = 100.dp)),
          colors = ButtonDefaults.buttonColors(Color(0xFF65558F)),
      ) {
        Text("Save", color = Color.White)
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
    Log.d(TAG, "Failed to save user profile: $errorMessage")
    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    return
  }

  Log.d(TAG, "Saving user profile")
  val newUser =
      User(name = name, dob = age, description = description, imageUrl = profileImageUri.toString())
  userViewModel.saveUser(newUser)
  if (userState.value == null) {
    Log.d(TAG, "Failed to save profile")
    Toast.makeText(context, "Failed to save profile", Toast.LENGTH_SHORT).show()
    return
  }

  Log.d(TAG, "Profile saved")
  Toast.makeText(context, "Profile saved", Toast.LENGTH_SHORT).show()
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
    !validateDate(dob) -> "Invalid date"
    name.isEmpty() -> "Please enter a name"
    description.isEmpty() -> "Please enter a description"
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
