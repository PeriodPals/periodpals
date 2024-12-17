package com.android.periodpals.ui.chat

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.android.periodpals.ChannelActivity
import com.android.periodpals.resources.C
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar

private const val SCREEN_TITLE = "My Chats"
private const val CHANNEL_SCREEN_TITLE = "Your Chats"

@Composable
fun ChannelsScreen(context: Context, navigationActions: NavigationActions) {
  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(C.Tag.ChannelsScreen.SCREEN),
      topBar = {
        TopAppBar(
            title = SCREEN_TITLE,
            backButton = true,
            onBackButtonClick = { navigationActions.navigateTo(Screen.ALERT_LIST) })
      },
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
  ) { paddingValues ->
    Column(modifier = Modifier.padding(paddingValues)) {
      io.getstream.chat.android.compose.ui.channels.ChannelsScreen(
          title = CHANNEL_SCREEN_TITLE,
          isShowingHeader = false,
          onChannelClick = { channel ->
            val intent = ChannelActivity.getIntent(context, channel.cid)
            context.startActivity(intent)
          },
          onBackPressed = { navigationActions.navigateTo(Screen.ALERT_LIST) },
      )
    }
  }
}
