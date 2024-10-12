package com.android.periodpals.ui.authentication

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.MainActivity
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInTest : TestCase() {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Before
  fun setUp() {
    Intents.init()
  }

  @After
  fun tearDown() {
    Intents.release()
  }

  @Test
  fun checkComponentsAreDisplayed() {
    // TODO: Check when logo is imported and designed
    //    composeTestRule.onNodeWithTag("loginAppLogo").assertIsDisplayed()

    composeTestRule.onNodeWithTag("signInScreen").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInBackground").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInTitle").assertTextEquals("Welcome to PeriodPals")
    composeTestRule.onNodeWithTag("signInInstruction").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInInstruction").assertTextEquals("Sign in to your account")
    composeTestRule.onNodeWithTag("signInEmail").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInPassword").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInPasswordVisibility").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInPasswordVisibility").assertHasClickAction()
    composeTestRule.onNodeWithTag("signInButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("signInOrText").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInOrText").assertTextEquals("Or continue with")
    composeTestRule.onNodeWithTag("signInGoogleButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInGoogleButton").assertHasClickAction()
    composeTestRule.onNodeWithTag("signInNotRegistered").assertIsDisplayed()
    composeTestRule.onNodeWithTag("signInNotRegistered").assertIsDisplayed().assertHasClickAction()
  }

  // TODO: Implement the test for Supabase login
  @Test
  fun validEmailAndPassword() {
    composeTestRule.activity.setContent { SignInScreen() }

    composeTestRule.onNodeWithTag("signInEmail").performTextInput("valid@example.com")
    composeTestRule.onNodeWithTag("signInPassword").performTextInput("validPassword")
    composeTestRule.onNodeWithTag("signInButton").performClick()

    // Check if the toast message "Login Successful" is displayed
  }

  // TODO: Implement the test for Supabase login
  //  @Test
  //  fun invalidEmail() {
  //    composeTestRule.activity.setContent { SignInScreen() }
  //
  //    composeTestRule.onNodeWithTag("signInEmail").performTextInput("invalid-email")
  //    composeTestRule.onNodeWithTag("signInPassword").performTextInput("validPassword")
  //    composeTestRule.onNodeWithTag("signInButton").performClick()
  //
  //    // Add assertions to verify error message for invalid email
  //  }

  // TODO: Implement with supabase
  //  @Test
  //  fun invalidPassword() {
  //    composeTestRule.activity.setContent { SignInScreen() }
  //
  //    composeTestRule.onNodeWithTag("loginUsername").performTextInput("valid@example.com")
  //    composeTestRule.onNodeWithTag("loginPassword").performTextInput("invalid")
  //    composeTestRule.onNodeWithTag("loginButton").performClick()
  //
  //    // Add assertions to verify error message for invalid email
  //  }

  @Test
  fun googleSignInReturnsValidActivityResult() {
    composeTestRule.activity.setContent { SignInScreen() }

    composeTestRule.onNodeWithTag("signInGoogleButton").performClick()
    composeTestRule.waitForIdle()
    intended(toPackage("com.google.android.gms"))
  }

  // TODO: Implement the test for Supabase login

  //  @Test
  //  fun supabaseLogInRetursValidActivityResult() {
  //    // Set up the test environment
  //    composeTestRule.setContent { SignInScreen() }
  //
  //    // Mock Supabase authentication
  //    val mockSupabaseClient = mockk<SupabaseClient>()
  //    every { mockSupabaseClient.auth.signInWithPassword(any(), any()) } returns AuthResponse(
  //      user = User(id = "testUserId"),
  //      session = Session(accessToken = "testAccessToken")
  //    )
  //
  //    // Perform the login action
  //    composeTestRule.onNodeWithTag("loginUsername").performTextInput("testUser")
  //    composeTestRule.onNodeWithTag("loginPassword").performTextInput("testPassword")
  //    composeTestRule.onNodeWithTag("loginButton").performClick()
  //
  //    // Verify the result
  //    composeTestRule.waitForIdle()
  //    verify { mockSupabaseClient.auth.signInWithPassword("testUser", "testPassword") }
  //    assertTrue(mockSupabaseClient.auth.currentSession != null)
  //  }

  // TODO: tests for toast messages ?
}
