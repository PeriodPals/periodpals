package com.android.periodpals.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.android.periodpals.model.chat.ChatViewModel
import io.getstream.chat.android.client.ChatClient

private const val TAG = "ChatScreen"

@Composable
fun ChatScreen(chatClient: ChatClient, channelId: String, chatViewModel: ChatViewModel) {
  // Initialize or use the channel from the ViewModel
  chatViewModel.initializeChannel(channelId)

  // Placeholder UI for displaying messages (to be replaced with actual implementation)
  Column {
    Text(text = "Welcome to the chat!")

    // Input field for sending a message
    BasicTextField(
        value = "", // Replace with a state variable
        onValueChange = { /* Update message state */},
        singleLine = true)

    Button(
        onClick = {
          val messageContent = "Your message content" // Replace with actual user input
          chatViewModel.sendMessage(messageContent)
        }) {
          Text(text = "Send")
        }
  }
}

@Composable
fun BasicTextField(value: String, onValueChange: () -> Unit, singleLine: Boolean) {
  TODO("Not yet implemented")
}
/*
fun setupChannel(chatClient: ChatClient, channelId: String) {
    val channel = chatClient.channel("messaging", channelId)

    // Connect to the channel and set up message listeners
    channel.watch().enqueue { result ->
        if (result.isSuccess) {
            Log.d(TAG, "Channel connected successfully.")
        } else {
            Log.e(TAG, "Failed to connect to channel.")
        }
    }
}

fun subscribeToMessages(channel: Channel) {
    channel.setMessageListener { event: Event ->
        if (event.type == Event.Type.MESSAGE_NEW) {
            val message = event.message
            Log.d(TAG, "New message received: ${message.text}")
            // Update your UI or message list state accordingly
        }
    }
}
*/
