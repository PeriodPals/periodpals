package com.android.periodpals.ui.authentication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class SignInScreenTest {
  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    // Mock the current route to the Alert List screen
    `when`(navigationActions.currentRoute()).thenReturn(Screen.ALERT_LIST)
  }

  @Test
  fun signInScreen_displaysCorrectUI() {
    // Set the content to the SignInScreen
    composeTestRule.setContent { SignInScreen(navigationActions) }

    // Check if the welcome text is displayed
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
  fun signInScreen_emailValidation_emptyEmail_showsError() {
    composeTestRule.setContent { SignInScreen(navigationActions) }

    // Click on the sign in button with empty fields
    composeTestRule.onNodeWithTag("signInButton").performClick()

    // Verify that the error message for email is displayed
    composeTestRule.onNodeWithTag("signInEmailError").assertTextEquals("Email cannot be empty")
  }

  @Test
  fun signInScreen_emailValidation_invalidEmail_showsError() {
    composeTestRule.setContent { SignInScreen(navigationActions) }

    // Enter an invalid email
    composeTestRule.onNodeWithTag("signInEmail").performTextInput("invalidEmail")

    // Click on the sign in button
    composeTestRule.onNodeWithTag("signInButton").performClick()

    // Verify that the error message for email is displayed
    composeTestRule.onNodeWithTag("signInEmailError").assertTextEquals("Email must contain @")
  }

  @Test
  fun signInScreen_passwordValidation_emptyPassword_showsError() {
    composeTestRule.setContent { SignInScreen(navigationActions) }

    // Enter a valid email
    composeTestRule.onNodeWithTag("signInEmail").performTextInput("test@example.com")

    // Click on the sign in button with empty password
    composeTestRule.onNodeWithTag("signInButton").performClick()

    // Verify that the error message for password is displayed
    composeTestRule
        .onNodeWithTag("signInPasswordError")
        .assertTextEquals("Password cannot be empty")
  }

  @Test
  fun signInScreen_signIn_successfulLogin() {
    composeTestRule.setContent { SignInScreen(navigationActions) }

    // Enter valid email and password
    composeTestRule.onNodeWithTag("signInEmail").performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("signInPassword").performTextInput("ValidPassword123")

    // Click on the sign in button
    composeTestRule.onNodeWithTag("signInButton").performClick()

    // Check for a successful login Toast (mocking would be required here)
    // Currently, you can't test Toast directly; you can use dependency injection or other methods
  }

  @Test
  fun signInScreen_signIn_failsInvalidLogin() {
    composeTestRule.setContent { SignInScreen(navigationActions) }

    // Enter valid email and an invalid password
    composeTestRule.onNodeWithTag("signInEmail").performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("signInPassword").performTextInput("InvalidPassword")

    // Click on the sign in button
    composeTestRule.onNodeWithTag("signInButton").performClick()

    // Check for a failed login Toast (mocking would be required here)
    // You can set up your test to verify that the error message or Toast appears.
  }

  @Test
  fun signInScreen_navigatesToSignUp() {
    composeTestRule.setContent { SignInScreen(navigationActions) }

    // Click on the "Not registered yet? Sign up here!" text
    composeTestRule.onNodeWithTag("signInNotRegistered").performClick()

    // Check for a navigation action (mocking would be required here)
    // You would verify that the navigation to the sign-up screen is triggered.
  }
}
