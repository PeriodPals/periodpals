package com.android.periodpals.ui.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.android.periodpals.model.SupabaseModule
import com.android.periodpals.model.user.UserRepositorySupabase
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.ui.navigation.NavigationActions
import org.junit.Rule
import org.junit.Test

class ProfileScreenTest {

  @get:Rule val composeTestRule = createComposeRule()

  private var db = UserViewModel(UserRepositorySupabase(SupabaseModule.getClient()))

  @Test
  fun displayAllComponents() {

    composeTestRule.setContent { ProfileScreen(db, NavigationActions(rememberNavController())) }
    composeTestRule.onNodeWithTag("profileAvatar").assertIsDisplayed()
    composeTestRule.onNodeWithTag("profileName").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Description").assertIsDisplayed()
    composeTestRule.onNodeWithTag("reviewOne").assertIsDisplayed()
    composeTestRule.onNodeWithTag("reviewTwo").assertIsDisplayed()
  }

  /**
   * ProfileScreen content is set by the user's profile data
   *
   * @Test fun profileScreen_hasCorrectContent() { composeTestRule.setContent { ProfileScreen(db,
   *   NavigationActions(rememberNavController())) }
   *   composeTestRule.onNodeWithTag("profileName").assertTextEquals("Name")
   *   composeTestRule.onNodeWithTag("Description").assertTextEquals("Description") }
   */
}
