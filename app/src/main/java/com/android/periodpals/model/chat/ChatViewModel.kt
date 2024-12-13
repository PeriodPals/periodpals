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
   * @param onSuccess Callback function to be called on successful connection.
   * @param onFailure Callback function to be called on connection failure, with the exception as a
   *   parameter.
   */
  fun connectUser(
      profile: User?,
      authenticationViewModel: AuthenticationViewModel,
      onSuccess: () -> Unit = { Log.d(TAG, "User connected successfully.") },
      onFailure: (Exception) -> Unit = { Log.d(TAG, "Failed to connect user: ${it.message}") },
  ) {
    if (profile == null || authenticationViewModel.authUserData.value == null) {
      Log.d(TAG, "Failed to connect user: profile or authentication data is null.")
      onFailure(RuntimeException("Profile or authentication data is null."))
      return
    }

    val uid = authenticationViewModel.authUserData.value!!.uid
    JwtTokenService.generateStreamToken(
        uid = uid,
        onSuccess = {
          val token = it
          val userImage = profile.imageUrl.ifEmpty { "https://bit.ly/2TIt8NR" }
          val user =
              io.getstream.chat.android.models.User(
                  id = uid, name = profile.name, image = userImage)

          chatClient.connectUser(user = user, token = token).execute()
          Log.d(TAG, "User connected successfully.")
          onSuccess()
        },
        onFailure = {
          Log.d(TAG, "Failed to generate token.")
          onFailure(it)
        })
  }

  /**
   * Creates a channel between the current user and a pal.
   *
   * @param myUid The current user's UID.
   * @param palUid The pal's UID.
   */
  fun createChannel(myUid: String, palUid: String): String {
    Log.d(TAG, "Creating channel between $myUid and $palUid.")
    val channelId = generateChannelId(myUid, palUid)
    val channelType = "messaging"
    val channelCid = generateCid(channelType, channelId)
    chatClient
        .createChannel(
            channelType = channelType,
            channelId = channelId,
            memberIds = listOf(myUid, palUid),
            extraData = mapOf("name" to "New Chat") // Change to Pal's name
            )
        .enqueue { result ->
          if (result.isSuccess) {
            Log.d(TAG, "Channel created successfully!")
          } else {
            Log.e(TAG, "Failed to create channel!")
          }
        }
    return channelCid
  }

  private fun generateChannelId(myUid: String, palUid: String): String {
    val sortedUids = listOf(myUid, palUid).sorted()
    return sortedUids
        .joinToString(separator = "") // Removed the separator (no underscores)
        .replace("-", "") // Remove dashes if any
  }

  private fun generateCid(channelType: String, channelId: String): String {
    // Ensure the channelType and channelId are not empty
    require(channelType.isNotEmpty()) { "channelType must not be empty" }
    require(channelId.isNotEmpty()) { "channelId must not be empty" }

    // Generate the cid in the format channelType:channelId
    return listOf(channelType, channelId).joinToString(separator = ":")
  }
}
