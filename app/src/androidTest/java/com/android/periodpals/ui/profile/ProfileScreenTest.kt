package com.android.periodpals.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.android.periodpals.model.user.UserRepositorySupabase
import com.android.periodpals.model.user.UserViewModel
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun displayAllComponents() {
    val db = UserViewModel(UserRepositorySupabase())

    composeTestRule.setContent { ProfileScreen(db) }
    composeTestRule.onNodeWithTag("profileAvatar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Description").assertIsDisplayed()
    composeTestRule.onNodeWithTag("reviewOne").assertIsDisplayed()
    composeTestRule.onNodeWithTag("reviewTwo").assertIsDisplayed()
  }

  @Test
  fun profileScreen_hasCorrectContent() {
    val db = UserViewModel(UserRepositorySupabase())

    composeTestRule.setContent { ProfileScreen(db) }
    composeTestRule.onNodeWithTag("profileName").assertTextEquals("Name")
    composeTestRule.onNodeWithTag("Description").assertTextEquals("Description")
  }
}
