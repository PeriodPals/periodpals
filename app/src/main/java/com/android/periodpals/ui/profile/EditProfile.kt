package com.android.periodpals.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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

/* Placeholder Screen, waiting for implementation */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun EditProfileScreen(navigationActions: NavigationActions) {
  var name by remember { mutableStateOf("") }
  var dob by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }

  Scaffold(
      bottomBar = ({
            BottomNavigationMenu(
                onTabSelect = { route -> navigationActions.navigateTo(route) },
                tabList = LIST_TOP_LEVEL_DESTINATION,
                selectedItem = navigationActions.currentRoute())
          }),
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
          Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Box {
              GlideImage(
                  model = "",
                  modifier =
                      Modifier.padding(1.dp)
                          .width(124.dp)
                          .height(124.dp)
                          .background(color = Color(0xFFD9D9D9), shape = CircleShape),
                  contentDescription = "image profile",
                  contentScale = ContentScale.None)
              Icon(
                  Icons.Filled.AddCircleOutline,
                  contentDescription = "add circle",
                  modifier =
                      Modifier.align(Alignment.BottomEnd)
                          .size(40.dp)
                          .background(color = Color(0xFF79747E), shape = CircleShape)
                          .testTag("add_circle_icon"))
            }
          }

          Text(
              text = "Mandatory Fields",
              style =
                  TextStyle(
                      fontWeight = FontWeight(500),
                  ))

          HorizontalDivider(thickness = 2.dp)

          Row(horizontalArrangement = Arrangement.Start) {
            Text(
                text = "Email: ",
                style =
                    TextStyle(
                        fontWeight = FontWeight(500),
                    ))

            Text(
                text = "emilia.jones@email.com",
                style =
                    TextStyle(
                        fontWeight = FontWeight(400),
                        textDecoration = TextDecoration.Underline,
                    ))
          }

          Text(
              text = "Name:",
              style =
                  TextStyle(
                      fontWeight = FontWeight(500),
                  ))

          OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              modifier =
                  Modifier.testTag("name_field")
                      .clip(RoundedCornerShape(10.dp)) // Clip the box to have rounded corners.
                      .border(
                          1.dp,
                          MaterialTheme.colorScheme.onSurface, // Color of the border.
                          RoundedCornerShape(10.dp), // Rounded corners for the border.
                      ),
          )

          Text(
              text = "Date of Birth:",
              style =
                  TextStyle(
                      fontWeight = FontWeight(500),
                  ))

          OutlinedTextField(
              value = dob,
              onValueChange = { dob = it },
              modifier =
                  Modifier.testTag("dob_field")
                      .clip(RoundedCornerShape(10.dp)) // Clip the box to have rounded corners.
                      .border(
                          1.dp,
                          MaterialTheme.colorScheme.onSurface, // Color of the border.
                          RoundedCornerShape(10.dp), // Rounded corners for the border.
                      ),
          )

          Text(
              text = "Your Profile",
              style =
                  TextStyle(
                      fontWeight = FontWeight(500),
                  ))

          HorizontalDivider(thickness = 2.dp)

          Text(
              text = "Description:",
              style =
                  TextStyle(
                      fontWeight = FontWeight(500),
                  ))

          OutlinedTextField(
              value = description,
              onValueChange = { description = it },
              modifier =
                  Modifier.testTag("description_field")
                      .clip(RoundedCornerShape(10.dp)) // Clip the box to have rounded corners.
                      .border(
                          1.dp,
                          MaterialTheme.colorScheme.onSurface, // Color of the border.
                          RoundedCornerShape(10.dp), // Rounded corners for the border.
                      )
                      .height(84.dp),
          )

          Button(
              onClick = {},
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
