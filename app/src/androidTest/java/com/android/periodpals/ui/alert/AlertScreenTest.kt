package com.android.periodpals.ui.alert

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class AlertScreenTest {
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
    composeTestRule.setContent { MaterialTheme { AlertScreen(navigationActions) } }

    composeTestRule.onNodeWithTag("alertInstruction").assertIsDisplayed()
    composeTestRule.onNodeWithTag("alertProduct").assertIsDisplayed()
    composeTestRule.onNodeWithTag("alertUrgency").assertIsDisplayed()
    composeTestRule.onNodeWithTag("alertLocation").assertIsDisplayed()
    composeTestRule.onNodeWithTag("alertMessage").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("alertSubmit")
        .assertIsDisplayed()
        .assertTextEquals("Ask for Help")
  }

  @Test
  fun interactWithComponents() {
    composeTestRule.setContent { MaterialTheme { AlertScreen(navigationActions) } }

    composeTestRule.onNodeWithTag("alertProduct").performClick()
    composeTestRule.onNodeWithTag("Pads").performClick()
    //        composeTestRule.onNodeWithTag("alertProduct").assertTextEquals("Pads")
    composeTestRule.onNodeWithTag("alertUrgency").performClick()
    composeTestRule.onNodeWithTag("!! Medium").performClick()
    //        composeTestRule.onNodeWithTag("alertUrgency").assertTextEquals("!! Medium")

    composeTestRule.onNodeWithTag("alertLocation").performTextInput("Rolex")
    composeTestRule.onNodeWithTag("alertMessage").performTextInput("I need help finding a tampon")

    composeTestRule.onNodeWithTag("alertSubmit").performClick()
  }
}
