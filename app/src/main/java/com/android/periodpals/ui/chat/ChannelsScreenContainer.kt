package com.android.periodpals.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.periodpals.resources.C.Tag.ChannelsScreen
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar

private const val SCREEN_TITLE = "My Chats"

@Composable
fun ChannelsScreenContainer(
    navigationActions: NavigationActions,
    channelsScreenContent: @Composable () -> Unit
) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(ChannelsScreen.SCREEN),
      topBar = {
        TopAppBar(
            title = SCREEN_TITLE,
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
