package com.android.periodpals.ui.profile

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
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
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class ProfileScreenTest {

  private lateinit var navigationActions: NavigationActions
  private lateinit var userViewModel: UserViewModel
  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private val name = "John Doe"
    private val imageUrl = "https://example.com"
    private val description = "A short description"
    private val dob = "01/01/2000"
    private val userState =
        mutableStateOf(User(name = name, imageUrl = imageUrl, description = description, dob = dob))
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    userViewModel = mock(UserViewModel::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)
  }

  @Test
  fun allComponentsAreDisplayed() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { ProfileScreen(userViewModel, navigationActions) }

    composeTestRule.onNodeWithTag(ProfileScreen.SCREEN).assertIsDisplayed()
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
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals("Your Profile")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertDoesNotExist()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertDoesNotExist()
  }

  @Test
  fun editButtonNavigatesToEditProfileScreen() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { ProfileScreen(userViewModel, navigationActions) }

    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).performScrollTo().performClick()

    verify(navigationActions).navigateTo(Screen.EDIT_PROFILE)
  }

  @Test
  fun profileScreenHasCorrectContentVMSuccess() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { ProfileScreen(userViewModel, navigationActions) }

    composeTestRule.onNodeWithTag(ProfileScreen.NAME_FIELD).performScrollTo().assertTextEquals(name)
    composeTestRule
        .onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD)
        .performScrollTo()
        .assertTextEquals(description)
  }

  @Test
  fun profileScreenHasCorrectContentVMFailure() {
    `when`(userViewModel.user).thenReturn(mutableStateOf(null))
    composeTestRule.setContent { ProfileScreen(userViewModel, navigationActions) }

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
