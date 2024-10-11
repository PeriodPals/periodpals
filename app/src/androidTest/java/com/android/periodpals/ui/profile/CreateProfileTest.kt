package com.android.periodpals.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
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

  /**
   * @Test fun testSaveButtonDisplayed() { composeTestRule.setContent { CreateProfile() }
   *
   * // Check if the Save button is displayed
   * composeTestRule.onNodeWithTag("save_button").assertIsDisplayed() }
   */
  @Test
  fun testProfileImageClick() {
    composeTestRule.setContent { CreateProfile() }

    // Perform click on the profile image
    composeTestRule.onNodeWithTag("profile_image").performClick()

    // Add assertions to verify the behavior after clicking the profile image
    // For example, you can check if the image picker intent is launched
  }
}
