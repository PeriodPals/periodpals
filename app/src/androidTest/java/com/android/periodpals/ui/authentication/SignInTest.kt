package com.android.periodpals.ui.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.UserAuthState
import com.android.periodpals.resources.C.Tag.SignInScreen
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
    private const val email = "test@example.com"
    private const val password = "password"
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    authViewModel = mock(AuthenticationViewModel::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.SIGN_IN)
    `when`(authViewModel.userAuthState)
        .thenReturn(mutableStateOf(UserAuthState.Success("User is logged in")))
    composeTestRule.setContent { SignInScreen(authViewModel, navigationActions) }
  }

  @Test
  fun allComponentsAreDisplayed() {

    composeTestRule.onNodeWithTag(SignInScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SignInScreen.BACKGROUND).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SignInScreen.TITLE_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SignInScreen.INSTRUCTION_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SignInScreen.EMAIL_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SignInScreen.PASSWORD_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SignInScreen.PASSWORD_VISIBILITY_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SignInScreen.CONTINUE_WITH_TEXT).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SignInScreen.GOOGLE_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(SignInScreen.NOT_REGISTERED_BUTTON).assertIsDisplayed()
  }

  @Test
  fun emptyEmailShowsCorrectError() {

    composeTestRule.onNodeWithTag(SignInScreen.PASSWORD_FIELD).performTextInput(password)
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).performClick()
    composeTestRule.onNodeWithTag(SignInScreen.EMAIL_ERROR_TEXT).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SignInScreen.EMAIL_ERROR_TEXT)
        .assertTextEquals("Email cannot be empty")
  }

  @Test
  fun emptyEmailDoesNotCallVM() {

    composeTestRule.onNodeWithTag(SignInScreen.PASSWORD_FIELD).performTextInput(password)
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).performClick()
    verify(authViewModel, never()).logInWithEmail(any(), any())
  }

  @Test
  fun emptyPasswordShowsCorrectError() {

    composeTestRule.onNodeWithTag(SignInScreen.EMAIL_FIELD).performTextInput(email)
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).performClick()
    composeTestRule.onNodeWithTag(SignInScreen.PASSWORD_ERROR_TEXT).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SignInScreen.PASSWORD_ERROR_TEXT)
        .assertTextEquals("Password cannot be empty")
  }

  @Test
  fun emptyPasswordDoesNotCallVM() {

    composeTestRule.onNodeWithTag(SignInScreen.EMAIL_FIELD).performTextInput(email)
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).performClick()
    verify(authViewModel, never()).logInWithEmail(any(), any())
  }

  @Test
  fun validSignInAttemptNavigatesToProfileScreen() {

    composeTestRule.onNodeWithTag(SignInScreen.EMAIL_FIELD).performTextInput(email)
    composeTestRule.onNodeWithTag(SignInScreen.PASSWORD_FIELD).performTextInput(password)
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).performClick()
    verify(navigationActions).navigateTo(Screen.PROFILE)
  }

  @Test
  fun validSignInAttemptCallsVMLogInWithEmail() {
    composeTestRule.onNodeWithTag(SignInScreen.EMAIL_FIELD).performTextInput(email)
    composeTestRule.onNodeWithTag(SignInScreen.PASSWORD_FIELD).performTextInput(password)
    composeTestRule.onNodeWithTag(SignInScreen.SIGN_IN_BUTTON).performClick()
    verify(authViewModel).logInWithEmail(eq(email), eq(password))
  }

  @Test
  fun signUpHereNavigatesToSignUpScreen() {
    composeTestRule.onNodeWithTag(SignInScreen.NOT_REGISTERED_BUTTON).performClick()
    verify(navigationActions).navigateTo(Screen.SIGN_UP)
  }
}
