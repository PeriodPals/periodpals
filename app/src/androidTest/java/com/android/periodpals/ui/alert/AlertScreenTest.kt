package com.android.periodpals.ui.alert

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.verify

class AlertScreenTest {
  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT)
    composeTestRule.setContent { MaterialTheme { AlertScreen(navigationActions) } }
  }

  @Test
  fun displayAllComponents() {
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
    composeTestRule.onNodeWithTag("alertProduct").performClick()
    composeTestRule.onNodeWithTag("Pads").performClick()
    //        composeTestRule.onNodeWithTag("alertProduct").assertTextEquals("Pads")
    composeTestRule.onNodeWithTag("alertUrgency").performClick()
    composeTestRule.onNodeWithTag("!! Medium").performClick()
    //        composeTestRule.onNodeWithTag("alertUrgency").assertTextEquals("!! Medium")

    composeTestRule.onNodeWithTag("alertLocation").performTextInput("Rolex")
    composeTestRule.onNodeWithTag("alertMessage").performTextInput("I need help finding a tampon")

    // Cannot test navigation actions
    //    composeTestRule.onNodeWithTag("alertSubmit").performClick()
  }

  @Test
  fun askForHelpButton_doesNotNavigate_whenProductNotPicked() {
    // Leave product empty
    composeTestRule.onNodeWithTag("alertUrgency").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("!! Medium").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("alertLocation").assertIsDisplayed().performTextInput("Rolex")
    composeTestRule
        .onNodeWithTag("alertMessage")
        .assertIsDisplayed()
        .performTextInput("I need help finding a tampon")

    // Click the "Ask for Help" button
    composeTestRule.onNodeWithTag("alertSubmit").assertIsDisplayed().performClick()

    // Verify that the navigation action does not occur
    composeTestRule.onNodeWithTag("alertScreen").assertIsDisplayed()
  }

  @Test
  fun askForHelpButton_doesNotNavigate_whenUrgencyNotPicked() {
    // Leave urgency empty
    composeTestRule.onNodeWithTag("alertProduct").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("Pads").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("alertLocation").assertIsDisplayed().performTextInput("Rolex")
    composeTestRule
        .onNodeWithTag("alertMessage")
        .assertIsDisplayed()
        .performTextInput("I need help finding a tampon")

    // Click the "Ask for Help" button
    composeTestRule.onNodeWithTag("alertSubmit").assertIsDisplayed().performClick()

    // Verify that the navigation action does not occur
    composeTestRule.onNodeWithTag("alertScreen").assertIsDisplayed()
  }

  @Test
  fun askForHelpButton_doesNotNavigate_whenLocationNotFilled() {
    // Leave location empty
    composeTestRule.onNodeWithTag("alertProduct").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("Pads").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("alertUrgency").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("!! Medium").assertIsDisplayed().performClick()
    composeTestRule
        .onNodeWithTag("alertMessage")
        .assertIsDisplayed()
        .performTextInput("I need help finding a tampon")

    // Click the "Ask for Help" button
    composeTestRule.onNodeWithTag("alertSubmit").assertIsDisplayed().performClick()

    // Verify that the navigation action does not occur
    composeTestRule.onNodeWithTag("alertScreen").assertIsDisplayed()
  }

  @Test
  fun askForHelpButton_doesNotNavigate_whenMessageNotFilled() {
    // Leave message empty
    composeTestRule.onNodeWithTag("alertProduct").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("Pads").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("alertUrgency").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("!! Medium").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("alertLocation").assertIsDisplayed().performTextInput("Rolex")

    // Click the "Ask for Help" button
    composeTestRule.onNodeWithTag("alertSubmit").assertIsDisplayed().performClick()

    // Verify that the navigation action does not occur
    composeTestRule.onNodeWithTag("alertScreen").assertIsDisplayed()
  }

  @Test
  fun askForHelpButton_doesNotNavigate_whenAllFieldsAreEmpty() {
    // Click the "Ask for Help" button without filling any fields
    composeTestRule.onNodeWithTag("alertSubmit").assertIsDisplayed().performClick()

    // Verify that the navigation action does not occur
    composeTestRule.onNodeWithTag("alertScreen").assertIsDisplayed()
  }

  @Test
  fun askForHelpButton_navigates_whenAllFieldsAreFilled() {
    // Fill all fields
    composeTestRule.onNodeWithTag("alertProduct").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("Pads").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("alertUrgency").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("!! Medium").assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag("alertLocation").assertIsDisplayed().performTextInput("Rolex")
    composeTestRule
        .onNodeWithTag("alertMessage")
        .assertIsDisplayed()
        .performTextInput("I need help finding a tampon")

    // Click the "Ask for Help" button
    composeTestRule.onNodeWithTag("alertSubmit").assertIsDisplayed().performClick()

    // Verify that the navigation action occurs
    // This can be done by checking that the current screen is not the AlertScreen
    verify(navigationActions).navigateTo(screen = Screen.ALERT_LIST)
  }
}
