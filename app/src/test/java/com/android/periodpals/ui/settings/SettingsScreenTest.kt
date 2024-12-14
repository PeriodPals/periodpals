package com.android.periodpals.ui.settings

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.AuthenticationUserData
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.SettingsScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopLevelDestination
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var authenticationViewModel: AuthenticationViewModel
  private lateinit var userViewModel: UserViewModel
  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private val userData = mutableStateOf(AuthenticationUserData("uid", "email@epfl.com"))
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    authenticationViewModel = mock(AuthenticationViewModel::class.java)
    userViewModel = mock(UserViewModel::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.SETTINGS)
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.setContent {
      SettingsScreen(userViewModel, authenticationViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(SettingsScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals("My Settings")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertDoesNotExist()

    composeTestRule
        .onNodeWithTag(SettingsScreen.NOTIFICATIONS_CONTAINER)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SettingsScreen.THEME_CONTAINER)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SettingsScreen.ACCOUNT_MANAGEMENT_CONTAINER)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SettingsScreen.NOTIFICATIONS_DESCRIPTION)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.PALS_TEXT).performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.PALS_SWITCH).performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SettingsScreen.HORIZONTAL_DIVIDER)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.PADS_TEXT).performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.PADS_SWITCH).performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.TAMPONS_TEXT).performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SettingsScreen.TAMPONS_SWITCH)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.ORGANIC_TEXT).performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SettingsScreen.ORGANIC_SWITCH)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SettingsScreen.THEME_DROP_DOWN_MENU_BOX)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SettingsScreen.PASSWORD_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SettingsScreen.PASSWORD_ICON)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SettingsScreen.SIGN_OUT_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SettingsScreen.SIGN_OUT_ICON)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SettingsScreen.DELETE_ACCOUNT_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SettingsScreen.DELETE_ACCOUNT_ICON)
        .performScrollTo()
        .assertIsDisplayed()

    composeTestRule.onNodeWithTag(SettingsScreen.THEME_DROP_DOWN_MENU).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.DELETE_ACCOUNT_CARD).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.CARD_EMOJI_ICON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.CARD_TEXT).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.DELETE_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.NOT_DELETE_BUTTON).assertIsNotDisplayed()
  }

  @Test
  fun allCardsComponentsAreDisplayed() {
    composeTestRule.setContent {
      SettingsScreen(userViewModel, authenticationViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(SettingsScreen.DELETE_ACCOUNT_ICON)
        .performScrollTo()
        .performClick()

    composeTestRule.onNodeWithTag(SettingsScreen.DELETE_ACCOUNT_CARD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.CARD_EMOJI_ICON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.CARD_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.DELETE_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SettingsScreen.NOT_DELETE_BUTTON).assertIsDisplayed()
  }

  @Test
  fun goBackButtonNavigatesToProfileScreen() {
    composeTestRule.setContent {
      SettingsScreen(userViewModel, authenticationViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).performClick()

    verify(navigationActions).goBack()
  }

  @Test
  fun performClickOnDropDownMenu() {
    composeTestRule.setContent {
      SettingsScreen(userViewModel, authenticationViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(SettingsScreen.THEME_DROP_DOWN_MENU_BOX)
        .performScrollTo()
        .performClick()

    composeTestRule.onNodeWithTag(SettingsScreen.THEME_DROP_DOWN_MENU).performClick()
  }

  @Test
  fun notDeleteAccountButtonDismissDialog() {
    composeTestRule.setContent {
      SettingsScreen(userViewModel, authenticationViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(SettingsScreen.DELETE_ACCOUNT_ICON)
        .performScrollTo()
        .performClick()
    composeTestRule.onNodeWithTag(SettingsScreen.NOT_DELETE_BUTTON).performClick()

    composeTestRule.onNodeWithTag(SettingsScreen.DELETE_ACCOUNT_CARD).assertIsNotDisplayed()
  }

  @Test
  fun signOutVMFailure() {
    `when`(authenticationViewModel.logOut(any(), any())).thenAnswer {
      val onFailure = it.arguments[1] as (Exception) -> Unit
      onFailure(Exception("Error signing out user"))
    }
    composeTestRule.setContent {
      SettingsScreen(userViewModel, authenticationViewModel, navigationActions)
    }
    composeTestRule.onNodeWithTag(SettingsScreen.SIGN_OUT_ICON).performScrollTo().performClick()

    verify(authenticationViewModel).logOut(any(), any())

    verify(navigationActions, never()).navigateTo(any<String>())
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
  }

  @Test
  fun signOutVMSuccess() {
    `when`(authenticationViewModel.logOut(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as () -> Unit
      onSuccess()
    }
    composeTestRule.setContent {
      SettingsScreen(userViewModel, authenticationViewModel, navigationActions)
    }
    composeTestRule.onNodeWithTag(SettingsScreen.SIGN_OUT_ICON).performScrollTo().performClick()

    verify(authenticationViewModel).logOut(any(), any())

    verify(navigationActions).navigateTo(Screen.SIGN_IN)
  }

  @Test
  fun deleteAccountVMFailure() {
    `when`(authenticationViewModel.authUserData).thenReturn(userData)
    `when`(authenticationViewModel.loadAuthenticationUserData(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as () -> Unit
      onSuccess()
    }
    `when`(userViewModel.deleteUser(any(), any(), any())).thenAnswer {
      val onFailure = it.arguments[2] as (Exception) -> Unit
      onFailure(Exception("Error deleting user account"))
    }

    composeTestRule.setContent {
      SettingsScreen(userViewModel, authenticationViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(SettingsScreen.DELETE_ACCOUNT_ICON)
        .performScrollTo()
        .performClick()
    composeTestRule.onNodeWithTag(SettingsScreen.DELETE_BUTTON).performClick()

    verify(userViewModel).deleteUser(eq(userData.value.uid), any(), any())

    verify(navigationActions, never()).navigateTo(any<String>())
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
  }

  @Test
  fun deleteAccountVMSuccess() {
    `when`(authenticationViewModel.authUserData).thenReturn(userData)
    `when`(authenticationViewModel.loadAuthenticationUserData(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as () -> Unit
      onSuccess()
    }
    `when`(userViewModel.deleteUser(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as () -> Unit
      onSuccess()
    }

    composeTestRule.setContent {
      SettingsScreen(userViewModel, authenticationViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(SettingsScreen.DELETE_ACCOUNT_ICON)
        .performScrollTo()
        .performClick()
    composeTestRule.onNodeWithTag(SettingsScreen.DELETE_BUTTON).performClick()

    verify(userViewModel).deleteUser(eq(userData.value.uid), any(), any())

    verify(navigationActions).navigateTo(Screen.SIGN_IN)
  }
}
