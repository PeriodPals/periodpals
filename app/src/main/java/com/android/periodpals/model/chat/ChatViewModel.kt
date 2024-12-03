package com.android.periodpals.model.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.channel.ChannelClient
import io.getstream.chat.android.models.Message
import io.getstream.result.Result

class ChatViewModel(private val chatClient: ChatClient) : ViewModel() {
  private var currentChannel: ChannelClient? = null

  fun initializeChannel(channelId: String) {
    currentChannel = chatClient.channel("messaging", channelId)
    currentChannel?.watch()?.enqueue { result ->
      if (result.isSuccess) {
        Log.d("ChatViewModel", "Channel connected successfully.")
      } else {
        Log.e("ChatViewModel", "Failed to connect to channel:")
      }
    }
  }

  fun sendMessage(messageText: String) {
    currentChannel?.let { channel ->
      val message = Message(text = messageText)
      channel.sendMessage(message).enqueue { result: Result<Message> ->
        if (result.isSuccess) {
          Log.d("ChatViewModel", "Message sent successfully.")
        } else {
          Log.e("ChatViewModel", "Failed to send message:")
        }
      }
    } ?: Log.e("ChatViewModel", "Current channel is not initialized.")
  }
}
