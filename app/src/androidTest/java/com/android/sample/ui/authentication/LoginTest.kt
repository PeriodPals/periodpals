package com.android.sample.ui.authentication

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.toPackage
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.sample.MainActivity
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest : TestCase() {
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
  fun titleAndButtonAreCorrectlyDisplayed() {
    // TODO: Check when logo is imported and designed
    //    composeTestRule.onNodeWithTag("loginAppLogo").assertIsDisplayed()

    composeTestRule.onNodeWithTag("loginTitle").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginTitle").assertTextEquals("Welcome to PeriodPals")

    composeTestRule.onNodeWithTag("loginInstruction").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginInstruction").assertTextEquals("Sign in to your account")

    composeTestRule.onNodeWithTag("loginGoogleButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginGoogleButton").assertHasClickAction()

    composeTestRule.onNodeWithTag("loginOr").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginOr").assertTextEquals("or continue with")

    composeTestRule.onNodeWithTag("loginUsername").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginPassword").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginPasswordVisibility").assertIsDisplayed()

    composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed()
    composeTestRule.onNodeWithTag("loginButton").assertHasClickAction()
  }

  @Test
  fun googleSignInReturnsValidActivityResult() {
    composeTestRule.setContent { SignInScreen() }

    composeTestRule.onNodeWithTag("loginGoogleButton").performClick()
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
