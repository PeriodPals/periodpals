package com.android.periodpals.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.CreateProfileScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
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
    `when`(navigationActions.currentRoute()).thenReturn(Screen.CREATE_PROFILE)
    composeTestRule.setContent { CreateProfileScreen(navigationActions) }
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.onNodeWithTag(CreateProfileScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateProfileScreen.PROFILE_PICTURE).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateProfileScreen.MANDATORY_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateProfileScreen.EMAIL_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(CreateProfileScreen.PROFILE_TEXT).assertIsDisplayed()
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
    composeTestRule
        .onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU)
        .assertIsNotDisplayed()
  }

  @Test
  fun testValidDateRecognition() {
    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("01/01/2000")
    assertTrue(validateDate("01/01/2000"))
    assertTrue(validateDate("31/12/1999"))
  }

  @Test
  fun testInvalidDateRecognition() {
    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("invalid_date")
    assertFalse(validateDate("32/01/2000")) // Invalid day
    assertFalse(validateDate("01/13/2000")) // Invalid month
    assertFalse(validateDate("01/01/abcd")) // Invalid year
    assertFalse(validateDate("01-01-2000")) // Invalid format
    assertFalse(validateDate("01/01")) // Incomplete date
  }

  @Test
  fun createInvalidProfileNoEmail() {
    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("01/01/2000")
    composeTestRule.onNodeWithTag(CreateProfileScreen.NAME_FIELD).performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DESCRIPTION_FIELD)
        .performTextInput("A short bio")
    composeTestRule.onNodeWithTag(CreateProfileScreen.SAVE_BUTTON).performClick()

    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidProfileNoDob() {
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.EMAIL_FIELD)
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag(CreateProfileScreen.NAME_FIELD).performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DESCRIPTION_FIELD)
        .performTextInput("A short bio")
    composeTestRule.onNodeWithTag(CreateProfileScreen.SAVE_BUTTON).performClick()

    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidProfileNoName() {
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.EMAIL_FIELD)
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("01/01/2000")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DESCRIPTION_FIELD)
        .performTextInput("A short bio")
    composeTestRule.onNodeWithTag(CreateProfileScreen.SAVE_BUTTON).performClick()

    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidProfileNoDescription() {
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.EMAIL_FIELD)
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("01/01/2000")
    composeTestRule.onNodeWithTag(CreateProfileScreen.NAME_FIELD).performTextInput("John Doe")
    composeTestRule.onNodeWithTag(CreateProfileScreen.SAVE_BUTTON).performClick()

    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createValidProfile() {
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.EMAIL_FIELD)
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("01/01/2000")
    composeTestRule.onNodeWithTag(CreateProfileScreen.NAME_FIELD).performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DESCRIPTION_FIELD)
        .performTextInput("A short bio")
    composeTestRule.onNodeWithTag(CreateProfileScreen.SAVE_BUTTON).performClick()

    verify(navigationActions).navigateTo(Screen.PROFILE)
  }
}
