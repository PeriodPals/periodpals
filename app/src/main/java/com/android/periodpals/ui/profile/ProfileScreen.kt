package com.android.periodpals.ui.profile

// import androidx.compose.ui.tooling.preview.Preview
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.periodpals.R
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
// @Preview
@Composable
fun ProfileScreen(navigationActions: NavigationActions) {
  // Declare and remember the profile image URI
  var profileImageUri by remember {
    mutableStateOf<Uri?>(
        Uri.parse("android.resource://com.android.periodpals/${R.drawable.generic_avatar}"))
  }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("profileScreen"),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      topBar = {
        TopAppBar(
            title = "Profile",
        )
      },
      content = { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          // Display the user's profile image.
          GlideImage(
              model = profileImageUri,
              contentDescription = "Avatar Imagee",
              contentScale = ContentScale.Crop,
              modifier =
                  Modifier.size(190.dp)
                      .testTag("profileAvatar")
                      .background(
                          color = MaterialTheme.colorScheme.background, shape = CircleShape),
          )

          ProfileName() // Display the user's profile name.
          ProfileDetails() // Display additional details like description and reviews.
        }
      })
}

@Composable
private fun ProfileName() {
  Text(
      text = "Name",
      modifier = Modifier.testTag("profileName"),
      fontSize = 24.sp, // Font size for the name.
      fontWeight = FontWeight.Bold // Make the text bold.
      )
}

@Composable
private fun ProfileDetails() {
  Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp) // Space items by 8dp vertically.
      ) {
        // Box for the description.
        Text(
            text = "Description",
            fontSize = 20.sp,
            modifier = Modifier.padding(vertical = 8.dp).testTag("Description"))
        ProfileInfoBox(text = "", minHeight = 100.dp, Modifier)
        Text(
            text = "New user / Number of interactions",
            fontSize = 16.sp,
            color = Color(101, 116, 193))
        Text(text = "Reviews", fontSize = 20.sp, modifier = Modifier.padding(vertical = 8.dp))
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
