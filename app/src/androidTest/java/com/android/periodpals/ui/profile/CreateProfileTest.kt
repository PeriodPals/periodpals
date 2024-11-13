package com.android.periodpals.ui.profile

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.C.Tag.ProfileScreens.CreateProfileScreen
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

  companion object {
    private val name = "John Doe"
    private val imageUrl = "https://example.com"
    private val description = "A short description"
    private val dob = "01/01/2000"
    private val userState =
        mutableStateOf(User(name = name, imageUrl = imageUrl, description = description, dob = dob))
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    userViewModel = mock(UserViewModel::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.CREATE_PROFILE)
  }

  @Test
  fun allComponentsAreDisplayed() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule.onNodeWithTag(CreateProfileScreen.SCREEN).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreens.PROFILE_PICTURE)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreens.MANDATORY_SECTION)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreens.YOUR_PROFILE_SECTION)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreens.SAVE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals("Save")
        .assertHasClickAction()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals("Create Your Account")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertDoesNotExist()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertDoesNotExist()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertDoesNotExist()
  }

  @Test
  fun testValidDateRecognition() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextInput("01/01/2000")
    assertTrue(validateDate("01/01/2000"))
    assertTrue(validateDate("31/12/1999"))
  }

  @Test
  fun testInvalidDateRecognition() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextInput("invalid_date")
    assertFalse(validateDate("32/01/2000")) // Invalid day
    assertFalse(validateDate("01/13/2000")) // Invalid month
    assertFalse(validateDate("01/01/abcd")) // Invalid year
    assertFalse(validateDate("01-01-2000")) // Invalid format
    assertFalse(validateDate("01/01")) // Incomplete date
  }

  @Test
  fun createInvalidProfileNoName() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(dob)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(description)
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performScrollTo().performClick()

    verify(userViewModel, never()).saveUser(any())
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidProfileNoDob() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(name)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(description)
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performScrollTo().performClick()

    verify(userViewModel, never()).saveUser(any())
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidProfileNoDescription() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(dob)
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(name)
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performScrollTo().performClick()

    verify(userViewModel, never()).saveUser(any())
    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createValidProfileVMFailure() {
    `when`(userViewModel.user).thenReturn(mutableStateOf(null))
    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(dob)
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(name)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(description)
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performScrollTo().performClick()

    verify(userViewModel).saveUser(any())
    verify(navigationActions, never()).navigateTo(Screen.PROFILE)
  }

  @Test
  fun createValidProfileVMSuccess() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(dob)
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(name)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(description)
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performScrollTo().performClick()

    verify(userViewModel).saveUser(any())
    verify(navigationActions).navigateTo(Screen.PROFILE)
  }
}
