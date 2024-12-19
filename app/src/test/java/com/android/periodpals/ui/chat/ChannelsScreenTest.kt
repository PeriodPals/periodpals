package com.android.periodpals.ui.chat

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.resources.C.Tag.ChannelsScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class ChannelsScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.setContent {
      ChannelsScreenContainer(navigationActions) { Text("Channels Content") }
    }

    composeTestRule.onNodeWithTag(ChannelsScreen.SCREEN).assertExists()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals("My Chats")
    composeTestRule
        .onNodeWithTag(TopAppBar.GO_BACK_BUTTON)
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertIsNotDisplayed()

    composeTestRule.onNodeWithTag(ChannelsScreen.CHANNELS).assertExists()
  }

  @Test
  fun testBackButtonClick() {
    composeTestRule.setContent {
      ChannelsScreenContainer(navigationActions) { Text("Channels Content") }
    }

    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsDisplayed().performClick()

    verify(navigationActions).navigateTo(Screen.ALERT_LIST)
  }
}
