package com.android.periodpals.ui.timer

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.android.periodpals.resources.C.Tag.TimerScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TimerScreenTest {
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.TIMER)
    composeTestRule.setContent { TimerScreen(navigationActions) }
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.onNodeWithTag(TimerScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals("Tampon Timer")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()

    composeTestRule.onNodeWithTag(TimerScreen.DISPLAYED_TEXT).performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag(TimerScreen.CIRCULAR_PROGRESS_INDICATOR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TimerScreen.HOURGLASS).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TimerScreen.START_STOP_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TimerScreen.USEFUL_TIP).assertIsDisplayed()
  }

  @Test
  fun startStopButtonTogglesRightText() {

    val button = composeTestRule.onNodeWithTag(TimerScreen.START_STOP_BUTTON)
    val displayedText = composeTestRule.onNodeWithTag(TimerScreen.DISPLAYED_TEXT)

    // Assert initial state
    button.assertTextEquals("START")
    displayedText.assertTextEquals(TimerScreen.DISPLAYED_TEXT_ONE)

    button.performClick()

    // Assert state after starting
    button.assertTextEquals("STOP")
    displayedText.assertTextEquals(TimerScreen.DISPLAYED_TEXT_TWO)

    button.performClick()

    // Assert state after stopping
    button.assertTextEquals("START")
    displayedText.assertTextEquals(TimerScreen.DISPLAYED_TEXT_ONE)
  }
}
