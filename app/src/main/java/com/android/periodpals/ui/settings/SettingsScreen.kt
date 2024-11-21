package com.android.periodpals.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.ProfileScreens.EditProfileScreen
import com.android.periodpals.resources.ComponentColor.getMenuItemColors
import com.android.periodpals.resources.ComponentColor.getMenuTextFieldColors
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens

private const val SCREEN_TITLE = "My Settings"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(userViewModel: UserViewModel, navigationActions: NavigationActions) {
  var receiveNotifications by remember { mutableStateOf(true) }
  var expanded by remember { mutableStateOf(false) }
  var theme by remember { mutableStateOf("System") }
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(EditProfileScreen.SCREEN),
      topBar = {
        TopAppBar(
            title = SCREEN_TITLE,
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
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Receive Pals’ Notifications")
        Switch(checked = receiveNotifications, onCheckedChange = { receiveNotifications = it })
      }
      HorizontalDivider()
      Text("Notify me when a pal needs ...", textAlign = TextAlign.Start)
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Receive Pals’ Notifications")
        Switch(checked = receiveNotifications, onCheckedChange = { receiveNotifications = it })
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Pads")
        Switch(checked = receiveNotifications, onCheckedChange = { receiveNotifications = it })
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Tampons")
        Switch(checked = receiveNotifications, onCheckedChange = { receiveNotifications = it })
      }
      Text("Which are ...", textAlign = TextAlign.Start)
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Organic")
        Switch(checked = receiveNotifications, onCheckedChange = { receiveNotifications = it })
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Theme")
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
          TextField(
              modifier = Modifier.fillMaxWidth().wrapContentHeight().menuAnchor(),
              textStyle = MaterialTheme.typography.labelLarge,
              value = "System",
              onValueChange = {},
              singleLine = true,
              readOnly = true,
              leadingIcon = { Icon(Icons.Outlined.PhoneAndroid, contentDescription = null) },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
              colors = getMenuTextFieldColors(),
          )
          ExposedDropdownMenu(
              expanded = expanded,
              onDismissRequest = { expanded = false },
              modifier = Modifier.wrapContentSize(),
              containerColor = MaterialTheme.colorScheme.primaryContainer,
          ) {
            DropdownMenuItem(
                modifier = Modifier.fillMaxWidth(),
                text = { Text(text = "Light Mode", style = MaterialTheme.typography.labelLarge) },
                onClick = {
                  theme = "Light Mode"
                  expanded = false
                },
                leadingIcon = { Icon(Icons.Outlined.LightMode, contentDescription = null) },
                colors = getMenuItemColors(),
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
            )

            DropdownMenuItem(
                modifier = Modifier.fillMaxWidth(),
                text = { Text(text = "Dark Mode", style = MaterialTheme.typography.labelLarge) },
                onClick = {
                  theme = "Dark Mode"
                  expanded = false
                },
                leadingIcon = { Icon(Icons.Outlined.DarkMode, contentDescription = null) },
                colors = getMenuItemColors(),
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
            )
          }
        }
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Change my password")
        Icon(Icons.Outlined.Key, contentDescription = null)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Sign Out")
        Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null)
      }
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Delete Account")
        Icon(Icons.Outlined.Delete, contentDescription = null)
      }
    }
  }
}
