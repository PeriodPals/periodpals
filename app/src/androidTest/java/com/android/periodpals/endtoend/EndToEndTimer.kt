package com.android.periodpals.endtoend

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.android.periodpals.MainActivity
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignInScreen
import com.android.periodpals.resources.C.Tag.AuthenticationScreens.SignUpScreen
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TAG = "EndToEndTimer"
private const val TIMEOUT = 60000L // 60 seconds, adjust for slower devices, networks and CI

@RunWith(AndroidJUnit4::class)
class EndToEndTimer : TestCase() {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val activityRule = ActivityTestRule(MainActivity::class.java)
  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)

  companion object SignUpData {
    private val signUpName = ('a'..'z').map { it }.shuffled().subList(0, 8).joinToString("")
    private val signUpEmail = "$signUpName@example.com"
    private const val PASSWORD = "iLoveSwent1234!"
    private const val DOB = "31/01/2000"
    private val signUpDescription = "Short bio containing my name to identify me: $signUpName"
  }

  @Before
  fun setUp() {
    composeTestRule.setContent { MainActivity() }
  }

  @Test
  fun timerEndToEnd() {
    // Navigate to the sign-up screen
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag(SignInScreen.SCREEN).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(SignInScreen.NOT_REGISTERED_NAV_LINK)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()
    composeTestRule.waitUntil(TIMEOUT) {
      composeTestRule.onAllNodesWithTag(SignUpScreen.SCREEN).fetchSemanticsNodes().size == 1
    }
  }
}
