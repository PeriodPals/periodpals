package com.android.periodpals.ui.settings

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.android.periodpals.R
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.SettingsScreen
import com.android.periodpals.resources.ComponentColor.getTertiaryCardColors
import com.android.periodpals.ui.components.SliderMenu
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens
import kotlin.math.roundToInt

// Log messages
private const val LOG_SETTINGS_TAG = "SettingsScreen"

private const val DEFAULT_RADIUS = 500F
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
@Composable
fun SettingsScreen(
    userViewModel: UserViewModel,
    authenticationViewModel: AuthenticationViewModel,
    navigationActions: NavigationActions,
) {

  // delete account dialog state
  var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var sliderPosition by remember { mutableFloatStateOf(DEFAULT_RADIUS) }

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
            title = context.getString(R.string.settings_screen_title),
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

      // account management section
      SettingsContainer(testTag = SettingsScreen.ACCOUNT_MANAGEMENT_CONTAINER) {
        SettingsIconRow(
            text = context.getString(R.string.settings_account_password),
            onClick = {},
            icon = Icons.Outlined.Key,
            textTestTag = SettingsScreen.PASSWORD_TEXT,
            iconTestTag = SettingsScreen.PASSWORD_ICON,
        )
        SettingsIconRow(
            text = context.getString(R.string.settings_account_sign_out),
            onClick = {
              authenticationViewModel.logOut(
                  onSuccess = {
                    Handler(Looper.getMainLooper())
                        .post { // used to show the Toast on the main thread
                          Toast.makeText(
                                  context,
                                  context.getString(R.string.settings_toast_success_sign_out),
                                  Toast.LENGTH_SHORT)
                              .show()
                        }
                    Log.d(LOG_SETTINGS_TAG, "Sign out successful")
                    navigationActions.navigateTo(Screen.SIGN_IN)
                  },
                  onFailure = {
                    Handler(Looper.getMainLooper())
                        .post { // used to show the Toast on the main thread
                          Toast.makeText(
                                  context,
                                  context.getString(R.string.settings_toast_failure_sign_out),
                                  Toast.LENGTH_SHORT)
                              .show()
                        }
                    Log.d(LOG_SETTINGS_TAG, "Failed to sign out")
                  })
            },
            icon = Icons.AutoMirrored.Outlined.Logout,
            textTestTag = SettingsScreen.SIGN_OUT_TEXT,
            iconTestTag = SettingsScreen.SIGN_OUT_ICON,
        )
        SettingsIconRow(
            text = context.getString(R.string.settings_account_delete),
            onClick = { showDialog = true },
            icon = Icons.Outlined.Delete,
            textTestTag = SettingsScreen.DELETE_ACCOUNT_TEXT,
            iconTestTag = SettingsScreen.DELETE_ACCOUNT_ICON,
            color = MaterialTheme.colorScheme.error,
        )
      }

      SliderMenu(sliderPosition) { sliderPosition = (it / 100).roundToInt() * 100f }
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
                text = context.getString(R.string.settings_dialog_text),
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
                          userViewModel.deleteUser(
                              authenticationViewModel.authUserData.value!!.uid,
                              onSuccess = {
                                Handler(Looper.getMainLooper())
                                    .post { // used to show the Toast on the main thread
                                      Toast.makeText(
                                              context,
                                              context.getString(
                                                  R.string.settings_toast_success_delete),
                                              Toast.LENGTH_SHORT)
                                          .show()
                                    }
                                Log.d(LOG_SETTINGS_TAG, "Account deleted successfully")
                                navigationActions.navigateTo(Screen.SIGN_IN)
                              },
                              onFailure = {
                                Handler(Looper.getMainLooper())
                                    .post { // used to show the Toast on the main thread
                                      Toast.makeText(
                                              context,
                                              context.getString(
                                                  R.string.settings_toast_failure_delete),
                                              Toast.LENGTH_SHORT)
                                          .show()
                                    }
                                Log.d(LOG_SETTINGS_TAG, "Failed to delete account")
                              })
                        },
                        onFailure = {
                          Handler(Looper.getMainLooper())
                              .post { // used to show the Toast on the main thread
                                Toast.makeText(
                                        context,
                                        context.getString(
                                            R.string.settings_toast_load_data_failure),
                                        Toast.LENGTH_SHORT)
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
