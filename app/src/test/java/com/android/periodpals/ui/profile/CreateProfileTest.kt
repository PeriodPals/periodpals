package com.android.periodpals.ui.profile

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.R
import com.android.periodpals.model.user.MIN_AGE
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.model.user.UserViewModel.Companion.DESCRIPTION_STATE_NAME
import com.android.periodpals.model.user.UserViewModel.Companion.DOB_STATE_NAME
import com.android.periodpals.model.user.UserViewModel.Companion.NAME_STATE_NAME
import com.android.periodpals.model.user.UserViewModel.Companion.PROFILE_IMAGE_STATE_NAME
import com.android.periodpals.model.user.isOldEnough
import com.android.periodpals.model.user.validateDate
import com.android.periodpals.resources.C.Tag.AlertListsScreen
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.C.Tag.ProfileScreens.CreateProfileScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopLevelDestination
import com.dsc.form_builder.FormState
import com.dsc.form_builder.TextFieldState
import com.dsc.form_builder.Validators
import io.github.kakaocup.kakao.common.utilities.getResourceString
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CreateProfileTest {
  private lateinit var navigationActions: NavigationActions
  private lateinit var userViewModel: UserViewModel

  @get:Rule val composeTestRule = createComposeRule()

  companion object {
    private val name = "John Doe"
    private val imageUrl = "https://example.com"
    private val description = "A short description"
    private val dob = "01/01/2000"
    private val preferredDistance = 500
    private val userState =
        mutableStateOf(
            User(
                name = name,
                imageUrl = imageUrl,
                description = description,
                dob = dob,
                preferredDistance = preferredDistance,
            ))

    private const val MAX_NAME_LENGTH = 128
    private const val MAX_DESCRIPTION_LENGTH = 512

    private const val ERROR_INVALID_NAME = "Please enter a name"
    private const val ERROR_NAME_TOO_LONG = "Name must be less than $MAX_NAME_LENGTH characters"
    private const val ERROR_INVALID_DESCRIPTION = "Please enter a description"
    private const val ERROR_DESCRIPTION_TOO_LONG =
        "Description must be less than $MAX_DESCRIPTION_LENGTH characters"
    private const val ERROR_INVALID_DOB = "Please enter a valid date"
    private const val ERROR_TOO_YOUNG = "You must be at least $MIN_AGE years old"

    private val nameValidators =
        listOf(
            Validators.Required(message = ERROR_INVALID_NAME),
            Validators.Max(message = ERROR_NAME_TOO_LONG, limit = MAX_NAME_LENGTH),
        )
    private val descriptionValidators =
        listOf(
            Validators.Required(message = ERROR_INVALID_DESCRIPTION),
            Validators.Max(message = ERROR_DESCRIPTION_TOO_LONG, limit = MAX_DESCRIPTION_LENGTH),
        )
    private val dobValidators =
        listOf(
            Validators.Required(message = ERROR_INVALID_DOB),
            Validators.Custom(
                message = ERROR_INVALID_DOB, function = { validateDate(it as String) }),
            Validators.Custom(message = ERROR_TOO_YOUNG, function = { isOldEnough(it as String) }),
        )
    private val profileImageValidators = emptyList<Validators>()
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    userViewModel = mock(UserViewModel::class.java)
    val formState =
        FormState(
            fields =
                listOf(
                    TextFieldState(name = NAME_STATE_NAME, validators = nameValidators),
                    TextFieldState(
                        name = DESCRIPTION_STATE_NAME, validators = descriptionValidators),
                    TextFieldState(name = DOB_STATE_NAME, validators = dobValidators),
                    TextFieldState(
                        name = PROFILE_IMAGE_STATE_NAME, validators = profileImageValidators),
                ))

    `when`(navigationActions.currentRoute()).thenReturn(Screen.CREATE_PROFILE)
    `when`(userViewModel.formState).thenReturn(formState)
    `when`(userViewModel.avatar).thenReturn(mutableStateOf(null))
  }

  @Test
  fun allComponentsAreDisplayed() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    composeTestRule.onNodeWithTag(CreateProfileScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.create_profile_screen_title))
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertDoesNotExist()

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
        .onNodeWithTag(ProfileScreens.DOB_MIN_AGE_TEXT)
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
        .onNodeWithTag(CreateProfileScreen.FILTER_RADIUS_EXPLANATION_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.create_profile_radius_explanation_text))
    composeTestRule
        .onNodeWithTag(AlertListsScreen.FILTER_RADIUS_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AlertListsScreen.FILTER_RADIUS_SLIDER)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreens.SAVE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals("Save")
        .assertHasClickAction()
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

    verify(userViewModel, never()).saveUser(any(), any(), any())

    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidProfileTooYoung() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { CreateProfileScreen(userViewModel, navigationActions) }

    val tooYoungDate = LocalDate.now().minusYears(MIN_AGE).plusDays(1)

    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(tooYoungDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(name)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(description)
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performScrollTo().performClick()

    verify(userViewModel, never()).saveUser(any(), any(), any())

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

    verify(userViewModel, never()).saveUser(any(), any(), any())

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

    verify(userViewModel, never()).saveUser(any(), any(), any())

    verify(navigationActions, never()).navigateTo(any<TopLevelDestination>())
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createValidProfileVMFailure() {
    `when`(userViewModel.user).thenReturn(mutableStateOf(null))
    `when`(userViewModel.saveUser(any(), any(), any())).thenAnswer {
      val onFailure = it.arguments[2] as (Exception) -> Unit
      onFailure(Exception("Error saving user"))
    }
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

    verify(userViewModel).saveUser(any(), any(), any())

    verify(navigationActions, never()).navigateTo(Screen.PROFILE)
  }

  @Test
  fun createValidProfileVMSuccess() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.uploadFile(any(), any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[2] as () -> Unit
      onSuccess()
    }
    `when`(userViewModel.saveUser(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as () -> Unit
      onSuccess()
    }

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

    verify(userViewModel).saveUser(any(), any(), any())

    verify(navigationActions).navigateTo(Screen.PROFILE)
  }

  @Test
  fun createValidProfileVMAvatarFailure() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.uploadFile(any(), any(), any(), any())).thenAnswer {
      val onFailure = it.arguments[3] as (Exception) -> Unit
      onFailure(Exception("Error uploading file"))
    }
    `when`(userViewModel.saveUser(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as () -> Unit
      onSuccess()
    }
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

    verify(userViewModel).saveUser(any(), any(), any())

    verify(navigationActions, never()).navigateTo(Screen.PROFILE)
  }
}
