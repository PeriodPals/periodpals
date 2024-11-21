package com.android.periodpals.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.resources.ComponentColor.getSwitchColors
import com.android.periodpals.ui.theme.dimens

/**
 * A composable function that displays a section in the settings screen.
 *
 * @param content the content to be displayed in the settings section.
 */
@Composable
fun SettingsContainer(content: @Composable () -> Unit) {
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
    content()
  }
}

/**
 * A composable function that displays a description in the settings screen.
 *
 * @param text the text to be displayed in the description.
 */
@Composable
fun SettingsDescription(text: String) {
  Box(modifier = Modifier.fillMaxWidth()) {
    Text(
        text,
        textAlign = TextAlign.Start,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
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
 */
@Composable
fun SettingsSwitchRow(text: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    Text(
        text,
        modifier = Modifier.padding(top = MaterialTheme.dimens.small2).wrapContentHeight(),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface)
    Switch(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        colors = getSwitchColors(),
    )
  }
}

/**
 * A composable function that displays a row with an icon in the settings screen.
 *
 * @param text The text to be displayed in the row.
 * @param onClick The function to be called when the icon is clicked.
 * @param icon The icon to be displayed in the row.
 */
@Composable
fun SettingsIconRow(text: String, onClick: () -> Unit, icon: ImageVector) {
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.wrapContentHeight(),
        color = MaterialTheme.colorScheme.onSurface)
    Icon(icon, contentDescription = null, modifier = Modifier.clickable { onClick() })
  }
}
