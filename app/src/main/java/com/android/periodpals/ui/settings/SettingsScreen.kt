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
import androidx.compose.material3.ButtonDefaults
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
import com.android.periodpals.resources.C.Tag.SettingsScreen
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

// Comments
private const val COMMENT_NOTIFICATIONS = "Notify me when a pal needs ..."
private const val COMMENT_ORGANIC = "Which are ..."

// Notifications
private const val NOTIF_PALS = "Receive Pals’ Notifications"
private const val NOTIF_PADS = "Pads"
private const val NOTIF_TAMPONS = "Tampons"
private const val NOTIF_ORGANIC = "Organic"

// Themes
private const val THEME_LABEL = "Theme"
private const val THEME_SYSTEM = "System"
private const val THEME_LIGHT = "Light Mode"
private const val THEME_DARK = "Dark Mode"

// account management
private const val ACCOUNT_PASSWORD = "Change Password"
private const val ACCOUNT_SIGN_OUT = "Sign Out"
private const val ACCOUNT_DELETE = "Delete Account"

// Dialog
private const val DIALOG_TEXT = "Are you sure you want to delete your account?"

// Dropdown choices
private val THEME_DROPDOWN_CHOICES =
    listOf(
        listOf(THEME_SYSTEM, Icons.Outlined.PhoneAndroid),
        listOf(THEME_LIGHT, Icons.Outlined.LightMode),
        listOf(THEME_DARK, Icons.Outlined.DarkMode))

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
  var theme by remember { mutableStateOf(THEME_SYSTEM) }
  var icon by remember { mutableStateOf(Icons.Outlined.PhoneAndroid) }

  // delete account dialog state
  var showDialog by remember { mutableStateOf(false) }

  // delete account dialog logic
  if (showDialog) {
    DeleteAccountDialog(navigationActions, onDismiss = { showDialog = false })
  }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(SettingsScreen.SCREEN),
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
      SettingsContainer(testTag = SettingsScreen.NOTIFICATIONS_CONTAINER) {
        SettingsSwitchRow(
            text = NOTIF_PALS,
            isChecked = receiveNotifications,
            onCheckedChange = { receiveNotifications = it },
            testTag = SettingsScreen.PALS_SWITCH,
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.testTag(SettingsScreen.HORIZONTAL_DIVIDER))
        SettingsDescription(
            text = COMMENT_NOTIFICATIONS, testTag = SettingsScreen.NOTIFICATIONS_DESCRIPTION)
        SettingsSwitchRow(
            text = NOTIF_PADS,
            isChecked = receiveNotifications && padsNotifications,
            onCheckedChange = { padsNotifications = it },
            testTag = SettingsScreen.PADS_SWITCH)
        SettingsSwitchRow(
            text = NOTIF_TAMPONS,
            isChecked = receiveNotifications && tamponsNotifications,
            onCheckedChange = { tamponsNotifications = it },
            testTag = SettingsScreen.TAMPONS_SWITCH)
        SettingsDescription(COMMENT_ORGANIC, SettingsScreen.ORGANIC_DESCRIPTION)
        SettingsSwitchRow(
            text = NOTIF_ORGANIC,
            isChecked = receiveNotifications && organicNotifications,
            onCheckedChange = { organicNotifications = it },
            testTag = SettingsScreen.ORGANIC_SWITCH)
      }

      // theme section
      SettingsContainer(testTag = SettingsScreen.THEME_CONTAINER) {
        ExposedDropdownMenuBox(
            modifier = Modifier.testTag(SettingsScreen.THEME_DROP_DOWN_MENU),
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
          TextField(
              modifier = Modifier.menuAnchor().fillMaxWidth(),
              textStyle = MaterialTheme.typography.labelLarge,
              value = theme,
              onValueChange = {},
              label = { Text(THEME_LABEL, style = MaterialTheme.typography.labelSmall) },
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
                            Modifier.padding(top = MaterialTheme.dimens.small2).wrapContentHeight(),
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

      // account management section
      SettingsContainer(testTag = SettingsScreen.ACCOUNT_MANAGEMENT_CONTAINER) {
        SettingsIconRow(
            text = ACCOUNT_PASSWORD,
            onClick = {},
            icon = Icons.Outlined.Key,
            testTag = SettingsScreen.PASSWORD_ICON_ROW,
        )
        SettingsIconRow(
            text = ACCOUNT_SIGN_OUT,
            onClick = { navigationActions.navigateTo(Screen.SIGN_IN) },
            icon = Icons.AutoMirrored.Outlined.Logout,
            testTag = SettingsScreen.SIGN_OUT_ICON_ROW,
        )
        SettingsIconRow(
            text = ACCOUNT_DELETE,
            onClick = { showDialog = true },
            icon = Icons.Outlined.Delete,
            testTag = SettingsScreen.DELETE_ACCOUNT_ICON_ROW,
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
            modifier =
                Modifier.fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.dimens.medium3,
                        vertical = MaterialTheme.dimens.small3,
                    )
                    .testTag(SettingsScreen.DELETE_ACCOUNT_CARD),
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
                modifier =
                    Modifier.size(MaterialTheme.dimens.iconSize)
                        .testTag(SettingsScreen.DELETE_EMOJI_ICON),
                imageVector = Icons.Outlined.SentimentVeryDissatisfied,
                contentDescription = "Account Deletion Emoji",
            )
            Text(
                modifier = Modifier.wrapContentSize().testTag(SettingsScreen.DELETE_TEXT),
                text = DIALOG_TEXT,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Row {
              Button(
                  onClick = { navigationActions.navigateTo(Screen.SIGN_IN) },
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.error,
                          contentColor = MaterialTheme.colorScheme.onError),
                  modifier =
                      Modifier.padding(MaterialTheme.dimens.small2)
                          .testTag(SettingsScreen.DELETE_BUTTON)) {
                    Text(
                        "Yes",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondary)
                  }
              Button(
                  onClick = onDismiss,
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.primary,
                          contentColor = MaterialTheme.colorScheme.onPrimary),
                  modifier =
                      Modifier.padding(MaterialTheme.dimens.small2)
                          .testTag(SettingsScreen.NOT_DELETE_BUTTON)) {
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
