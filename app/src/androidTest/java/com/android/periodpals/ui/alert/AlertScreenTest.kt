package com.android.periodpals.ui.alert

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class AlertScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testAlertScreenUI() {
    // Start the composable in the test environment
    composeTestRule.setContent { MaterialTheme { AlertScreen() } }

    // Check if the instruction text is displayed
    composeTestRule.onNodeWithTag("alertInstruction").assertIsDisplayed()

    // Check if the product dropdown is displayed and clickable
    composeTestRule
        .onNodeWithTag("alertProduct")
        .assertIsDisplayed()
        .performClick() // To expand the dropdown menu

    // Check if urgency dropdown is displayed and clickable
    composeTestRule.onNodeWithTag("alertUrgency").assertIsDisplayed().performClick()

    // Enter text in the location field
    composeTestRule
        .onNodeWithTag("alertLocation")
        .assertIsDisplayed()
        .performTextInput("Rolex Learning Center")

    // Enter text in the message field
    composeTestRule
        .onNodeWithTag("alertMessage")
        .assertIsDisplayed()
        .performTextInput("I need help finding a tampon! I'll be at rolex until 2pm")

    // Check if the submit button is displayed and perform a click
    composeTestRule
        .onNodeWithTag("alertSubmit")
        .assertIsDisplayed()
        .assertHasClickAction() // Ensure the button is clickable
  }
}
