package com.android.periodpals.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateProfileTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun testProfileImageDisplayed() {
    composeTestRule.setContent { CreateProfile() }

    // Check if the profile image is displayed
    composeTestRule.onNodeWithTag("profile_image").assertIsDisplayed()
  }

  @Test
  fun testFormFieldsDisplayed() {
    composeTestRule.setContent { CreateProfile() }

    // Check if the form fields are displayed
    composeTestRule.onNodeWithTag("email_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dob_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("name_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("description_field").assertIsDisplayed()
  }

  @Test
  fun testSaveButtonClickWithValidDate() {
    composeTestRule.setContent { CreateProfile() }

    // Input valid date
    composeTestRule.onNodeWithTag("dob_field").performTextInput("01/01/2000")

    // Perform click on the save button
    composeTestRule.onNodeWithTag("save_button").performClick()
    composeTestRule.waitForIdle()

    // Add assertions to verify the behavior after clicking the save button with valid date
    // For example, you can check if the date is correctly parsed and saved
  }

  @Test
  fun testSaveButtonClickWithInvalidDate() {
    composeTestRule.setContent { CreateProfile() }

    // Input invalid date
    composeTestRule.onNodeWithTag("dob_field").performTextInput("invalid_date")

    // Perform click on the save button
    composeTestRule.onNodeWithTag("save_button").performClick()
    composeTestRule.waitForIdle()
  }
}
