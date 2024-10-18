package com.android.periodpals.ui.alert

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.android.periodpals.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class AlertScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { MaterialTheme { AlertScreen(NavigationActions(rememberNavController())) } }

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
    composeTestRule.setContent { MaterialTheme { AlertScreen(NavigationActions(rememberNavController())) } }

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
}
