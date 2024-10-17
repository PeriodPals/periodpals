package com.android.periodpals.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class ProfileScreenTest {
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    // Mock the current route to the Alert List screen
    `when`(navigationActions.currentRoute()).thenReturn(Screen.ALERT_LIST)
  }

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { ProfileScreen(navigationActions) }
    composeTestRule.onNodeWithTag("profileAvatar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Description").assertIsDisplayed()
    composeTestRule.onNodeWithTag("reviewOne").assertIsDisplayed()
    composeTestRule.onNodeWithTag("reviewTwo").assertIsDisplayed()
  }

  @Test
  fun profileScreen_hasCorrectContent() {
    composeTestRule.setContent { ProfileScreen(navigationActions) }
    composeTestRule.onNodeWithTag("profileName").assertTextEquals("Name")
    composeTestRule.onNodeWithTag("Description").assertTextEquals("Description")
  }
}
