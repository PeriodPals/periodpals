package com.android.periodpals.ui.profile

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopLevelDestination
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@RunWith(AndroidJUnit4::class)
class CreateProfileTest {
  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT)
    composeTestRule.setContent { MaterialTheme { CreateProfileScreen(navigationActions) } }
  }

  @Test
  fun testProfileImageDisplayed() {
    // Check if the profile image is displayed
    composeTestRule.onNodeWithTag("profile_image").assertIsDisplayed()
  }

  @Test
  fun testFormFieldsDisplayed() {
    // Check if the form fields are displayed
    composeTestRule.onNodeWithTag("email_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dob_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("name_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("description_field").assertIsDisplayed()
  }

  @Test
  fun testSaveButtonClickWithValidDate() {
    // Input valid date
    composeTestRule.onNodeWithTag("dob_field").performTextInput("01/01/2000")

    // Perform click on the save button
    // Cannot test navigation actions currently
    //    composeTestRule.onNodeWithTag("save_button").performClick()
    //    composeTestRule.waitForIdle()

    assertTrue(validateDate("01/01/2000"))
    assertTrue(validateDate("31/12/1999"))
  }

  @Test
  fun testSaveButtonClickWithInvalidDate() {
    // Input invalid date
    composeTestRule.onNodeWithTag("dob_field").performTextInput("invalid_date")

    // Perform click on the save button
    composeTestRule.onNodeWithTag("save_button").performClick()
    composeTestRule.waitForIdle()

    assertFalse(validateDate("32/01/2000")) // Invalid day
    assertFalse(validateDate("01/13/2000")) // Invalid month
    assertFalse(validateDate("01/01/abcd")) // Invalid year
    assertFalse(validateDate("01-01-2000")) // Invalid format
    assertFalse(validateDate("01/01")) // Incomplete date
  }

  @Test
  fun saveButton_doesNotNavigate_whenEmailNotFilled() {
    // Leave email empty
    composeTestRule.onNodeWithTag("dob_field").assertIsDisplayed().performTextInput("01/01/2000")
    composeTestRule.onNodeWithTag("name_field").assertIsDisplayed().performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag("description_field")
        .assertIsDisplayed()
        .performTextInput("A short bio")

    // Click the save button
    composeTestRule.onNodeWithTag("save_button").assertIsDisplayed().performClick()

    // Verify that the navigation action does not occur
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun saveButton_doesNotNavigate_whenDobNotFilled() {
    // Leave date of birth empty
    composeTestRule
        .onNodeWithTag("email_field")
        .assertIsDisplayed()
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag("name_field").assertIsDisplayed().performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag("description_field")
        .assertIsDisplayed()
        .performTextInput("A short bio")

    // Click the save button
    composeTestRule.onNodeWithTag("save_button").assertIsDisplayed().performClick()

    // Verify that the navigation action does not occur
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun saveButton_doesNotNavigate_whenNameNotFilled() {
    // Leave name empty
    composeTestRule
        .onNodeWithTag("email_field")
        .assertIsDisplayed()
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag("dob_field").assertIsDisplayed().performTextInput("01/01/2000")
    composeTestRule
        .onNodeWithTag("description_field")
        .assertIsDisplayed()
        .performTextInput("A short bio")

    // Click the save button
    composeTestRule.onNodeWithTag("save_button").assertIsDisplayed().performClick()

    // Verify that the navigation action does not occur
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun saveButton_doesNotNavigate_whenDescriptionNotFilled() {
    // Leave description empty
    composeTestRule
        .onNodeWithTag("email_field")
        .assertIsDisplayed()
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag("dob_field").assertIsDisplayed().performTextInput("01/01/2000")
    composeTestRule.onNodeWithTag("name_field").assertIsDisplayed().performTextInput("John Doe")

    // Click the save button
    composeTestRule.onNodeWithTag("save_button").assertIsDisplayed().performClick()

    // Verify that the navigation action does not occur
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun saveButton_navigates_whenAllFieldsAreFilled() {
    // Fill all fields
    composeTestRule
        .onNodeWithTag("email_field")
        .assertIsDisplayed()
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag("dob_field").assertIsDisplayed().performTextInput("01/01/2000")
    composeTestRule.onNodeWithTag("name_field").assertIsDisplayed().performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag("description_field")
        .assertIsDisplayed()
        .performTextInput("A short bio")

    // Click the save button
    composeTestRule.onNodeWithTag("save_button").assertIsDisplayed().performClick()

    // Verify that the navigation action occurs
    verify(navigationActions).navigateTo(screen = Screen.PROFILE)
  }
}
