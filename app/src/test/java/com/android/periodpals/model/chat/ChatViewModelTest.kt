package com.android.periodpals.model.chat

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.android.periodpals.MainCoroutineRule
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.AuthenticationUserData
import com.android.periodpals.model.user.User
import com.android.periodpals.services.JwtTokenService
import io.getstream.chat.android.client.ChatClient
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

  @get:Rule val mainCoroutineRule: TestRule = MainCoroutineRule()

  private lateinit var chatViewModel: ChatViewModel
  private lateinit var chatClient: ChatClient
  private lateinit var authenticationViewModel: AuthenticationViewModel

  private val profile =
      mutableStateOf(User(name = NAME, imageUrl = IMAGE_URL, description = DESCRIPTION, dob = DOB))
  private val authUserData = mutableStateOf(AuthenticationUserData(uid = UID, email = EMAIL))

  companion object {
    private const val TAG = "ChatViewModel"
    private const val UID = "uid"
    private const val EMAIL = "email"
    private const val NAME = "name"
    private const val IMAGE_URL = "imageUrl"
    private const val DESCRIPTION = "description"
    private const val DOB = "31/01/1999"
  }

  @Before
  fun setUp() {
    chatClient = mockk(relaxed = true)
    authenticationViewModel = mockk(relaxed = true)
    mockkObject(JwtTokenService)
    chatViewModel = ChatViewModel(chatClient)
    mockkStatic(Log::class)

    every { authenticationViewModel.authUserData } returns authUserData
  }

  @Test
  fun `connectUser should log success when user is connected`() {
    every { JwtTokenService.generateStreamToken(UID, any(), any()) } answers
        {
          secondArg<(String) -> Unit>().invoke("generated_token")
        }

    chatViewModel.connectUser(profile.value, authenticationViewModel)

    verify { Log.d(TAG, "User connected successfully.") }
  }

  @Test
  fun `connectUser should log error when authentication data is null`() {
    every { authenticationViewModel.authUserData } returns mutableStateOf(null)

    chatViewModel.connectUser(profile.value, authenticationViewModel)

    verify { Log.d(TAG, "Failed to connect user: profile or authentication data is null.") }
  }

  @SuppressLint("CheckResult")
  @Test
  fun `connectUser should generate token and connect user successfully`() {
    every { JwtTokenService.generateStreamToken(UID, any(), any()) } answers
        {
          secondArg<(String) -> Unit>().invoke("generated_token")
        }

    chatViewModel.connectUser(profile.value, authenticationViewModel)

    val expectedUser =
        io.getstream.chat.android.models.User(id = UID, name = NAME, image = IMAGE_URL)
    verify { chatClient.connectUser(expectedUser, "generated_token") }
    verify { Log.d(TAG, "User connected successfully.") }
  }

  @Test
  fun `connectUser should log error when token generation fails`() {
    every { JwtTokenService.generateStreamToken(UID, any(), any()) } answers
        {
          thirdArg<(Exception) -> Unit>().invoke(Exception("Failed to generate token."))
        }

    chatViewModel.connectUser(profile.value, authenticationViewModel)

    verify { Log.d(TAG, "Failed to generate token.") }
  }
}