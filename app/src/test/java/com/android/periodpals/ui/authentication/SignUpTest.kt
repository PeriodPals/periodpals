package com.android.periodpals.ui.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.authentication.AuthenticationViewModel.Companion.CONFIRM_PASSWORD_STATE_NAME
import com.android.periodpals.model.authentication.AuthenticationViewModel.Companion.EMAIL_STATE_NAME
import com.android.periodpals.model.authentication.AuthenticationViewModel.Companion.PASSWORD_SIGNUP_STATE_NAME
import com.android.periodpals.model.user.UserAuthenticationState
import com.android.periodpals.resources.C.Tag.AuthenticationScreens
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignUpScreen
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.dsc.form_builder.FormState
import com.dsc.form_builder.TextFieldState
import com.dsc.form_builder.Validators
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SignUpScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions
  private lateinit var authViewModel: AuthenticationViewModel

  companion object {
    private const val EMAIL = "test@example.com"
    private const val INVALID_EMAIL = "invalidEmail"
    private const val PASSWORD = "Passw0rd*"
    private const val PSW_TOO_SHORT = "Sh0rt*"
    private const val PSW_NO_CAPITAL = "passw0rd*"
    private const val PSW_NO_MINUSCULE = "PASSW0RD*"
    private const val PSW_NO_NUMBER = "Password*"
    private const val PSW_NO_SPECIAL = "Passw0rd"
    private const val PSW_DO_NOT_MATCH_1 = "Password1*"
    private const val PSW_DO_NOT_MATCH_2 = "Password2*"

    private const val PASSWORD_MIN_LENGTH = 8
    private const val PASSWORD_MAX_LENGTH = 128
    private const val EMPTY_EMAIL_ERROR_MESSAGE = "Email cannot be empty"
    private const val EMPTY_PASSWORD_ERROR_MESSAGE = "Password cannot be empty"
    private const val TOO_SHORT_PASSWORD_ERROR_MESSAGE =
        "Password must be at least $PASSWORD_MIN_LENGTH characters long"
    private const val TOO_LONG_PASSWORD_ERROR_MESSAGE =
        "Password must be at most $PASSWORD_MAX_LENGTH characters long"
    private const val NO_CAPITAL_PASSWORD_ERROR_MESSAGE =
        "Password must contain at least one capital letter"
    private const val NO_LOWER_CASE_PASSWORD_ERROR_MESSAGE =
        "Password must contain at least one lower case letter"
    private const val NO_NUMBER_PASSWORD_ERROR_MESSAGE = "Password must contain at least one number"
    private const val NO_SPECIAL_CHAR_PASSWORD_ERROR_MESSAGE =
        "Password must contain at least one special character"

    private val emailValidators =
        listOf(Validators.Email(), Validators.Required(message = EMPTY_EMAIL_ERROR_MESSAGE))
    private val passwordSignupValidators =
        listOf(
            Validators.Min(message = TOO_SHORT_PASSWORD_ERROR_MESSAGE, limit = PASSWORD_MIN_LENGTH),
            Validators.Max(message = TOO_LONG_PASSWORD_ERROR_MESSAGE, limit = PASSWORD_MAX_LENGTH),
            Validators.Custom(
                message = NO_CAPITAL_PASSWORD_ERROR_MESSAGE,
                function = { Regex(".*[A-Z].*").containsMatchIn(it as String) },
            ),
            Validators.Custom(
                message = NO_LOWER_CASE_PASSWORD_ERROR_MESSAGE,
                function = { Regex(".*[a-z].*").containsMatchIn(it as String) },
            ),
            Validators.Custom(
                message = NO_NUMBER_PASSWORD_ERROR_MESSAGE,
                function = { Regex(".*[0-9].*").containsMatchIn(it as String) },
            ),
            Validators.Custom(
                message = NO_SPECIAL_CHAR_PASSWORD_ERROR_MESSAGE,
                function = { Regex(".*[!@#\$%^&*(),.?\":{}|<>].*").containsMatchIn(it as String) },
            ),
            Validators.Required(message = EMPTY_PASSWORD_ERROR_MESSAGE),
        )
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    authViewModel = mock(AuthenticationViewModel::class.java)
    val formState =
        FormState(
            fields =
                listOf(
                    TextFieldState(name = EMAIL_STATE_NAME, validators = emailValidators),
                    TextFieldState(
                        name = PASSWORD_SIGNUP_STATE_NAME,
                        validators = passwordSignupValidators,
                    ),
                    TextFieldState(
                        name = CONFIRM_PASSWORD_STATE_NAME,
                        validators = passwordSignupValidators,
                    ),
                ))

    `when`(
            authViewModel.signUpWithEmail(
                userEmail = any(),
                userPassword = any(),
                onSuccess = any(),
                onFailure = any(),
            ))
        .thenAnswer {
          val onSuccess = it.arguments[2] as () -> Unit
          onSuccess()
        }
    `when`(navigationActions.currentRoute()).thenReturn(Screen.SIGN_UP)
    `when`(authViewModel.userAuthenticationState)
        .thenReturn(mutableStateOf(UserAuthenticationState.Success("User is signed up")))
    `when`(authViewModel.formState).thenReturn(formState)

    composeTestRule.setContent { SignUpScreen(authViewModel, navigationActions) }
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.onNodeWithTag(SignUpScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertDoesNotExist()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertDoesNotExist()
    composeTestRule.onNodeWithTag(AuthenticationScreens.BACKGROUND).assertIsDisplayed()

    composeTestRule
        .onNodeWithTag(AuthenticationScreens.WELCOME_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SignUpScreen.INSTRUCTION_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_VISIBILITY_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_VISIBILITY_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performScrollTo().assertIsDisplayed()
  }

  @Test
  fun emptyEmailShowsCorrectError() {
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performScrollTo()
        .performTextInput(PASSWORD)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performScrollTo()
        .performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performScrollTo().performClick()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_ERROR_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_ERROR_TEXT)
        .performScrollTo()
        .assertTextEquals("Email cannot be empty")
  }

  @Test
  fun emptyEmailDoesNotCallVM() {
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performScrollTo()
        .performTextInput(PASSWORD)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performScrollTo()
        .performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performScrollTo().performClick()

    verify(authViewModel, never()).signUpWithEmail(any(), any(), any(), any())
  }

  @Test
  fun invalidEmailShowsCorrectError() {
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .performScrollTo()
        .performTextInput(INVALID_EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performScrollTo()
        .performTextInput(PASSWORD)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performScrollTo()
        .performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performScrollTo().performClick()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_ERROR_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_ERROR_TEXT)
        .performScrollTo()
        .assertTextEquals("Invalid email address")
  }

  @Test
  fun invalidEmailDoesNotCallVM() {
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .performScrollTo()
        .performTextInput(INVALID_EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performScrollTo()
        .performTextInput(PASSWORD)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performScrollTo()
        .performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performScrollTo().performClick()

    verify(authViewModel, never()).signUpWithEmail(any(), any(), any(), any())
  }

  @Test
  fun emptyPasswordShowsCorrectError() {
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .performScrollTo()
        .performTextInput(EMAIL)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performScrollTo().performClick()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_ERROR_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_ERROR_TEXT)
        .performScrollTo()
        .assertTextEquals("Password cannot be empty")
  }

  @Test
  fun emptyPasswordDoesNotCallVM() {
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .performScrollTo()
        .performTextInput(EMAIL)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performScrollTo().performClick()

    verify(authViewModel, never()).signUpWithEmail(any(), any(), any(), any())
  }

  @Test
  fun emptyConfirmedPasswordShowsCorrectError() {
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .performScrollTo()
        .performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performScrollTo()
        .performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performScrollTo().performClick()
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_ERROR_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_ERROR_TEXT)
        .assertTextEquals("Passwords do not match")
  }

  @Test
  fun emptyConfirmedPasswordDoesNotCallVM() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule.onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD).performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()

    verify(authViewModel, never()).signUpWithEmail(any(), any(), any(), any())
  }

  @Test
  fun emptyPasswordOnlyShowsCorrectError() {
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .performScrollTo()
        .performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performScrollTo()
        .performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_ERROR_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_ERROR_TEXT)
        .assertTextEquals("Password cannot be empty")
  }

  @Test
  fun emptyPasswordOnlyDoesNotCallVM() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule.onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD).performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    verify(authViewModel, never()).signUpWithEmail(any(), any(), any(), any())
  }

  @Test
  fun tooShortPasswordShowsCorrectError() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performTextInput(PSW_TOO_SHORT)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performTextInput(PSW_TOO_SHORT)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    composeTestRule.onNodeWithTag(AuthenticationScreens.PASSWORD_ERROR_TEXT).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_ERROR_TEXT)
        .assertTextEquals("Password must be at least 8 characters long")
  }

  @Test
  fun tooShortPasswordDoesNotCallVM() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performTextInput(PSW_TOO_SHORT)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performTextInput(PSW_TOO_SHORT)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    verify(authViewModel, never()).signUpWithEmail(any(), any(), any(), any())
  }

  @Test
  fun noCapitalPasswordShowsCorrectError() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performTextInput(PSW_NO_CAPITAL)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performTextInput(PSW_NO_CAPITAL)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    composeTestRule.onNodeWithTag(AuthenticationScreens.PASSWORD_ERROR_TEXT).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_ERROR_TEXT)
        .assertTextEquals("Password must contain at least one capital letter")
  }

  @Test
  fun noCapitalPasswordDoesNotCallVM() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performTextInput(PSW_NO_CAPITAL)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performTextInput(PSW_NO_CAPITAL)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    verify(authViewModel, never()).signUpWithEmail(any(), any(), any(), any())
  }

  @Test
  fun noMinusculePasswordShowsCorrectError() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performTextInput(PSW_NO_MINUSCULE)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performTextInput(PSW_NO_MINUSCULE)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    composeTestRule.onNodeWithTag(AuthenticationScreens.PASSWORD_ERROR_TEXT).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_ERROR_TEXT)
        .assertTextEquals("Password must contain at least one lower case letter")
  }

  @Test
  fun noMinusculePasswordDoesNotCallVM() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performTextInput(PSW_NO_MINUSCULE)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performTextInput(PSW_NO_MINUSCULE)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    verify(authViewModel, never()).signUpWithEmail(any(), any(), any(), any())
  }

  @Test
  fun noNumberPasswordShowsCorrectError() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performTextInput(PSW_NO_NUMBER)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performTextInput(PSW_NO_NUMBER)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    composeTestRule.onNodeWithTag(AuthenticationScreens.PASSWORD_ERROR_TEXT).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_ERROR_TEXT)
        .assertTextEquals("Password must contain at least one number")
  }

  @Test
  fun noNumberPasswordDoesNotCallVM() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performTextInput(PSW_NO_NUMBER)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performTextInput(PSW_NO_NUMBER)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    verify(authViewModel, never()).signUpWithEmail(any(), any(), any(), any())
  }

  @Test
  fun noSpecialPasswordShowsCorrectError() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performTextInput(PSW_NO_SPECIAL)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performTextInput(PSW_NO_SPECIAL)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    composeTestRule.onNodeWithTag(AuthenticationScreens.PASSWORD_ERROR_TEXT).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_ERROR_TEXT)
        .assertTextEquals("Password must contain at least one special character")
  }

  @Test
  fun noSpecialPasswordDoesNotCallVM() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performTextInput(PSW_NO_SPECIAL)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performTextInput(PSW_NO_SPECIAL)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    verify(authViewModel, never()).signUpWithEmail(any(), any(), any(), any())
  }

  @Test
  fun doNotMatchPasswordShowsCorrectError() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performTextInput(PSW_DO_NOT_MATCH_1)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performTextInput(PSW_DO_NOT_MATCH_2)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    composeTestRule.onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_ERROR_TEXT).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_ERROR_TEXT)
        .assertTextEquals("Passwords do not match")
  }

  @Test
  fun doNotMatchPasswordDoesNotCallVM() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performTextInput(PSW_DO_NOT_MATCH_1)
    composeTestRule
        .onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD)
        .performTextInput(PSW_DO_NOT_MATCH_2)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    verify(authViewModel, never()).signUpWithEmail(any(), any(), any(), any())
  }

  @Test
  fun validSignUpAttemptNavigatesToCreateProfileScreen() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule.onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD).performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD).performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    verify(navigationActions).navigateTo(Screen.CREATE_PROFILE)
  }

  @Test
  fun validSignUpAttemptCallsVMLogInWithEmail() {
    composeTestRule.onNodeWithTag(AuthenticationScreens.EMAIL_FIELD).performTextInput(EMAIL)
    composeTestRule.onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD).performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignUpScreen.CONFIRM_PASSWORD_FIELD).performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignUpScreen.SIGN_UP_BUTTON).performClick()
    verify(authViewModel).signUpWithEmail(eq(EMAIL), eq(PASSWORD), any(), any())
  }
}
