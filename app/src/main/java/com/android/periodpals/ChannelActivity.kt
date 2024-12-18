package com.android.periodpals

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory

/**
 * Activity that displays a chat channel. It uses the [MessagesScreen] to display the messages of a
 * channel.
 */
class ChannelActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Load the ID of the selected channel
    val channelId = intent.getStringExtra(KEY_CHANNEL_ID)!!

    // Add the MessagesScreen to your UI
    setContent {
      ChatTheme {
        MessagesScreen(
            viewModelFactory =
                MessagesViewModelFactory(context = this, channelId = channelId, messageLimit = 30),
            onBackPressed = { finish() })
      }
    }
  }

  // Create an intent to start this Activity, with a given channelId
  companion object {
    private const val KEY_CHANNEL_ID = "channelId"

    fun getIntent(context: Context, channelId: String): Intent {
      return Intent(context, ChannelActivity::class.java).apply {
        putExtra(KEY_CHANNEL_ID, channelId)
      }
    }
  }
}
