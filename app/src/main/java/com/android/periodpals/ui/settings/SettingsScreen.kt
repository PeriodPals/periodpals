package com.android.periodpals.ui.settings

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.ProfileScreens.EditProfileScreen
import com.android.periodpals.resources.ComponentColor.getFilledPrimaryButtonColors
import com.android.periodpals.resources.ComponentColor.getMenuItemColors
import com.android.periodpals.resources.ComponentColor.getMenuTextFieldColors
import com.android.periodpals.resources.ComponentColor.getSwitchColors
import com.android.periodpals.resources.ComponentColor.getTertiaryCardColors
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens

private const val SCREEN_TITLE = "My Settings"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(userViewModel: UserViewModel, navigationActions: NavigationActions) {
  var receiveNotifications by remember { mutableStateOf(true) }
  var padsNotifications by remember { mutableStateOf(true) }
  var tamponsNotifications by remember { mutableStateOf(true) }
  var organicNotifications by remember { mutableStateOf(true) }
  var expanded by remember { mutableStateOf(false) }
  var theme by remember { mutableStateOf("System") }
  var icon by remember { mutableStateOf(Icons.Outlined.PhoneAndroid) }
  var showDialog by remember { mutableStateOf(false) }

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
      Column(
          modifier =
              Modifier.background(
                      MaterialTheme.colorScheme.surfaceContainerLow, MaterialTheme.shapes.medium)
                  .padding(
                      horizontal = MaterialTheme.dimens.medium1,
                      vertical = MaterialTheme.dimens.small3,
                  )
                  .fillMaxSize(),
          verticalArrangement =
              Arrangement.spacedBy(MaterialTheme.dimens.small3, Alignment.CenterVertically),
      ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              "Receive Pals’ Notifications",
              modifier = Modifier.padding(top = MaterialTheme.dimens.small2).wrapContentHeight(),
              style = MaterialTheme.typography.labelLarge,
              color = MaterialTheme.colorScheme.onSurface)
          Switch(
              checked = receiveNotifications,
              onCheckedChange = { receiveNotifications = it },
              colors = getSwitchColors(),
          )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Box(modifier = Modifier.fillMaxWidth()) {
          Text(
              "Notify me when a pal needs ...",
              style = MaterialTheme.typography.labelMedium,
              textAlign = TextAlign.Start,
              color = MaterialTheme.colorScheme.onSurface)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              "Pads",
              style = MaterialTheme.typography.labelLarge,
              modifier = Modifier.padding(top = MaterialTheme.dimens.small2).wrapContentHeight(),
              color = MaterialTheme.colorScheme.onSurface,
          )
          Switch(
              checked = receiveNotifications && padsNotifications,
              onCheckedChange = { padsNotifications = it },
              colors = getSwitchColors())
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              "Tampons",
              style = MaterialTheme.typography.labelLarge,
              modifier = Modifier.padding(top = MaterialTheme.dimens.small2).wrapContentHeight(),
              color = MaterialTheme.colorScheme.onSurface)
          Switch(
              checked = receiveNotifications && tamponsNotifications,
              onCheckedChange = { tamponsNotifications = it },
              colors = getSwitchColors())
        }
        Box(modifier = Modifier.fillMaxWidth()) {
          Text(
              "Which are ...",
              textAlign = TextAlign.Start,
              style = MaterialTheme.typography.labelMedium,
              modifier = Modifier.fillMaxWidth().wrapContentHeight(),
              color = MaterialTheme.colorScheme.onSurface,
          )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              "Organic",
              style = MaterialTheme.typography.labelLarge,
              modifier = Modifier.padding(top = MaterialTheme.dimens.small2).wrapContentHeight(),
              color = MaterialTheme.colorScheme.onSurface)
          Switch(
              checked = receiveNotifications && organicNotifications,
              onCheckedChange = { organicNotifications = it },
              colors = getSwitchColors())
        }
      }

      Column(
          modifier =
              Modifier.background(
                      MaterialTheme.colorScheme.surfaceContainerLow, MaterialTheme.shapes.medium)
                  .padding(
                      horizontal = MaterialTheme.dimens.medium1,
                      vertical = MaterialTheme.dimens.small3,
                  )
                  .fillMaxSize(),
          verticalArrangement =
              Arrangement.spacedBy(MaterialTheme.dimens.small3, Alignment.CenterVertically),
      ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          /**
           * Text( "Theme", style = MaterialTheme.typography.labelLarge, modifier =
           * Modifier.padding(top = MaterialTheme.dimens.small3).wrapContentHeight(), color =
           * MaterialTheme.colorScheme.onSurface)
           */
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
              DropdownMenuItem(
                  modifier = Modifier.fillMaxWidth(),
                  text = {
                    Text(
                        text = "System",
                        style = MaterialTheme.typography.labelLarge,
                        modifier =
                            Modifier.padding(top = MaterialTheme.dimens.small2).wrapContentHeight(),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                  },
                  onClick = {
                    theme = "System"
                    icon = Icons.Outlined.PhoneAndroid
                    expanded = false
                  },
                  leadingIcon = { Icon(Icons.Outlined.PhoneAndroid, contentDescription = null) },
                  colors = getMenuItemColors(),
                  contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
              )
              DropdownMenuItem(
                  modifier = Modifier.fillMaxWidth(),
                  text = {
                    Text(
                        text = "Light Mode",
                        style = MaterialTheme.typography.labelLarge,
                        modifier =
                            Modifier.padding(top = MaterialTheme.dimens.small2).wrapContentHeight(),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                  },
                  onClick = {
                    theme = "Light Mode"
                    icon = Icons.Outlined.LightMode
                    expanded = false
                  },
                  leadingIcon = { Icon(Icons.Outlined.LightMode, contentDescription = null) },
                  colors = getMenuItemColors(),
                  contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
              )
              DropdownMenuItem(
                  modifier = Modifier.fillMaxWidth(),
                  text = {
                    Text(
                        text = "Dark Mode",
                        style = MaterialTheme.typography.labelLarge,
                        modifier =
                            Modifier.padding(top = MaterialTheme.dimens.small2).wrapContentHeight(),
                        color = MaterialTheme.colorScheme.onSurface)
                  },
                  onClick = {
                    theme = "Dark Mode"
                    icon = Icons.Outlined.DarkMode
                    expanded = false
                  },
                  leadingIcon = { Icon(Icons.Outlined.DarkMode, contentDescription = null) },
                  colors = getMenuItemColors(),
                  contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
              )
            }
          }
        }
      }

      Column(
          modifier =
              Modifier.background(
                      MaterialTheme.colorScheme.surfaceContainerLow, MaterialTheme.shapes.medium)
                  .padding(
                      horizontal = MaterialTheme.dimens.medium1,
                      vertical = MaterialTheme.dimens.small3,
                  )
                  .fillMaxSize(),
          verticalArrangement =
              Arrangement.spacedBy(MaterialTheme.dimens.small3, Alignment.CenterVertically),
      ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              "Change my password",
              style = MaterialTheme.typography.labelLarge,
              modifier = Modifier.wrapContentHeight(),
              color = MaterialTheme.colorScheme.onSurface)
          Icon(Icons.Outlined.Key, contentDescription = null, modifier = Modifier.clickable {})
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              "Sign Out",
              style = MaterialTheme.typography.labelLarge,
              modifier = Modifier.wrapContentHeight(),
              color = MaterialTheme.colorScheme.onSurface)
          Icon(
              Icons.AutoMirrored.Outlined.Logout,
              contentDescription = null,
              modifier = Modifier.clickable { navigationActions.navigateTo(Screen.SIGN_IN) })
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              "Delete Account",
              style = MaterialTheme.typography.labelLarge,
              modifier = Modifier.wrapContentHeight(),
              color = MaterialTheme.colorScheme.error,
          )
          Icon(
              Icons.Outlined.Delete,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.clickable { showDialog = true })
        }
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
