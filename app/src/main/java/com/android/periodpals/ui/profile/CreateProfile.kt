package com.android.periodpals.ui.profile

import android.app.Activity
import android.content.Intent
import android.icu.util.GregorianCalendar
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.periodpals.R
import com.android.periodpals.resources.C.Tag.CreateProfileScreen
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

private const val SCREEN_TITLE = "Create Your Account"

/**
 * Composable function for the Create Profile screen.
 *
 * @param navigationActions Actions to handle navigation events.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CreateProfileScreen(navigationActions: NavigationActions) {
  var name by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var age by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }

  var profileImageUri by remember {
    mutableStateOf<Uri?>(
        Uri.parse("android.resource://com.android.periodpals/" + R.drawable.generic_avatar))
  }
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
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          // Profile picture
          Box(
              modifier =
                  Modifier.size(124.dp)
                      .clip(shape = RoundedCornerShape(100.dp))
                      .background(
                          color = MaterialTheme.colorScheme.background,
                          shape = RoundedCornerShape(100.dp),
                      )
                      .testTag(CreateProfileScreen.PROFILE_PICTURE)
                      .clickable {
                        val pickImageIntent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
                        launcher.launch(pickImageIntent)
                      }) {
                GlideImage(
                    model = profileImageUri,
                    contentDescription = "profile picture",
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier.size(124.dp)
                            .background(
                                color = MaterialTheme.colorScheme.background, shape = CircleShape),
                )
              }

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
              modifier =
                  Modifier.align(Alignment.Start).testTag(CreateProfileScreen.MANDATORY_TEXT))
          // Email field
          OutlinedTextField(
              value = email,
              onValueChange = { email = it },
              label = { Text("Email") },
              placeholder = { Text("Enter your email") },
              modifier = Modifier.testTag(CreateProfileScreen.EMAIL_FIELD),
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
              modifier = Modifier.align(Alignment.Start).testTag(CreateProfileScreen.PROFILE_TEXT))

          // Name field
          OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("Displayed Name") },
              placeholder = { Text("Enter your name") },
              modifier = Modifier.testTag(CreateProfileScreen.NAME_FIELD),
          )
          // Description field
          OutlinedTextField(
              value = description,
              onValueChange = { description = it },
              label = { Text("Description") },
              placeholder = { Text("Enter a description") },
              modifier = Modifier.height(124.dp).testTag(CreateProfileScreen.DESCRIPTION_FIELD),
          )
          // Save button
          Button(
              onClick = {
                val errorMessage = validateFields(email, name, age, description)
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
                  Modifier.width(84.dp)
                      .height(40.dp)
                      .testTag(CreateProfileScreen.SAVE_BUTTON)
                      .background(
                          color = Color(0xFF65558F), shape = RoundedCornerShape(size = 100.dp)),
              colors = ButtonDefaults.buttonColors(Color(0xFF65558F)),
          ) {
            Text("Save", color = Color.White)
          }
        }
      },
  )
}

/**
 * Validates the fields of the profile screen.
 *
 * @param email The email address entered by the user.
 * @param name The name entered by the user.
 * @param dob The date of birth entered by the user.
 * @param description The description entered by the user.
 * @return An error message if validation fails, otherwise null.
 */
private fun validateFields(
    email: String,
    name: String,
    dob: String,
    description: String,
): String? {
  return when {
    email.isEmpty() -> "Please enter an email"
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
