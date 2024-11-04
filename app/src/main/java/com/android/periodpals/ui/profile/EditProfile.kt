package com.android.periodpals.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import com.android.periodpals.R
import com.android.periodpals.resources.C.Tag.EditProfileScreen.ADD_CIRCLE_ICON
import com.android.periodpals.resources.C.Tag.EditProfileScreen.DESCRIPTION_FIELD
import com.android.periodpals.resources.C.Tag.EditProfileScreen.DOB_FIELD
import com.android.periodpals.resources.C.Tag.EditProfileScreen.EDIT_PROFILE_SCREEN
import com.android.periodpals.resources.C.Tag.EditProfileScreen.EMAIL_FIELD
import com.android.periodpals.resources.C.Tag.EditProfileScreen.NAME_FIELD
import com.android.periodpals.resources.C.Tag.EditProfileScreen.PROFILE_PICTURE
import com.android.periodpals.resources.C.Tag.EditProfileScreen.SAVE_BUTTON
import com.android.periodpals.ui.components.ProfileDescriptionInput
import com.android.periodpals.ui.components.ProfileText
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EditProfileScreen(navigationActions: NavigationActions) {
  // State variables, to remplace it with the real data
  var email by remember { mutableStateOf("emilia.jones@email.com") }
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
      modifier = Modifier.fillMaxSize().testTag(EDIT_PROFILE_SCREEN),
      topBar = {
        TopAppBar(
            title = "Edit your Profile",
            true,
            onBackButtonClick = { navigationActions.navigateTo(Screen.PROFILE) })
      },
      content = { pd ->
        Column(
            modifier = Modifier.padding(pd).padding(24.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          // Profile image section
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Box {
              GlideImage(
                  model = profileImageUri,
                  modifier =
                      Modifier.padding(1.dp)
                          .clip(shape = RoundedCornerShape(100.dp))
                          .size(124.dp)
                          .testTag(PROFILE_PICTURE)
                          .background(
                              color = MaterialTheme.colorScheme.background,
                              shape = RoundedCornerShape(100.dp)),
                  contentDescription = "image profile",
                  contentScale = ContentScale.Crop,
              )

              Icon(
                  Icons.Filled.AddCircleOutline,
                  contentDescription = "add circle",
                  modifier =
                      Modifier.align(Alignment.TopEnd)
                          .size(40.dp)
                          .background(color = Color(0xFF79747E), shape = CircleShape)
                          .testTag(ADD_CIRCLE_ICON)
                          .clickable {
                            val pickImageIntent =
                                Intent(Intent.ACTION_PICK).apply { type = "image/*" }
                            launcher.launch(pickImageIntent)
                          },
              )
            }
          }

          // Section title

          ProfileText("Mandatory Fields", "MANDATORY_FIELDS")

          // Divider
          HorizontalDivider(thickness = 2.dp)

          // Email input field
          OutlinedTextField(
              value = email,
              onValueChange = { email = it },
              label = { Text("Email") },
              placeholder = { Text("Enter your email") },
              modifier = Modifier.testTag(EMAIL_FIELD))

          // Name input field
          OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("Name") },
              placeholder = { Text("Enter your name") },
              modifier = Modifier.testTag(NAME_FIELD))

          // Date of Birth input field
          OutlinedTextField(
              value = dob,
              onValueChange = { dob = it },
              label = { Text("Date of Birth") },
              placeholder = { Text("DD/MM/YYYY") },
              modifier = Modifier.testTag(DOB_FIELD),
          )

          // Section title
          ProfileText("Your Profile: ", "YOUR_PROFILE")

          // Divider
          HorizontalDivider(thickness = 2.dp)

          // Description input field
          ProfileDescriptionInput(description, { description = it }, DESCRIPTION_FIELD)

          // Save Changes button
          Button(
              onClick = {
                val errorMessage = validateFields(name, dob, description, email)
                if (errorMessage != null) {
                  Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                } else {
                  // Save the profile (future implementation)
                  Toast.makeText(context, "Profile saved", Toast.LENGTH_SHORT).show()
                  navigationActions.navigateTo(Screen.PROFILE)
                }
              },
              enabled = true,
              modifier =
                  Modifier.padding(1.dp)
                      .testTag(SAVE_BUTTON)
                      .align(Alignment.CenterHorizontally)
                      .background(
                          color = Color(0xFFD9D9D9), shape = RoundedCornerShape(size = 100.dp)),
              colors = ButtonDefaults.buttonColors(Color(0xFFD9D9D9)),
          ) {
            Text("Save Changes")
          }
        }
      })
}

/** Validates the fields of the profile screen. */
private fun validateFields(
    name: String,
    date: String,
    description: String,
    email: String
): String? {
  return when {
    name.isEmpty() -> "Please enter a name"
    !validateDate(date) -> "Invalid date"
    description.isEmpty() -> "Please enter a description"
    email.isEmpty() -> "Please enter an email"
    else -> null
  }
}
