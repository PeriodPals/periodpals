package com.android.periodpals.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.periodpals.R
import com.android.periodpals.ui.navigation.TopAppBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi

@Preview
@Composable
fun ProfileScreen() {
  // Declare and remember the profile image URI
  /*var profileImageUri by remember {
    mutableStateOf<Uri?>(
        Uri.parse("android.resource://com.android.periodpals/${R.drawable.generic_avatar}"))
  }*/

  Scaffold(
      modifier = Modifier.testTag("profileScreen"),
      topBar = { TopAppBar("Your Profile") },
      //        bottomBar = { BottomNavigationMenu(
      //            onTabSelect = { route -> navigationActions.navigateTo(route) },
      //            tabList = LIST_TOP_LEVEL_DESTINATION,
      //            selectedItem = navigationActions.currentRoute()
      //        ) },
      content = { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          ProfileAvatar(/*profileImageUri*/ ) // Display the user's profile avatar.
          Spacer(modifier = Modifier.height(16.dp))
          ProfileName() // Display the user's profile name.
          Spacer(modifier = Modifier.height(8.dp))
          ProfileDetails() // Display additional details like description and reviews.
        }
      })
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ProfileAvatar(/*profileImageUri: Any*/ ) {

  Box(
      modifier =
          Modifier.size(180.dp) // Set the size of the avatar container.
              .background(
                  color = MaterialTheme.colorScheme.background, // Set background color.
                  shape = RoundedCornerShape(90.dp) // Rounded shape with a radius of 90dp (circle).
                  ),
      contentAlignment = Alignment.Center // Center the avatar image inside the box.
      ) {
        /*GlideImage(
            model = profileImageUri,
            contentDescription = "Avatar Imagee",
            contentScale = ContentScale.Crop,
            modifier =
                Modifier.size(124.dp) // Set size of the image
                    .background(
                        color = MaterialTheme.colorScheme.background, // Background color
                        shape = CircleShape // Circular shape
                        ),
        )*/

        Image(
            painter =
                painterResource(id = R.drawable.generic_avatar), // Use a placeholder avatar image.
            contentDescription = "Avatar Image",
            modifier =
                Modifier.fillMaxSize() // Fill the size of the box.
                    .testTag("profileAvatar"),
            contentScale = ContentScale.Crop // Crop the image to fit the box.
            )
      }
}

@Composable
private fun ProfileName() {
  Text(
      text = "Displayed Name",
      modifier = Modifier.testTag("profileName"),
      fontSize = 24.sp, // Font size for the name.
      fontWeight = FontWeight.Bold // Make the text bold.
      )
}

@Composable
private fun ProfileDetails() {
  Column(
      modifier = Modifier.fillMaxWidth().padding(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp) // Space items by 8dp vertically.
      ) {
        // Box for the description.
        Text(text = "Description", fontSize = 20.sp)
        ProfileInfoBox(text = "", minHeight = 100.dp, Modifier.testTag("Description"))
        Text(
            text = "New user / Number of interactions",
            fontSize = 16.sp,
            color = Color(101, 116, 193))
        Text(text = "Reviews", fontSize = 20.sp)
        // Boxes for reviews.
        ProfileInfoBox(text = "", minHeight = 20.dp, Modifier.testTag("reviewOne"))
        ProfileInfoBox(text = "", minHeight = 20.dp, Modifier.testTag("reviewTwo"))
      }
}

@Composable
private fun ProfileInfoBox(text: String, minHeight: Dp, modifier: Modifier) {
  // Reusable composable for displaying information inside a bordered box.
  Box(
      modifier =
          modifier
              .fillMaxWidth() // Make the box fill the available width.
              .clip(RoundedCornerShape(8.dp)) // Clip the box to have rounded corners.
              .border(
                  1.dp,
                  MaterialTheme.colorScheme.onSurface, // Color of the border.
                  RoundedCornerShape(8.dp) // Rounded corners for the border.
                  )
              .padding(8.dp) // Padding inside the box.
              .heightIn(min = minHeight) // Set a minimum height for the box.
      ) {
        // Text inside the box
        Text(text = text, fontSize = 20.sp, textAlign = TextAlign.Start)
      }
}
