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
import com.android.periodpals.model.user.UserAuthenticationState
import com.android.periodpals.resources.C.Tag.AuthenticationScreens
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignInScreen
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class SignInScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions
  private lateinit var authViewModel: AuthenticationViewModel

  companion object {
    private const val EMAIL = "test@example.com"
    private const val PASSWORD = "password"
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    authViewModel = mock(AuthenticationViewModel::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.SIGN_IN)
    `when`(authViewModel.userAuthenticationState)
        .thenReturn(mutableStateOf(UserAuthenticationState.Success("User is logged in")))
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
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SignInScreen.CONTINUE_WITH_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(SignInScreen.GOOGLE_BUTTON).performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SignInScreen.NOT_REGISTERED_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
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

    verify(authViewModel, never()).logInWithEmail(any(), any())
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

    verify(authViewModel, never()).logInWithEmail(any(), any())
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

    verify(authViewModel).logInWithEmail(eq(EMAIL), eq(PASSWORD))
  }

  @Test
  fun signUpHereNavigatesToSignUpScreen() {
    composeTestRule
        .onNodeWithTag(SignInScreen.NOT_REGISTERED_BUTTON)
        .performScrollTo()
        .performClick()

    verify(navigationActions).navigateTo(Screen.SIGN_UP)
  }
}
