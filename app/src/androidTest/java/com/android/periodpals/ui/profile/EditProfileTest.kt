import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.profile.EditProfileScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditProfileTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  // Set up the EditProfileScreen
  fun setUp() {
    composeTestRule.setContent {
      MaterialTheme { EditProfileScreen(NavigationActions(rememberNavController())) }
    }
  }

  @Test
  fun testEmailRowDisplayed() {

    // Check if the email row is displayed
    composeTestRule.onNodeWithTag("email_row").assertIsDisplayed()
  }

  @Test
  fun testProfileImageDisplayed() {

    // Check if the profile image is displayed
    composeTestRule.onNodeWithTag("profile_image").assertIsDisplayed()

    // Check if the add circle icon is displayed
    composeTestRule.onNodeWithTag("add_circle_icon").assertIsDisplayed()
  }

  @Test
  fun testPerformTextInput() {

    // Perform text input
    composeTestRule.onNodeWithTag("name_field").performTextInput("New Name")
    composeTestRule.onNodeWithTag("dob_field").performTextInput("02/02/2022")
    composeTestRule.onNodeWithTag("description_field").performTextInput("New Description")

    // Verify text input
    composeTestRule.onNodeWithTag("name_field").assertTextEquals("New Name")
    composeTestRule.onNodeWithTag("dob_field").assertTextEquals("02/02/2022")
    composeTestRule.onNodeWithTag("description_field").assertTextEquals("New Description")
  }

  @Test
  fun testPerformSaveButtonClick() {
    // Perform save button click
    composeTestRule.onNodeWithTag("save_button").performClick()
  }
}
