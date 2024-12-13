package com.android.periodpals.ui.profile

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.chat.ChatViewModel
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.C.Tag.ProfileScreens.ProfileScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.services.PushNotificationsService
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.never
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProfileScreenTest {

  @Mock private lateinit var userViewModel: UserViewModel
  @Mock private lateinit var authenticationViewModel: AuthenticationViewModel
  @Mock private lateinit var navigationActions: NavigationActions
  @Mock private lateinit var pushNotificationsService: PushNotificationsService
  @Mock private lateinit var chatViewModel: ChatViewModel
  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private const val CHAT_VM_TAG = "ChatViewModel"
    private const val NAME = "John Doe"
    private const val IMAGE_URL = "https://example.com"
    private const val DESCRIPTION = "A short description"
    private const val dob = "01/01/2000"
    private val userState =
        mutableStateOf(
            User(
                name = NAME,
                imageUrl = IMAGE_URL,
                description = DESCRIPTION,
                dob = dob,
                preferredDistance = 1))
    private val userAvatar = mutableStateOf(byteArrayOf())
  }

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)
  }

  @Test
  fun allComponentsAreDisplayed() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.avatar).thenReturn(userAvatar)
    composeTestRule.setContent {
      ProfileScreen(
          userViewModel,
          authenticationViewModel,
          pushNotificationsService,
          chatViewModel,
          navigationActions)
    }

    composeTestRule.onNodeWithTag(ProfileScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals("Your Profile")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertIsDisplayed()

    composeTestRule
        .onNodeWithTag(ProfileScreens.PROFILE_PICTURE)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(ProfileScreen.NAME_FIELD).performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreen.CONTRIBUTION_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreen.REVIEWS_SECTION)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals("Reviews")
    composeTestRule
        .onNodeWithTag(ProfileScreen.NO_REVIEWS_ICON)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreen.NO_REVIEWS_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreen.NO_REVIEWS_CARD)
        .performScrollTo()
        .assertIsDisplayed()
  }

  @Test
  fun settingsButtonNavigatesToSettingsScreen() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.avatar).thenReturn(userAvatar)
    composeTestRule.setContent {
      ProfileScreen(
          userViewModel,
          authenticationViewModel,
          pushNotificationsService,
          chatViewModel,
          navigationActions)
    }

    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).performClick()

    verify(navigationActions).navigateTo(Screen.SETTINGS)
  }

  @Test
  fun editButtonNavigatesToEditProfileScreen() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.avatar).thenReturn(userAvatar)

    composeTestRule.setContent {
      ProfileScreen(
          userViewModel,
          authenticationViewModel,
          pushNotificationsService,
          chatViewModel,
          navigationActions)
    }

    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).performClick()

    verify(navigationActions).navigateTo(Screen.EDIT_PROFILE)
  }

  @Test
  fun initVmSuccess() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.avatar).thenReturn(userAvatar)

    `when`(userViewModel.init())
        .thenAnswer({
          val onSuccess = it.arguments[0] as () -> Unit
          onSuccess()
        })
    composeTestRule.setContent {
      ProfileScreen(
          userViewModel,
          authenticationViewModel,
          pushNotificationsService,
          chatViewModel,
          navigationActions)
    }
    org.mockito.kotlin.verify(navigationActions, Mockito.never()).navigateTo(Screen.PROFILE)
  }

  @Test
  fun initVmFailure() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.avatar).thenReturn(userAvatar)

    `when`(userViewModel.init())
        .thenAnswer({
          val onFailure = it.arguments[1] as () -> Unit
          onFailure()
        })
    composeTestRule.setContent {
      ProfileScreen(
          userViewModel,
          authenticationViewModel,
          pushNotificationsService,
          chatViewModel,
          navigationActions)
    }
    org.mockito.kotlin.verify(navigationActions, Mockito.never()).navigateTo(Screen.PROFILE)
  }

  @Test
  fun profileScreenHasCorrectContentVMSuccess() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.avatar).thenReturn(userAvatar)

    composeTestRule.setContent {
      ProfileScreen(
          userViewModel,
          authenticationViewModel,
          pushNotificationsService,
          chatViewModel,
          navigationActions)
    }

    composeTestRule.onNodeWithTag(ProfileScreen.NAME_FIELD).performScrollTo().assertTextEquals(NAME)
    composeTestRule
        .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
        .performScrollTo()
        .assertTextEquals(DESCRIPTION)
  }

  @Test
  fun profileScreenHasCorrectContentVMFailure() {
    `when`(userViewModel.user).thenReturn(mutableStateOf(null))
    `when`(userViewModel.avatar).thenReturn(userAvatar)
    composeTestRule.setContent {
      ProfileScreen(
          userViewModel,
          authenticationViewModel,
          pushNotificationsService,
          chatViewModel,
          navigationActions)
    }

    composeTestRule
        .onNodeWithTag(ProfileScreen.NAME_FIELD)
        .performScrollTo()
        .assertTextEquals("Error loading name, try again later.")
    composeTestRule
        .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
        .performScrollTo()
        .assertTextEquals("Error loading description, try again later.")
  }

  @Test
  fun loadAndConnectClient() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.avatar).thenReturn(userAvatar)

    doAnswer { invocation ->
          val onSuccess = invocation.arguments[0] as () -> Unit
          onSuccess()
        }
        .`when`(authenticationViewModel)
        .loadAuthenticationUserData(any(), any())
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[2] as () -> Unit
          onSuccess()
        }
        .`when`(chatViewModel)
        .connectUser(any(), any(), any(), any())

    composeTestRule.setContent {
      ProfileScreen(
          userViewModel,
          authenticationViewModel,
          pushNotificationsService,
          chatViewModel,
          navigationActions,
      )
    }

    verify(authenticationViewModel).loadAuthenticationUserData(any(), any())
    verify(chatViewModel).connectUser(any(), any(), any(), any())
  }

  @Test
  fun loadFailsCannotConnectClient() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.avatar).thenReturn(userAvatar)

    doAnswer { invocation ->
          val onFailure = invocation.arguments[1] as (Exception) -> Unit
          onFailure(RuntimeException("Failed to load user data"))
        }
        .`when`(authenticationViewModel)
        .loadAuthenticationUserData(any(), any())
    doAnswer { invocation ->
          val onSuccess = invocation.arguments[2] as () -> Unit
          onSuccess()
        }
        .`when`(chatViewModel)
        .connectUser(any(), any(), any(), any())

    composeTestRule.setContent {
      ProfileScreen(
          userViewModel,
          authenticationViewModel,
          pushNotificationsService,
          chatViewModel,
          navigationActions,
      )
    }

    verify(authenticationViewModel).loadAuthenticationUserData(any(), any())
    verify(chatViewModel, never()).connectUser(any(), any(), any(), any())
  }

  @Test
  fun loadAndThenConnectClientFails() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.avatar).thenReturn(userAvatar)

    doAnswer { invocation ->
          val onSuccess = invocation.arguments[0] as () -> Unit
          onSuccess()
        }
        .`when`(authenticationViewModel)
        .loadAuthenticationUserData(any(), any())
    doAnswer { invocation ->
          val onFailure = invocation.arguments[3] as (Exception) -> Unit
          onFailure(RuntimeException("Failed to connect user"))
        }
        .`when`(chatViewModel)
        .connectUser(any(), any(), any(), any())

    composeTestRule.setContent {
      ProfileScreen(
          userViewModel,
          authenticationViewModel,
          pushNotificationsService,
          chatViewModel,
          navigationActions,
      )
    }

    verify(authenticationViewModel).loadAuthenticationUserData(any(), any())
    verify(chatViewModel).connectUser(any(), any(), any(), any())
  }
}
