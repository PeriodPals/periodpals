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
import com.android.periodpals.ui.navigation.Route
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

class SignUpScreenTest {

  @get:Rule val composeTestRule = createComposeRule()
  private lateinit var navigationActions: NavigationActions
  private lateinit var authViewModel: AuthViewModel

  companion object {
    private const val email = "test@example.com"
    private const val invalidEmail = "invalidEmail"
    private const val password = "Passw0rd*"
    private const val tooShort = "short"
    private const val noCapital = "password"
    private const val noMinuscule = "PASSWORD"
    private const val noNumber = "Password"
    private const val noSpecial = "Passw0rd"
    private const val doNotMatch1 = "Password1*"
    private const val doNotMatch2 = "Password2*"
  }

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    authViewModel = mock(AuthViewModel::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.ALERT_LIST)
    `when`(authViewModel.userAuthState)
        .thenReturn(mutableStateOf(UserAuthState.Success("User is logged up")))
    composeTestRule.setContent { SignUpScreen(authViewModel, navigationActions) }
  }

  @Test
  fun allComponentsAreDisplayed() {

    composeTestRule.onNodeWithTag("signUpScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpBackground").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpInstruction").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpEmail").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpPassword").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpPasswordVisibility").assertIsDisplayed()

    composeTestRule.onNodeWithTag("signUpConfirmText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpConfirmPassword").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpConfirmVisibility").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpButton").assertIsDisplayed()
  }

  @Test
  fun emptyEmailShowsCorrectError() {

    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(password)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule.onNodeWithTag("signUpEmailError").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpEmailError").assertTextEquals("Email cannot be empty")
  }

  @Test
  fun emptyEmailDoesNotCallVM() {

    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(password)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule.onNodeWithTag("signUpEmailError").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpEmailError").assertTextEquals("Email cannot be empty")
    verify(authViewModel, never()).signUpWithEmail(any(), any(), any())
  }

  @Test
  fun invalidEmailShowsCorrectError() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(invalidEmail)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(password)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule.onNodeWithTag("signUpEmailError").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpEmailError").assertTextEquals("Email must contain @")
  }

  @Test
  fun invalidEmailDoesNotCallVM() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(invalidEmail)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(password)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule.onNodeWithTag("signUpEmailError").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signUpEmailError").assertTextEquals("Email must contain @")
    verify(authViewModel, never()).signUpWithEmail(any(), eq(invalidEmail), eq(password))
  }

  @Test
  fun emptyPasswordShowsCorrectError() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule.onNodeWithTag("signUpPasswordError").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password cannot be empty")
  }

  @Test
  fun emptyPasswordDoesNotCallVM() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule.onNodeWithTag("signUpPasswordError").assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password cannot be empty")
    verify(authViewModel, never()).signUpWithEmail(any(), eq(email), any())
  }

  @Test
  fun tooShortPasswordShowsCorrectError() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(tooShort)
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput(tooShort)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must be at least 8 characters long")
  }

  @Test
  fun tooShortPasswordDoesNotCallVM() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(tooShort)
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput(tooShort)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must be at least 8 characters long")
    verify(authViewModel, never()).signUpWithEmail(any(), eq(email), eq(tooShort))
  }

  @Test
  fun noCapitalPasswordShowsCorrectError() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(noCapital)
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput(noCapital)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must contain at least one capital letter")
  }

  @Test
  fun noCapitalPasswordDoesNotCallVM() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(noCapital)
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput(noCapital)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must contain at least one capital letter")
    verify(authViewModel, never()).signUpWithEmail(any(), eq(email), eq(noCapital))
  }

  @Test
  fun noMinusculePasswordShowsCorrectError() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(noMinuscule)
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput(noMinuscule)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must contain at least one lower case letter")
  }

  @Test
  fun noMinusculePasswordDoesNotCallVM() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(noMinuscule)
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput(noMinuscule)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must contain at least one lower case letter")
    verify(authViewModel, never()).signUpWithEmail(any(), eq(email), eq(noMinuscule))
  }

  @Test
  fun noNumberPasswordShowsCorrectError() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(noNumber)
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput(noNumber)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must contain at least one number")
  }

  @Test
  fun noNumberPasswordDoesNotCallVM() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(noNumber)
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput(noNumber)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must contain at least one number")
    verify(authViewModel, never()).signUpWithEmail(any(), eq(email), eq(noNumber))
  }

  @Test
  fun noSpecialPasswordShowsCorrectError() {
    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(noSpecial)
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput(noSpecial)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must contain at least one special character")
  }

  @Test
  fun noSpecialPasswordDoesNotCallVM() {
    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(noSpecial)
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput(noSpecial)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must contain at least one special character")
    verify(authViewModel, never()).signUpWithEmail(any(), eq(email), eq(noSpecial))
  }

  @Test
  fun doNotMatchPasswordShowsCorrectError() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(doNotMatch1)
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput(doNotMatch2)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule.onNodeWithTag("signUpConfirmError").assertTextEquals("Passwords do not match")
  }

  @Test
  fun doNotMatchPasswordDoesNotCallVM() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(doNotMatch1)
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput(doNotMatch2)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    composeTestRule.onNodeWithTag("signUpConfirmError").assertTextEquals("Passwords do not match")
    verify(authViewModel, never()).signUpWithEmail(any(), eq(email), eq(doNotMatch1))
    verify(authViewModel, never()).signUpWithEmail(any(), eq(email), eq(doNotMatch2))
  }

  @Test
  fun validSignUpAttemptNavigatesToCreateProfileScreen() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(password)
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput(password)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    verify(navigationActions).navigateTo(Screen.CREATE_PROFILE)

    // TODO: Supabase integration for account creation
  }

  @Test
  fun validSignUpAttemptCallsVMLogInWithEmail() {

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput(email)
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput(password)
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput(password)
    composeTestRule.onNodeWithTag("signUpButton").performClick()
    verify(authViewModel).signUpWithEmail(any(), eq(email), eq(password))

    // TODO: Supabase integration for account creation
  }
}
