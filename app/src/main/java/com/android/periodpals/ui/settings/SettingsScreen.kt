package com.android.periodpals.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.ProfileScreens.EditProfileScreen
import com.android.periodpals.resources.ComponentColor.getFilledPrimaryButtonColors
import com.android.periodpals.resources.ComponentColor.getMenuItemColors
import com.android.periodpals.resources.ComponentColor.getMenuTextFieldColors
import com.android.periodpals.resources.ComponentColor.getTertiaryCardColors
import com.android.periodpals.ui.components.SettingsContainer
import com.android.periodpals.ui.components.SettingsDescription
import com.android.periodpals.ui.components.SettingsIconRow
import com.android.periodpals.ui.components.SettingsSwitchRow
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens

private const val SCREEN_TITLE = "My Settings"
private val THEME_DROPDOWN_CHOICES =
    listOf(
        listOf("System", Icons.Outlined.PhoneAndroid),
        listOf("Light Mode", Icons.Outlined.LightMode),
        listOf("Dark Mode", Icons.Outlined.DarkMode))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(userViewModel: UserViewModel, navigationActions: NavigationActions) {
  // notifications states
  var receiveNotifications by remember { mutableStateOf(true) }
  var padsNotifications by remember { mutableStateOf(true) }
  var tamponsNotifications by remember { mutableStateOf(true) }
  var organicNotifications by remember { mutableStateOf(true) }
  // theme states
  var expanded by remember { mutableStateOf(false) }
  var theme by remember { mutableStateOf("System") }
  var icon by remember { mutableStateOf(Icons.Outlined.PhoneAndroid) }
  // delete account dialog state
  var showDialog by remember { mutableStateOf(false) }

  // delete account dialog logic
  if (showDialog) {
    DeleteAccountDialog(navigationActions, onDismiss = { showDialog = false })
  }

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

      // notification section
      SettingsContainer {
        SettingsSwitchRow(
            text = "Receive Pals’ Notifications",
            isChecked = receiveNotifications,
            onCheckedChange = { receiveNotifications = it },
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        SettingsDescription("Notify me when a pal needs ...")
        SettingsSwitchRow(
            text = "Pads",
            isChecked = receiveNotifications && padsNotifications,
            onCheckedChange = { padsNotifications = it },
        )
        SettingsSwitchRow(
            text = "Tampons",
            isChecked = receiveNotifications && tamponsNotifications,
            onCheckedChange = { tamponsNotifications = it },
        )
        SettingsDescription("Which are ...")
        SettingsSwitchRow(
            text = "Organic",
            isChecked = receiveNotifications && organicNotifications,
            onCheckedChange = { organicNotifications = it },
        )
      }
      // theme section
      SettingsContainer {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          ExposedDropdownMenuBox(
              expanded = expanded,
              onExpandedChange = { expanded = it },
          ) {
            TextField(
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                textStyle = MaterialTheme.typography.labelLarge,
                value = theme,
                onValueChange = {},
                label = { Text("Theme", style = MaterialTheme.typography.labelSmall) },
                singleLine = true,
                readOnly = true,
                leadingIcon = { Icon(icon, contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = getMenuTextFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.wrapContentSize(),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ) {
              THEME_DROPDOWN_CHOICES.forEach { option ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = {
                      Text(
                          text = option[0] as String,
                          style = MaterialTheme.typography.labelLarge,
                          modifier =
                              Modifier.padding(top = MaterialTheme.dimens.small2)
                                  .wrapContentHeight(),
                          color = MaterialTheme.colorScheme.onSurface,
                      )
                    },
                    onClick = {
                      theme = option[0] as String
                      icon = option[1] as ImageVector
                      expanded = false
                    },
                    leadingIcon = { Icon(option[1] as ImageVector, contentDescription = null) },
                    colors = getMenuItemColors(),
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
              }
            }
          }
        }
      }

      // account management section
      SettingsContainer {
        SettingsIconRow(
            text = "Change my password",
            onClick = {},
            icon = Icons.Outlined.Key,
        )
        SettingsIconRow(
            text = "Sign Out",
            onClick = { navigationActions.navigateTo(Screen.SIGN_IN) },
            icon = Icons.AutoMirrored.Outlined.Logout,
        )
        SettingsIconRow(
            text = "Delete Account",
            onClick = { showDialog = true },
            icon = Icons.Outlined.Delete,
        )
      }
    }
  }
}

/**
 * Composable function that displays a dialog asking for the user whether they want to delete their
 * account..
 *
 * @param navigationActions The navigation actions to use.
 * @param onDismiss The callback to dismiss the dialog.
 */
@Composable
private fun DeleteAccountDialog(navigationActions: NavigationActions, onDismiss: () -> Unit) {

  Dialog(
      onDismissRequest = onDismiss,
      properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier.wrapContentSize(),
            shape = RoundedCornerShape(size = MaterialTheme.dimens.cardRoundedSize),
            colors = getTertiaryCardColors(),
            elevation =
                CardDefaults.cardElevation(defaultElevation = MaterialTheme.dimens.cardElevation),
        ) {
          Column(
              modifier = Modifier.wrapContentSize().padding(MaterialTheme.dimens.small2),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement =
                  Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterVertically),
          ) {
            Icon(
                modifier = Modifier.size(MaterialTheme.dimens.iconSize),
                imageVector = Icons.Outlined.SentimentVeryDissatisfied,
                contentDescription = "Account Deletion Emoji",
            )
            Text(
                modifier = Modifier.wrapContentSize(),
                text = "Are you sure you want to delete your account?",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Row {
              Button(
                  onClick = { navigationActions.navigateTo(Screen.SIGN_IN) },
                  colors = getFilledPrimaryButtonColors(),
                  modifier = Modifier.padding(MaterialTheme.dimens.small2)) {
                    Text(
                        "Yes",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondary)
                  }
              Button(
                  onClick = onDismiss,
                  colors = getFilledPrimaryButtonColors(),
                  modifier = Modifier.padding(MaterialTheme.dimens.small2)) {
                    Text(
                        "No",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondary)
                  }
            }
          }
        }
      }
}
