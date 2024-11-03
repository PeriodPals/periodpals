package com.android.periodpals.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.resources.C.Tag.CreateProfileScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
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
    composeTestRule.setContent { CreateProfileScreen(navigationActions) }
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.onNodeWithTag(CreateProfileScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateProfileScreen.PROFILE_PICTURE).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateProfileScreen.EMAIL_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateProfileScreen.NAME_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateProfileScreen.DESCRIPTION_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateProfileScreen.SAVE_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals("Create Your Account")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
  }

  @Test
  fun testSaveButtonClickWithValidDate() {
    // Input valid date
    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("01/01/2000")

    // Perform click on the save button
    // Cannot test navigation actions currently
    //    composeTestRule.onNodeWithTag(CreateProfileScreen.SAVE_BUTTON).performClick()
    //    composeTestRule.waitForIdle()

    assertTrue(validateDate("01/01/2000"))
    assertTrue(validateDate("31/12/1999"))
  }

  @Test
  fun testSaveButtonClickWithInvalidDate() {
    // Input invalid date
    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("invalid_date")

    // Perform click on the save button
    composeTestRule.onNodeWithTag(CreateProfileScreen.SAVE_BUTTON).performClick()
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
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DOB_FIELD)
        .assertIsDisplayed()
        .performTextInput("01/01/2000")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.NAME_FIELD)
        .assertIsDisplayed()
        .performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DESCRIPTION_FIELD)
        .assertIsDisplayed()
        .performTextInput("A short bio")

    // Click the save button
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.SAVE_BUTTON)
        .assertIsDisplayed()
        .performClick()

    // Verify that the navigation action does not occur
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun saveButton_doesNotNavigate_whenDobNotFilled() {
    // Leave date of birth empty
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.EMAIL_FIELD)
        .assertIsDisplayed()
        .performTextInput("john.doe@example.com")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.NAME_FIELD)
        .assertIsDisplayed()
        .performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DESCRIPTION_FIELD)
        .assertIsDisplayed()
        .performTextInput("A short bio")

    // Click the save button
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.SAVE_BUTTON)
        .assertIsDisplayed()
        .performClick()

    // Verify that the navigation action does not occur
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun saveButton_doesNotNavigate_whenNameNotFilled() {
    // Leave name empty
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.EMAIL_FIELD)
        .assertIsDisplayed()
        .performTextInput("john.doe@example.com")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DOB_FIELD)
        .assertIsDisplayed()
        .performTextInput("01/01/2000")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DESCRIPTION_FIELD)
        .assertIsDisplayed()
        .performTextInput("A short bio")

    // Click the save button
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.SAVE_BUTTON)
        .assertIsDisplayed()
        .performClick()

    // Verify that the navigation action does not occur
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun saveButton_doesNotNavigate_whenDescriptionNotFilled() {
    // Leave description empty
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.EMAIL_FIELD)
        .assertIsDisplayed()
        .performTextInput("john.doe@example.com")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DOB_FIELD)
        .assertIsDisplayed()
        .performTextInput("01/01/2000")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.NAME_FIELD)
        .assertIsDisplayed()
        .performTextInput("John Doe")

    // Click the save button
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.SAVE_BUTTON)
        .assertIsDisplayed()
        .performClick()

    // Verify that the navigation action does not occur
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun saveButton_navigates_whenAllFieldsAreFilled() {
    // Fill all fields
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.EMAIL_FIELD)
        .assertIsDisplayed()
        .performTextInput("john.doe@example.com")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DOB_FIELD)
        .assertIsDisplayed()
        .performTextInput("01/01/2000")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.NAME_FIELD)
        .assertIsDisplayed()
        .performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DESCRIPTION_FIELD)
        .assertIsDisplayed()
        .performTextInput("A short bio")

    // Click the save button
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.SAVE_BUTTON)
        .assertIsDisplayed()
        .performClick()

    // Verify that the navigation action occurs
    verify(navigationActions).navigateTo(screen = Screen.PROFILE)
  }
}
