package com.android.periodpals.ui.profile

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserViewModel
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
  private lateinit var userViewModel: UserViewModel

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    userViewModel = mock(UserViewModel::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.CREATE_PROFILE)
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

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
    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("01/01/2000")
    assertTrue(validateDate("01/01/2000"))
    assertTrue(validateDate("31/12/1999"))
  }

  @Test
  fun testInvalidDateRecognition() {
    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("invalid_date")
    assertFalse(validateDate("32/01/2000")) // Invalid day
    assertFalse(validateDate("01/13/2000")) // Invalid month
    assertFalse(validateDate("01/01/abcd")) // Invalid year
    assertFalse(validateDate("01-01-2000")) // Invalid format
    assertFalse(validateDate("01/01")) // Incomplete date
  }

  @Test
  fun createInvalidProfileNoEmail() {
    val userState =
        mutableStateOf(User("John Doe", "https://example.com", "A short bio", "01/01/2000"))
    `when`(userViewModel.user).thenReturn(userState)

    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("01/01/2000")
    composeTestRule.onNodeWithTag(CreateProfileScreen.NAME_FIELD).performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DESCRIPTION_FIELD)
        .performTextInput("A short bio")
    composeTestRule.onNodeWithTag(CreateProfileScreen.SAVE_BUTTON).performClick()

    verify(userViewModel, never()).saveUser(any())

    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidProfileNoDob() {
    val userState =
        mutableStateOf(User("John Doe", "https://example.com", "A short bio", "01/01/2000"))
    `when`(userViewModel.user).thenReturn(userState)

    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(CreateProfileScreen.EMAIL_FIELD)
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag(CreateProfileScreen.NAME_FIELD).performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DESCRIPTION_FIELD)
        .performTextInput("A short bio")
    composeTestRule.onNodeWithTag(CreateProfileScreen.SAVE_BUTTON).performClick()

    verify(userViewModel, never()).saveUser(any())

    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidProfileNoName() {
    val userState =
        mutableStateOf(User("John Doe", "https://example.com", "A short bio", "01/01/2000"))
    `when`(userViewModel.user).thenReturn(userState)

    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(CreateProfileScreen.EMAIL_FIELD)
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("01/01/2000")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DESCRIPTION_FIELD)
        .performTextInput("A short bio")
    composeTestRule.onNodeWithTag(CreateProfileScreen.SAVE_BUTTON).performClick()

    verify(userViewModel, never()).saveUser(any())

    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidProfileNoDescription() {
    val userState =
        mutableStateOf(User("John Doe", "https://example.com", "A short bio", "01/01/2000"))
    `when`(userViewModel.user).thenReturn(userState)

    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(CreateProfileScreen.EMAIL_FIELD)
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("01/01/2000")
    composeTestRule.onNodeWithTag(CreateProfileScreen.NAME_FIELD).performTextInput("John Doe")
    composeTestRule.onNodeWithTag(CreateProfileScreen.SAVE_BUTTON).performClick()

    verify(userViewModel, never()).saveUser(any())

    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createValidProfileVMFailure() {
    `when`(userViewModel.user).thenReturn(mutableStateOf(null))

    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(CreateProfileScreen.EMAIL_FIELD)
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("01/01/2000")
    composeTestRule.onNodeWithTag(CreateProfileScreen.NAME_FIELD).performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DESCRIPTION_FIELD)
        .performTextInput("A short bio")
    composeTestRule.onNodeWithTag(CreateProfileScreen.SAVE_BUTTON).performClick()

    verify(userViewModel).saveUser(any())

    verify(navigationActions, never()).navigateTo(Screen.PROFILE)
  }

  @Test
  fun createValidProfileVMSuccess() {
    val userState =
        mutableStateOf(User("John Doe", "https://example.com", "A short bio", "01/01/2000"))
    `when`(userViewModel.user).thenReturn(userState)

    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(CreateProfileScreen.EMAIL_FIELD)
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag(CreateProfileScreen.DOB_FIELD).performTextInput("01/01/2000")
    composeTestRule.onNodeWithTag(CreateProfileScreen.NAME_FIELD).performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag(CreateProfileScreen.DESCRIPTION_FIELD)
        .performTextInput("A short bio")
    composeTestRule.onNodeWithTag(CreateProfileScreen.SAVE_BUTTON).performClick()

    verify(userViewModel).saveUser(any())

    verify(navigationActions).navigateTo(Screen.PROFILE)
  }
}
