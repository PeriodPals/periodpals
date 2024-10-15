package com.android.periodpals.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displayAllComponents() {
    composeTestRule.setContent { ProfileScreen() }
    composeTestRule.onNodeWithTag("profileAvatar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Description").assertIsDisplayed()
    composeTestRule.onNodeWithTag("reviewOne").assertIsDisplayed()
    composeTestRule.onNodeWithTag("reviewTwo").assertIsDisplayed()
  }

  @Test
  fun profileScreen_hasCorrectContent() {
    composeTestRule.setContent { ProfileScreen() }
    composeTestRule.onNodeWithTag("profileName").assertExists("Displayed Name")
    composeTestRule.onNodeWithTag("Description").assertExists("Description")
  }
}
