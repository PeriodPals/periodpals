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
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.model.user.MAX_AGE
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.model.user.UserViewModel.Companion.DESCRIPTION_STATE_NAME
import com.android.periodpals.model.user.UserViewModel.Companion.DOB_STATE_NAME
import com.android.periodpals.model.user.UserViewModel.Companion.NAME_STATE_NAME
import com.android.periodpals.model.user.UserViewModel.Companion.PROFILE_IMAGE_STATE_NAME
import com.android.periodpals.model.user.isOldEnough
import com.android.periodpals.model.user.validateDate
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.C.Tag.ProfileScreens.EditProfileScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import com.dsc.form_builder.FormState
import com.dsc.form_builder.TextFieldState
import com.dsc.form_builder.Validators
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditProfileTest {

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
    private const val ERROR_TOO_YOUNG = "You must be at least $MAX_AGE years old"

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

    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)
    `when`(userViewModel.formState).thenReturn(formState)
  }

  @Test
  fun allComponentsAreDisplayed() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { EditProfileScreen(userViewModel, navigationActions) }

    composeTestRule.onNodeWithTag(EditProfileScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals("Edit Your Profile")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertDoesNotExist()

    composeTestRule
        .onNodeWithTag(ProfileScreens.PROFILE_PICTURE)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(EditProfileScreen.EDIT_PROFILE_PICTURE)
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
        .onNodeWithTag(ProfileScreens.SAVE_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals("Save")
        .assertHasClickAction()
  }

  @Test
  fun editValidProfileVMFailure() {
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

    org.mockito.kotlin.verify(userViewModel).saveUser(any(), any(), any())
    org.mockito.kotlin.verify(navigationActions, Mockito.never()).navigateTo(Screen.PROFILE)
  }

  @Test
  fun editValidProfileVMSuccess() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.uploadFile(any(), any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[2] as () -> Unit
      onSuccess()
    }
    `when`(userViewModel.saveUser(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as () -> Unit
      onSuccess()
    }
    composeTestRule.setContent { EditProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(name)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(dob)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(description)
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performScrollTo().performClick()

    verify(navigationActions).navigateTo(Screen.PROFILE)
  }

  @Test
  fun editInvalidProfileNoName() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { EditProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(dob)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(description)
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performScrollTo().performClick()

    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun editInvalidProfileNoDOB() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { EditProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(name)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(description)
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performScrollTo().performClick()

    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun editProfileDOBTooYoung() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { EditProfileScreen(userViewModel, navigationActions) }

    val tooYoungDate = LocalDate.now().minusYears(MAX_AGE).plusDays(1)

    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(name)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(tooYoungDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(description)
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performScrollTo().performClick()

    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun editInvalidProfileNoDescription() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { EditProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(name)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(dob)
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performScrollTo().performClick()

    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun editInvalidProfileAllEmptyFields() {
    `when`(userViewModel.user).thenReturn(userState)
    composeTestRule.setContent { EditProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performScrollTo().performClick()

    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun EditValidProfileVMAvatarFailure() {
    `when`(userViewModel.user).thenReturn(userState)
    `when`(userViewModel.uploadFile(any(), any(), any(), any())).thenAnswer {
      val onFailure = it.arguments[3] as (Exception) -> Unit
      onFailure(Exception("Error uploading file"))
    }
    `when`(userViewModel.saveUser(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as () -> Unit
      onSuccess()
    }
    composeTestRule.setContent { EditProfileScreen(userViewModel, navigationActions) }

    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(dob)
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(name)
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextClearance()
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performScrollTo()
        .performTextInput(description)
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performScrollTo().performClick()

    org.mockito.kotlin.verify(userViewModel).saveUser(any(), any(), any())

    org.mockito.kotlin.verify(navigationActions, Mockito.never()).navigateTo(Screen.PROFILE)
  }
}
