package com.android.periodpals.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.ProfileScreen
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
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)

    composeTestRule.setContent { ProfileScreen(navigationActions) }
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.onNodeWithTag(ProfileScreen.PROFILE_PICTURE).assertIsDisplayed()
    composeTestRule.onNodeWithTag(ProfileScreen.NAME_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(ProfileScreen.NO_REVIEWS_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsDisplayed()
  }

  @Test
  fun editButtonNavigatesToEditProfileScreen() {
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).performClick()
    verify(navigationActions).navigateTo(Screen.EDIT_PROFILE)
  }

  @Test
  fun profileScreenHasCorrectContent() {
    composeTestRule.onNodeWithTag(ProfileScreen.NAME_FIELD).assertTextEquals("Name")
    composeTestRule.onNodeWithTag(ProfileScreen.DESCRIPTION_FIELD).assertTextEquals("Description")
  }
}
