package com.android.periodpals.ui.authentication

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.model.auth.AuthViewModel
import com.android.periodpals.model.user.UserAuthState
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
  private lateinit var authViewModel: AuthViewModel

  companion object {
    private const val email = "test@example.com"
    private const val password = "password"
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    authViewModel = mock(AuthViewModel::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Screen.SIGN_IN)
    `when`(authViewModel.userAuthState)
        .thenReturn(mutableStateOf(UserAuthState.Success("User is logged in")))
    composeTestRule.setContent { SignInScreen(authViewModel, navigationActions) }
  }

  @Test
  fun allComponentsAreDisplayed() {

    composeTestRule.onNodeWithTag("signInScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInBackground").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInInstruction").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInEmail").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInPassword").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInPasswordVisibility").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInOrText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInGoogleButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInNotRegistered").assertIsDisplayed()
  }

  @Test
  fun emptyEmailShowsCorrectError() {

    composeTestRule.onNodeWithTag("signInPassword").performTextInput(password)
    composeTestRule.onNodeWithTag("signInButton").performClick()
    composeTestRule.onNodeWithTag("signInEmailError").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInEmailError").assertTextEquals("Email cannot be empty")
  }

  @Test
  fun emptyEmailDoesNotCallVM() {

    composeTestRule.onNodeWithTag("signInPassword").performTextInput(password)
    composeTestRule.onNodeWithTag("signInButton").performClick()
    verify(authViewModel, never()).logInWithEmail(any(), any(), any())
  }

  @Test
  fun emptyPasswordShowsCorrectError() {

    composeTestRule.onNodeWithTag("signInEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signInButton").performClick()
    composeTestRule.onNodeWithTag("signInPasswordError").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("signInPasswordError")
        .assertTextEquals("Password cannot be empty")
  }

  @Test
  fun emptyPasswordDoesNotCallVM() {

    composeTestRule.onNodeWithTag("signInEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signInButton").performClick()
    verify(authViewModel, never()).logInWithEmail(any(), any(), any())
  }

  @Test
  fun validSignInAttemptNavigatesToProfileScreen() {

    composeTestRule.onNodeWithTag("signInEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signInPassword").performTextInput(password)
    composeTestRule.onNodeWithTag("signInButton").performClick()
    verify(navigationActions).navigateTo(Screen.PROFILE)
  }

  @Test
  fun validSignInAttemptCallsVMLogInWithEmail() {
    composeTestRule.onNodeWithTag("signInEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signInPassword").performTextInput(password)
    composeTestRule.onNodeWithTag("signInButton").performClick()
    verify(authViewModel).logInWithEmail(any(), eq(email), eq(password))
  }

  @Test
  fun signUpHereNavigatesToSignUpScreen() {
    composeTestRule.onNodeWithTag("signInNotRegistered").performClick()
    verify(navigationActions).navigateTo(Screen.SIGN_UP)
  }
}
