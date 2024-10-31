package com.android.periodpals.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EditProfileScreen(navigationActions: NavigationActions) {
  // State variables to hold the input values
  var name by remember { mutableStateOf("") }
  var dob by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }

  var profileImageUri by remember { mutableStateOf<Uri?>(Uri.parse("")) }

  val launcher =
      rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
              profileImageUri = result.data?.data
            }
          }

  val context = LocalContext.current

  Scaffold(
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      topBar = {
        TopAppBar(
            title = "Edit your Profile",
            true,
            onBackButtonClick = { navigationActions.navigateTo("profile") })
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
                          .testTag("profile_image")
                          .background(
                              color = Color(0xFFD9D9D9), shape = RoundedCornerShape(100.dp)),
                  contentDescription = "image profile",
                  contentScale = ContentScale.Crop,
              )

              Icon(
                  Icons.Filled.AddCircleOutline,
                  contentDescription = "add circle",
                  modifier =
                      Modifier.align(Alignment.BottomEnd)
                          .size(40.dp)
                          .background(color = Color(0xFF79747E), shape = CircleShape)
                          .testTag("add_circle_icon")
                          .clickable {
                            val pickImageIntent =
                                Intent(Intent.ACTION_PICK).apply { type = "image/*" }
                            launcher.launch(pickImageIntent)
                          },
              )
            }
          }

          // Section title
          ProfileText("Mandatory Fields")

          // Divider
          HorizontalDivider(thickness = 2.dp)

          // Email row
          Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.testTag("email_row")) {
            ProfileText("Email: ")
            ProfileText(
                "emilia.jones@email.com",
                TextStyle(fontWeight = FontWeight(400), textDecoration = TextDecoration.Underline))
          }

          // Name input field
          ProfileField(
              title = "Name: ",
              value = name,
              onValueChange = { name = it },
              modifier = Modifier.testTag("name_field"))

          // Date of Birth input field
          ProfileField(
              title = "Date of Birth: ",
              value = dob,
              onValueChange = { dob = it },
              modifier = Modifier.testTag("dob_field"))

          // Section title
          ProfileText("Your Profile: ")

          // Divider
          HorizontalDivider(thickness = 2.dp)

          // Description input field
          ProfileField(
              title = "Description: ",
              value = description,
              onValueChange = { description = it },
              modifier = Modifier.testTag("description_field").height(84.dp))

          // Save Changes button
          Button(
              onClick = {
                val errorMessage = validateFields(name, dob, description)
                if (errorMessage != null) {
                  Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                } else {
                  // Save the profile (future implementation)
                  Toast.makeText(context, "Profile saved", Toast.LENGTH_SHORT).show()
                }
              },
              enabled = true,
              modifier =
                  Modifier.padding(1.dp)
                      .testTag("save_button")
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

@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
  OutlinedTextField(
      value = value,
      onValueChange = onValueChange,
      modifier =
          modifier
              .clip(RoundedCornerShape(10.dp))
              .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(10.dp)))
}

@Composable
fun ProfileText(title: String, textStyle: TextStyle = TextStyle(fontWeight = FontWeight(500))) {
  Text(text = title, style = textStyle)
}

@Composable
fun ProfileField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
  ProfileText(title)
  ProfileTextField(value = value, onValueChange = onValueChange, modifier = modifier)
}

/** Validates the fields of the profile screen. */
private fun validateFields(name: String, date: String, description: String): String? {
  return when {
    name.isEmpty() -> "Please enter a name"
    !validateDate(date) -> "Invalid date"
    description.isEmpty() -> "Please enter a description"
    else -> null
  }
}
