package com.android.periodpals.endtoend

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.MainActivity
import com.android.periodpals.ui.navigation.NavigationActions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This end-to-end test checks the user flow for signing up a new user. See "SignUp" user flow (M1:
 * User Flow 1)
 */
@RunWith(AndroidJUnit4::class)
class SignUpUserFlowTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

  companion object {
    private const val email = "ada_lovelace@epfl.ch"
    private const val psswd = "iLoveSwent1234!"

    // Define user view model
  }

  @Before
  fun setUp() {

  }

  @After
  fun tearDown() {
    // delete user from database
  }

  @Test
  fun SignUpEndToEnd() = {
    // App entry point

  }
}
