package com.android.periodpals.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class CreateProfileTest {
  private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    // Mock the current route to the Alert List screen
    `when`(navigationActions.currentRoute()).thenReturn(Screen.ALERT_LIST)
  }
  @Test
  fun testProfileImageDisplayed() {
    composeTestRule.setContent { CreateProfileScreen(navigationActions) }

    // Check if the profile image is displayed
    composeTestRule.onNodeWithTag("profile_image").assertIsDisplayed()
  }

  @Test
  fun testFormFieldsDisplayed() {
    composeTestRule.setContent { CreateProfileScreen(navigationActions) }

    // Check if the form fields are displayed
    composeTestRule.onNodeWithTag("email_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("dob_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("name_field").assertIsDisplayed()
    composeTestRule.onNodeWithTag("description_field").assertIsDisplayed()
  }

  @Test
  fun testSaveButtonClickWithValidDate() {
    composeTestRule.setContent { CreateProfileScreen(navigationActions) }

    // Input valid date
    composeTestRule.onNodeWithTag("dob_field").performTextInput("01/01/2000")

    // Perform click on the save button
    composeTestRule.onNodeWithTag("save_button").performClick()
    composeTestRule.waitForIdle()

    assertTrue(validateDate("01/01/2000"))
    assertTrue(validateDate("31/12/1999"))
  }

  @Test
  fun testSaveButtonClickWithInvalidDate() {
    composeTestRule.setContent { CreateProfileScreen(navigationActions) }

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
}
