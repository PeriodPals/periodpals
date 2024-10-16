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

class SignUpScreenTest {
    private lateinit var navigationActions: NavigationActions

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    // Mock the current route to the Alert List screen
    `when`(navigationActions.currentRoute()).thenReturn(Screen.ALERT_LIST)
  }

  @Test
  fun signUpScreen_displaysCorrectUI() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    // Assert visibility of UI elements
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
  fun signUpScreen_emailValidation_emptyEmail_showsError() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    // Attempt to sign up with an empty email
    composeTestRule.onNodeWithTag("signUpButton").performClick()

    // Assert the error message is displayed
    composeTestRule.onNodeWithTag("signUpEmailError").assertTextEquals("Email cannot be empty")
  }

  @Test
  fun signUpScreen_emailValidation_invalidEmail_showsError() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    // Input an invalid email
    composeTestRule.onNodeWithTag("signUpEmail").performTextInput("invalidEmail")
    composeTestRule.onNodeWithTag("signUpButton").performClick()

    // Assert the error message is displayed
    composeTestRule.onNodeWithTag("signUpEmailError").assertTextEquals("Email must contain @")
  }

  @Test
  fun signUpScreen_passwordValidation_emptyPassword_showsError() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    // Input an email and attempt to sign up with an empty password
    composeTestRule.onNodeWithTag("signUpEmail").performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("signUpButton").performClick()

    // Assert the error message is displayed
    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password cannot be empty")
  }

  @Test
  fun signUpScreen_passwordValidation_passwordTooShort_showsError() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput("short")
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput("short")
    composeTestRule.onNodeWithTag("signUpButton").performClick()

    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must be at least 8 characters long")
  }

  @Test
  fun signUpScreen_passwordValidation_passwordNoCapital_showsError() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput("password")
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput("password")
    composeTestRule.onNodeWithTag("signUpButton").performClick()

    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must contain at least one capital letter")
  }

  @Test
  fun signUpScreen_passwordValidation_passwordNoMinuscule_showsError() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput("PASSWORD")
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput("PASSWORD")
    composeTestRule.onNodeWithTag("signUpButton").performClick()

    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must contain at least one lower case letter")
  }

  @Test
  fun signUpScreen_passwordValidation_passwordNoNumber_showsError() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput("Password")
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput("Password")
    composeTestRule.onNodeWithTag("signUpButton").performClick()

    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must contain at least one number")
  }

  @Test
  fun signUpScreen_passwordValidation_passwordNoSpecial_showsError() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    composeTestRule.onNodeWithTag("signUpEmail").performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput("Passw0rd")
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput("Passw0rd")
    composeTestRule.onNodeWithTag("signUpButton").performClick()

    composeTestRule
        .onNodeWithTag("signUpPasswordError")
        .assertTextEquals("Password must contain at least one special character")
  }

  @Test
  fun signUpScreen_passwordValidation_passwordsDoNotMatch_showsError() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    // Input an email and mismatched passwords
    composeTestRule.onNodeWithTag("signUpEmail").performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput("Password123")
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput("Password456")
    composeTestRule.onNodeWithTag("signUpButton").performClick()

    // Assert the error message is displayed
    composeTestRule.onNodeWithTag("signUpConfirmError").assertTextEquals("Passwords do not match")
  }

  @Test
  fun signUpScreen_signUp_successfulRegistration() {
    composeTestRule.setContent { RegisterScreen(navigationActions) }

    // Input valid data and perform sign up
    composeTestRule.onNodeWithTag("signUpEmail").performTextInput("test@example.com")
    composeTestRule.onNodeWithTag("signUpPassword").performTextInput("ValidPassword123!")
    composeTestRule.onNodeWithTag("signUpConfirmPassword").performTextInput("ValidPassword123!")
    composeTestRule.onNodeWithTag("signUpButton").performClick()

    // You can assert here for a visual change or a Toast message if possible
    // Since Toast can't be tested directly, consider an alternative for future testing
    // TODO: Supabase integration for account creation
  }
}
