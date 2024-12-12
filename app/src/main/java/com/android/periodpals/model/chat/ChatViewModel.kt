package com.android.periodpals.model.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.User
import io.getstream.chat.android.client.ChatClient

private const val TAG = "ChatViewModel"

class ChatViewModel(private val chatClient: ChatClient) : ViewModel() {

  /**
   * Connects the user to the Stream Chat service.
   *
   * @param profile The user's profile information.
   * @param authenticationViewModel The ViewModel used for authentication.
   */
  fun connectUser(profile: User?, authenticationViewModel: AuthenticationViewModel) {
    Log.d(TAG, "User id: ${authenticationViewModel.authUserData.value?.uid}")
    val userImage =
        profile?.imageUrl?.ifEmpty { "https://bit.ly/2TIt8NR" } ?: "https://bit.ly/2TIt8NR"
    val user =
        io.getstream.chat.android.models.User(
            id = authenticationViewModel.authUserData.value?.uid ?: "",
            name = profile?.name ?: "Error loading name",
            image = userImage)
    var token = ""
    authenticationViewModel.getJwtToken(onSuccess = { token = it })
    chatClient.connectUser(user = user, token = token).enqueue()
    Log.d(TAG, "User connected successfully.")
  }

  //  private var currentChannel: ChannelClient? = null
  //
  //  fun initializeChannel(channelId: String) {
  //    currentChannel = chatClient.channel("messaging", channelId)
  //    currentChannel?.watch()?.enqueue { result ->
  //      if (result.isSuccess) {
  //        Log.d("ChatViewModel", "Channel connected successfully.")
  //      } else {
  //        Log.e("ChatViewModel", "Failed to connect to channel:")
  //      }
  //    }
  //  }
  //
  //  fun sendMessage(messageText: String) {
  //    currentChannel?.let { channel ->
  //      val message = Message(text = messageText)
  //      channel.sendMessage(message).enqueue { result: Result<Message> ->
  //        if (result.isSuccess) {
  //          Log.d("ChatViewModel", "Message sent successfully.")
  //        } else {
  //          Log.e("ChatViewModel", "Failed to send message:")
  //        }
  //      }
  //    } ?: Log.e("ChatViewModel", "Current channel is not initialized.")
  //  }
  //
  //  fun initializeStreamChat(userId: String) {
  //    val user = User(id = userId)
  //    chatClient
  //        .connectUser(
  //            user = user, token = "your-stream-token" // You need to generate this token
  //            )
  //        .enqueue { result ->
  //          if (result.isSuccess) {
  //            // User connected successfully
  //          } else {
  //            // Handle error
  //          }
  //        }
  //  }
}
