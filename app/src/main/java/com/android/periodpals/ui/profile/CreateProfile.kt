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
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

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
  var context = LocalContext.current

  val launcher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
              profileImageUri = result.data?.data
            }
          }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("createProfileScreen"),
      content = { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Box(
              modifier =
                  Modifier.size(124.dp)
                      .clip(shape = RoundedCornerShape(100.dp))
                      .background(
                          color = MaterialTheme.colorScheme.background,
                          shape = RoundedCornerShape(100.dp),
                      )
                      .testTag("profile_image")
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

          Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Mandatory",
                style =
                    TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(500),
                        letterSpacing = 0.2.sp,
                    ),
            )
          }

          OutlinedTextField(
              value = email,
              onValueChange = { email = it },
              label = { Text("Email") },
              placeholder = { Text("Enter your email") },
              modifier = Modifier.testTag("email_field"),
          )

          OutlinedTextField(
              value = age,
              onValueChange = { age = it },
              label = { Text("Date of Birth") },
              placeholder = { Text("DD/MM/YYYY") },
              modifier = Modifier.testTag("dob_field"),
          )

          Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Your profile",
                style =
                    TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(500),
                        letterSpacing = 0.2.sp,
                    ),
            )
          }

          OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("Displayed Name") },
              placeholder = { Text("Enter your name") },
              modifier = Modifier.testTag("name_field"),
          )

          OutlinedTextField(
              value = description,
              onValueChange = { description = it },
              label = { Text("Description") },
              placeholder = { Text("Enter a description") },
              modifier = Modifier.height(124.dp).testTag("description_field"),
          )

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
                  Modifier.padding(0.dp)
                      .width(84.dp)
                      .height(40.dp)
                      .testTag("save_button")
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

/** Validates the fields of the profile screen. */
private fun validateFields(
    email: String,
    name: String,
    date: String,
    description: String
): String? {
  return when {
    email.isEmpty() -> "Please enter an email"
    name.isEmpty() -> "Please enter a name"
    !validateDate(date) -> "Invalid date"
    description.isEmpty() -> "Please enter a description"
    else -> null
  }
}

/** Validates the date is in the format DD/MM/YYYY and is a valid date. */
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
