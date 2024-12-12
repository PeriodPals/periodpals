package com.android.periodpals.ui.profile

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
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
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProfileScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var userViewModel: UserViewModel
  private lateinit var pushNotificationsService: PushNotificationsService
  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private val name = "John Doe"
    private val imageUrl = "https://example.com"
    private val description = "A short description"
    private val dob = "01/01/2000"
    private val userState =
        mutableStateOf(User(name = name, imageUrl = imageUrl, description = description, dob = dob))
    private val userAvatar = mutableStateOf(byteArrayOf())
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    userViewModel = mock(UserViewModel::class.java)
    pushNotificationsService = mock(PushNotificationsService::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)
  }

  @Test
  fun allComponentsAreDisplayed() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.avatar).thenReturn(userAvatar)
    composeTestRule.setContent {
      ProfileScreen(userViewModel, pushNotificationsService, navigationActions)
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
      ProfileScreen(userViewModel, pushNotificationsService, navigationActions)
    }

    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).performClick()

    verify(navigationActions).navigateTo(Screen.SETTINGS)
  }

  @Test
  fun editButtonNavigatesToEditProfileScreen() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.avatar).thenReturn(userAvatar)

    composeTestRule.setContent {
      ProfileScreen(userViewModel, pushNotificationsService, navigationActions)
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
      ProfileScreen(userViewModel, pushNotificationsService, navigationActions)
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
      ProfileScreen(userViewModel, pushNotificationsService, navigationActions)
    }
    org.mockito.kotlin.verify(navigationActions, Mockito.never()).navigateTo(Screen.PROFILE)
  }

  @Test
  fun profileScreenHasCorrectContentVMSuccess() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.avatar).thenReturn(userAvatar)

    composeTestRule.setContent {
      ProfileScreen(userViewModel, pushNotificationsService, navigationActions)
    }

    composeTestRule.onNodeWithTag(ProfileScreen.NAME_FIELD).performScrollTo().assertTextEquals(name)
    composeTestRule
        .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
        .performScrollTo()
        .assertTextEquals(description)
  }

  @Test
  fun profileScreenHasCorrectContentVMFailure() {
    `when`(userViewModel.user).thenReturn(mutableStateOf(null))
    `when`(userViewModel.avatar).thenReturn(userAvatar)
    composeTestRule.setContent {
      ProfileScreen(userViewModel, pushNotificationsService, navigationActions)
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
}
