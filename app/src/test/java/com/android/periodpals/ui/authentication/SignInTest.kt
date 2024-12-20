package com.android.periodpals.ui.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.R
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.authentication.AuthenticationViewModel.Companion.EMAIL_STATE_NAME
import com.android.periodpals.model.authentication.AuthenticationViewModel.Companion.PASSWORD_LOGIN_STATE_NAME
import com.android.periodpals.model.user.UserAuthenticationState
import com.android.periodpals.resources.C.Tag.AuthenticationScreens
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignInScreen
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.dsc.form_builder.FormState
import com.dsc.form_builder.TextFieldState
import com.dsc.form_builder.Validators
import io.github.kakaocup.kakao.common.utilities.getResourceString
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

@Suppress("UNCHECKED_CAST")
@RunWith(RobolectricTestRunner::class)
class SignInScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions
  private lateinit var authViewModel: AuthenticationViewModel

  companion object {
    private const val EMAIL = "test@example.com"
    private const val PASSWORD = "password"

    private const val PASSWORD_MAX_LENGTH = 128
    private const val EMPTY_EMAIL_ERROR_MESSAGE = "Email cannot be empty"
    private const val EMPTY_PASSWORD_ERROR_MESSAGE = "Password cannot be empty"
    private const val TOO_LONG_PASSWORD_ERROR_MESSAGE =
        "Password must be at most $PASSWORD_MAX_LENGTH characters long"

    private val emailValidators =
        listOf(Validators.Email(), Validators.Required(message = EMPTY_EMAIL_ERROR_MESSAGE))
    private val passwordLoginValidators =
        listOf(
            Validators.Required(message = EMPTY_PASSWORD_ERROR_MESSAGE),
            Validators.Max(message = TOO_LONG_PASSWORD_ERROR_MESSAGE, limit = PASSWORD_MAX_LENGTH),
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
                        name = PASSWORD_LOGIN_STATE_NAME, validators = passwordLoginValidators),
                ))

    `when`(
            authViewModel.logInWithEmail(
                userEmail = any(),
                userPassword = any(),
                onSuccess = any(),
                onFailure = any(),
            ))
        .thenAnswer {
          val onSuccess = it.arguments[2] as () -> Unit
          onSuccess()
        }
    `when`(navigationActions.currentRoute()).thenReturn(Screen.SIGN_IN)
    `when`(authViewModel.userAuthenticationState)
        .thenReturn(mutableStateOf(UserAuthenticationState.Success("User is logged in")))
    `when`(authViewModel.formState).thenReturn(formState)

    composeTestRule.setContent { SignInScreen(authViewModel, navigationActions) }
  }

  @Test
  fun allComponentsAreDisplayed() {

    composeTestRule.onNodeWithTag(SignInScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertDoesNotExist()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertDoesNotExist()
    composeTestRule.onNodeWithTag(AuthenticationScreens.BACKGROUND).assertIsDisplayed()

    composeTestRule
        .onNodeWithTag(AuthenticationScreens.WELCOME_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SignInScreen.INSTRUCTION_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.sign_in_instruction))
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
        .onNodeWithTag(SignInScreen.SIGN_IN_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.sign_in_button_text))
    composeTestRule
        .onNodeWithTag(SignInScreen.NOT_REGISTERED_NAV_LINK)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.sign_in_sign_up_text))
  }

  @Test
  fun emptyEmailShowsCorrectError() {

    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performScrollTo()
        .performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).performScrollTo().performClick()
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
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).performScrollTo().performClick()

    verify(authViewModel, never()).logInWithEmail(any(), any(), any(), any())
  }

  @Test
  fun emptyPasswordShowsCorrectError() {

    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .performScrollTo()
        .performTextInput(EMAIL)
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).performScrollTo().performClick()
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
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).performScrollTo().performClick()

    verify(authViewModel, never()).logInWithEmail(any(), any(), any(), any())
  }

  @Test
  fun validSignInAttemptNavigatesToProfileScreen() {

    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .performScrollTo()
        .performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performScrollTo()
        .performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).performScrollTo().performClick()

    verify(navigationActions).navigateTo(Screen.PROFILE)
  }

  @Test
  fun validSignInAttemptCallsVMLogInWithEmail() {
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.EMAIL_FIELD)
        .performScrollTo()
        .performTextInput(EMAIL)
    composeTestRule
        .onNodeWithTag(AuthenticationScreens.PASSWORD_FIELD)
        .performScrollTo()
        .performTextInput(PASSWORD)
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).performScrollTo().performClick()

    verify(authViewModel).logInWithEmail(eq(EMAIL), eq(PASSWORD), any(), any())
  }

  @Test
  fun signUpHereNavigatesToSignUpScreen() {
    composeTestRule
        .onNodeWithTag(SignInScreen.NOT_REGISTERED_NAV_LINK)
        .performScrollTo()
        .performClick()

    verify(navigationActions).navigateTo(Screen.SIGN_UP)
  }
}
