package com.android.periodpals.ui.settings

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.SettingsScreen
import com.android.periodpals.resources.ComponentColor.getMenuItemColors
import com.android.periodpals.resources.ComponentColor.getMenuTextFieldColors
import com.android.periodpals.resources.ComponentColor.getSwitchColors
import com.android.periodpals.resources.ComponentColor.getTertiaryCardColors
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens

private const val SCREEN_TITLE = "My Settings"

// Comments
private const val COMMENT_NOTIFICATIONS = "Notify me when a pal needs ..."
private const val COMMENT_ORGANIC = "Which are ..."

// Notifications
private const val NOTIF_PALS = "Pals’ Notifications"
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

// Log messages
private const val LOG_SETTINGS_TAG = "SettingsScreen"

private const val LOG_SETTINGS_SUCCESS_SIGN_OUT = "Sign out successful"
private const val LOG_SETTINGS_FAILURE_SIGN_OUT = "Failed to sign out"

private const val LOG_SETTINGS_SUCCESS_DELETE = "Account deleted successfully"
private const val LOG_SETTINGS_FAILURE_DELETE = "Failed to delete account"

// Toast messages

private const val TOAST_SETTINGS_SUCCESS_SIGN_OUT = "Sign out successful"
private const val TOAST_SETTINGS_FAILURE_SIGN_OUT = "Failed to sign out"

private const val TOAST_SETTINGS_SUCCESS_DELETE = "Account deleted successfully"
private const val TOAST_SETTINGS_FAILURE_DELETE = "Failed to delete account"

private const val TOAST_LOAD_DATA_FAILURE = "Failed loading user authentication data"

/**
 * A composable function that displays the Settings screen, where users can manage their
 * notifications, themes, and account settings.
 *
 * This screen includes sections for:
 * - Notifications: Users can toggle notifications for different categories.
 * - Themes: Users can select a theme from a dropdown menu.
 * - Account Management: Users can change their password, sign out, or delete their account.
 *
 * @param userViewModel The ViewModel that handles user data.
 * @param authenticationViewModel The ViewModel that handles authentication logic.
 * @param navigationActions The navigation actions that can be performed in the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userViewModel: UserViewModel,
    authenticationViewModel: AuthenticationViewModel,
    navigationActions: NavigationActions
) {

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

  val context = LocalContext.current

  // delete account dialog logic
  if (showDialog) {
    DeleteAccountDialog(
        authenticationViewModel,
        userViewModel,
        context,
        navigationActions,
        onDismiss = { showDialog = false })
  }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(SettingsScreen.SCREEN),
      topBar = {
        TopAppBar(
            title = SCREEN_TITLE,
            true,
            onBackButtonClick = { navigationActions.goBack() },
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
            textTestTag = SettingsScreen.PALS_TEXT,
            switchTestTag = SettingsScreen.PALS_SWITCH,
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
            textTestTag = SettingsScreen.PADS_TEXT,
            switchTestTag = SettingsScreen.PADS_SWITCH)
        SettingsSwitchRow(
            text = NOTIF_TAMPONS,
            isChecked = receiveNotifications && tamponsNotifications,
            onCheckedChange = { tamponsNotifications = it },
            textTestTag = SettingsScreen.TAMPONS_TEXT,
            switchTestTag = SettingsScreen.TAMPONS_SWITCH)
        SettingsDescription(COMMENT_ORGANIC, SettingsScreen.ORGANIC_DESCRIPTION)
        SettingsSwitchRow(
            text = NOTIF_ORGANIC,
            isChecked = receiveNotifications && organicNotifications,
            onCheckedChange = { organicNotifications = it },
            textTestTag = SettingsScreen.ORGANIC_TEXT,
            switchTestTag = SettingsScreen.ORGANIC_SWITCH)
      }

      // theme section
      SettingsContainer(testTag = SettingsScreen.THEME_CONTAINER) {
        ExposedDropdownMenuBox(
            modifier = Modifier.testTag(SettingsScreen.THEME_DROP_DOWN_MENU_BOX),
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
          TextField(
              modifier = Modifier.menuAnchor().fillMaxWidth().wrapContentHeight(),
              textStyle = MaterialTheme.typography.labelLarge,
              value = theme,
              onValueChange = {},
              label = { Text(THEME_LABEL, style = MaterialTheme.typography.labelMedium) },
              singleLine = true,
              readOnly = true,
              leadingIcon = { Icon(icon, contentDescription = null) },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
              colors = getMenuTextFieldColors(),
          )
          ExposedDropdownMenu(
              expanded = expanded,
              onDismissRequest = { expanded = false },
              modifier = Modifier.wrapContentSize().testTag(SettingsScreen.THEME_DROP_DOWN_MENU),
              containerColor = MaterialTheme.colorScheme.primaryContainer,
          ) {
            THEME_DROPDOWN_CHOICES.forEach { option ->
              DropdownMenuItem(
                  modifier = Modifier.fillMaxWidth().wrapContentHeight(),
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
                  leadingIcon = {
                    Icon(
                        option[1] as ImageVector,
                        contentDescription = null,
                    )
                  },
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
            textTestTag = SettingsScreen.PASSWORD_TEXT,
            iconTestTag = SettingsScreen.PASSWORD_ICON,
        )
        SettingsIconRow(
            text = ACCOUNT_SIGN_OUT,
            onClick = {
              authenticationViewModel.logOut(
                  onSuccess = {
                    Handler(Looper.getMainLooper())
                        .post { // used to show the Toast on the main thread
                          Toast.makeText(
                                  context, TOAST_SETTINGS_SUCCESS_SIGN_OUT, Toast.LENGTH_SHORT)
                              .show()
                        }
                    Log.d(LOG_SETTINGS_TAG, LOG_SETTINGS_SUCCESS_SIGN_OUT)
                    navigationActions.navigateTo(Screen.SIGN_IN)
                    navigationActions.navigateTo(Route.AUTH)
                  },
                  onFailure = {
                    Handler(Looper.getMainLooper())
                        .post { // used to show the Toast on the main thread
                          Toast.makeText(
                                  context, TOAST_SETTINGS_FAILURE_SIGN_OUT, Toast.LENGTH_SHORT)
                              .show()
                        }
                    Log.d(LOG_SETTINGS_TAG, LOG_SETTINGS_FAILURE_SIGN_OUT)
                  })
            },
            icon = Icons.AutoMirrored.Outlined.Logout,
            textTestTag = SettingsScreen.SIGN_OUT_TEXT,
            iconTestTag = SettingsScreen.SIGN_OUT_ICON,
        )
        SettingsIconRow(
            text = ACCOUNT_DELETE,
            onClick = { showDialog = true },
            icon = Icons.Outlined.Delete,
            textTestTag = SettingsScreen.DELETE_ACCOUNT_TEXT,
            iconTestTag = SettingsScreen.DELETE_ACCOUNT_ICON,
            color = MaterialTheme.colorScheme.error,
        )
      }
    }
  }
}

/**
 * A composable function that displays a section in the settings screen.
 *
 * @param testTag the test tag for the section.
 * @param content the content to be displayed in the settings section.
 */
@Composable
private fun SettingsContainer(testTag: String, content: @Composable () -> Unit) {
  Column(
      modifier =
          Modifier.background(
                  MaterialTheme.colorScheme.surfaceContainerLow, MaterialTheme.shapes.medium)
              .padding(
                  horizontal = MaterialTheme.dimens.medium1,
                  vertical = MaterialTheme.dimens.small2,
              )
              .fillMaxSize()
              .testTag(testTag),
      verticalArrangement =
          Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterVertically),
  ) {
    content()
  }
}

/**
 * A composable function that displays a description in the settings screen.
 *
 * @param text the text to be displayed in the description.
 * @param testTag the test tag for the description.
 */
@Composable
private fun SettingsDescription(text: String, testTag: String) {
  Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
    Text(
        text,
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.fillMaxWidth().wrapContentHeight().testTag(testTag),
        color = MaterialTheme.colorScheme.onSurface,
    )
  }
}

/**
 * A composable function that displays a row with a switch in the settings screen.
 *
 * @param text The text to be displayed in the row.
 * @param isChecked The state of the switch.
 * @param onCheckedChange The function to be called when the switch is toggled.
 * @param textTestTag The test tag for the text.
 * @param switchTestTag The test tag for the switch.
 */
@Composable
private fun SettingsSwitchRow(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    textTestTag: String,
    switchTestTag: String
) {
  Row(
      modifier = Modifier.fillMaxWidth().wrapContentHeight(),
      horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text,
            modifier =
                Modifier.padding(top = MaterialTheme.dimens.small2)
                    .wrapContentHeight()
                    .testTag(textTestTag),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = getSwitchColors(),
            modifier = Modifier.testTag(switchTestTag),
        )
      }
}

/**
 * A composable function that displays a row with an icon in the settings screen.
 *
 * @param text The text to be displayed in the row.
 * @param onClick The function to be called when the icon is clicked.
 * @param icon The icon to be displayed in the row.
 * @param textTestTag The test tag for the text.
 * @param iconTestTag The test tag for the icon.
 * @param color The color of the text and icon.
 */
@Composable
private fun SettingsIconRow(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector,
    textTestTag: String,
    iconTestTag: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
  Row(
      modifier = Modifier.fillMaxWidth().wrapContentHeight(),
      horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.wrapContentHeight().testTag(textTestTag),
            color = color)
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.clickable { onClick() }.testTag(iconTestTag),
            tint = color)
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
private fun DeleteAccountDialog(
    authenticationViewModel: AuthenticationViewModel,
    userViewModel: UserViewModel,
    context: Context,
    navigationActions: NavigationActions,
    onDismiss: () -> Unit
) {
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
                    .testTag(SettingsScreen.DELETE_ACCOUNT_CARD)
                    .wrapContentHeight(),
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
                        .testTag(SettingsScreen.CARD_EMOJI_ICON),
                imageVector = Icons.Outlined.SentimentVeryDissatisfied,
                contentDescription = "Account Deletion Emoji",
            )
            Text(
                modifier = Modifier.wrapContentSize().testTag(SettingsScreen.CARD_TEXT),
                text = DIALOG_TEXT,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Row {
              Button(
                  onClick = {
                    authenticationViewModel.loadAuthenticationUserData(
                        onSuccess = {
                          Log.d(
                              LOG_SETTINGS_TAG, "user data loaded successfully, deleting the user")
                          authenticationViewModel.authUserData.value?.let {
                            userViewModel.deleteUser(
                                it.uid,
                                onSuccess = {
                                  Handler(Looper.getMainLooper())
                                      .post { // used to show the Toast on the main thread
                                        Toast.makeText(
                                                context,
                                                TOAST_SETTINGS_SUCCESS_DELETE,
                                                Toast.LENGTH_SHORT)
                                            .show()
                                      }
                                  Log.d(LOG_SETTINGS_TAG, LOG_SETTINGS_SUCCESS_DELETE)
                                  navigationActions.navigateTo(Screen.SIGN_IN)
                                  navigationActions.navigateTo(Route.AUTH)
                                },
                                onFailure = {
                                  Handler(Looper.getMainLooper())
                                      .post { // used to show the Toast on the main thread
                                        Toast.makeText(
                                                context,
                                                TOAST_SETTINGS_FAILURE_DELETE,
                                                Toast.LENGTH_SHORT)
                                            .show()
                                      }
                                  Log.d(LOG_SETTINGS_TAG, LOG_SETTINGS_FAILURE_DELETE)
                                })
                          }
                        },
                        onFailure = {
                          Handler(Looper.getMainLooper())
                              .post { // used to show the Toast on the main thread
                                Toast.makeText(context, TOAST_LOAD_DATA_FAILURE, Toast.LENGTH_SHORT)
                                    .show()
                              }
                          Log.d(LOG_SETTINGS_TAG, "failed to load user data, can't delete the user")
                        })
                  },
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
