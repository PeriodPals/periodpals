package com.android.periodpals.model.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.User
import com.android.periodpals.services.JwtTokenService
import io.getstream.chat.android.client.ChatClient

private const val TAG = "ChatViewModel"

/**
 * View model for the chat feature.
 *
 * @property chatClient The client used for connecting to the Stream Chat service.
 */
class ChatViewModel(private val chatClient: ChatClient) : ViewModel() {

  /**
   * Connects the user to the Stream Chat service.
   *
   * @param profile The user's profile information.
   * @param authenticationViewModel The ViewModel used for authentication.
   */
  fun connectUser(profile: User?, authenticationViewModel: AuthenticationViewModel) {
    if (profile == null || authenticationViewModel.authUserData.value == null) {
      Log.d(TAG, "Failed to connect user: profile or authentication data is null.")
      return
    }

    val uid = authenticationViewModel.authUserData.value!!.uid
    var token = ""
    JwtTokenService.generateStreamToken(
        uid, onSuccess = { token = it }, onFailure = { Log.d(TAG, "Failed to generate token.") })

    val userImage = profile.imageUrl.ifEmpty { "https://bit.ly/2TIt8NR" }
    val user =
        io.getstream.chat.android.models.User(id = uid, name = profile.name, image = userImage)

    chatClient.connectUser(user = user, token = token).enqueue()
    Log.d(TAG, "User connected successfully.")
  }
}
