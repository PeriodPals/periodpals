package com.android.periodpals.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.android.periodpals.R
import com.android.periodpals.resources.C.Tag.ChannelsScreen
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar

/**
 * Composable function that displays the Channels screen.
 *
 * @param navigationActions The actions that can be performed on the screen.
 * @param context The context of the screen.
 * @param channelsScreenContent The content of the screen.
 */
@Composable
fun ChannelsScreenContainer(
    navigationActions: NavigationActions,
    channelsScreenContent: @Composable () -> Unit,
) {
  val context = LocalContext.current

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(ChannelsScreen.SCREEN),
      topBar = {
        TopAppBar(
            title = context.getString(R.string.channel_screen_title),
            backButton = true,
            onBackButtonClick = { navigationActions.navigateTo(Screen.ALERT_LIST) })
      },
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
  ) { paddingValues ->
    Column(modifier = Modifier.padding(paddingValues).testTag(ChannelsScreen.CHANNELS)) {
      channelsScreenContent()
    }
  }
}
