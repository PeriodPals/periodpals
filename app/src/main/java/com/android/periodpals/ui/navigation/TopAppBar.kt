package com.android.periodpals.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.android.periodpals.ui.theme.PurpleGrey80

/**
 * Displays a top app bar with an optional back button.
 *
 * @param title The title text to be displayed in the app bar.
 * @param backButton Whether to show a back button. Default is false.
 * @param onBackButtonClick Called when the back button is clicked.
 *
 * ### Usage:
 * The top app bar can be displayed with a title:
 * ```
 * TopAppBar(title = "Tampon Timer")
 * ```
 *
 * To include a back button, e.g., on the edit profile screen:
 * ```kotlin
 * TopAppBar(
 *     title = "Tampon Timer",
 *     backButton = true,
 *     onBackButtonClick = { navigationActions.goBack() }
 * )
 * ```
 *
 * ### Testing:
 * - Use the testTag "topBar" to verify the app bar is displayed.
 * - If the back button is shown, check for the "goBackButton" tag to confirm its presence and
 *   functionality.
 * - If the edit button is shown, check for the "editButton" tag to confirm its presence and
 *   functionality.
 * - The title can be checked using the "screenTitle" testTag.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: String,
    backButton: Boolean = false,
    editButton: Boolean = false,
    onBackButtonClick: (() -> Unit)? = {},
    onEditButtonClick: (() -> Unit)? = {}
) {
  require(!backButton || onBackButtonClick != null) {
    "onBackButtonClick must be provided when backButton is true"
  }
  require(!editButton || onEditButtonClick != null) {
    "onEditButtonClick must be provided when editButton is true"
  }

  CenterAlignedTopAppBar(
      modifier = Modifier.fillMaxWidth().height(48.dp).testTag("topBar"),
      title = {
        Text(
            text = title,
            modifier = Modifier.padding(12.dp).testTag("screenTitle"),
            style = MaterialTheme.typography.titleMedium)
      },
      navigationIcon = {
        if (backButton) {
          IconButton(onClick = onBackButtonClick!!, modifier = Modifier.testTag("goBackButton")) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp))
          }
        }
      },
      actions = {
        if (editButton) {
          IconButton(onClick = onEditButtonClick!!, modifier = Modifier.testTag("editButton")) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit",
                modifier = Modifier.size(20.dp))
          }
        }
      },
      colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = PurpleGrey80))
}
