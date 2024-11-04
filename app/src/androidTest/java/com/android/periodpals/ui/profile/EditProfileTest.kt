import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.resources.C.Tag.BottomNavigationMenu
import com.android.periodpals.resources.C.Tag.EditProfileScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.profile.EditProfileScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.never

@RunWith(AndroidJUnit4::class)
class EditProfileTest {

  private lateinit var navigationActions: NavigationActions
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  // Set up the EditProfileScreen
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)

    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)

    composeTestRule.setContent { EditProfileScreen(navigationActions) }
  }

  @Test
  fun displayAllComponents() {

    composeTestRule.onNodeWithTag(EditProfileScreen.NAME_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(EditProfileScreen.DOB_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(EditProfileScreen.DESCRIPTION_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(EditProfileScreen.EMAIL_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(EditProfileScreen.PROFILE_PICTURE).assertIsDisplayed()
    composeTestRule.onNodeWithTag(EditProfileScreen.ADD_CIRCLE_ICON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(EditProfileScreen.EDIT_PROFILE_SCREEN).assertIsDisplayed()

    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertDoesNotExist()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()

    composeTestRule
        .onNodeWithTag(EditProfileScreen.SAVE_BUTTON)
        .assertIsDisplayed()
        .assertTextEquals("Save Changes")
  }

  @Test
  fun createValidProfile() {
    composeTestRule.onNodeWithTag(EditProfileScreen.NAME_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.DOB_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.DESCRIPTION_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.EMAIL_FIELD).performTextClearance()

    composeTestRule.onNodeWithTag(EditProfileScreen.NAME_FIELD).performTextInput("John Doe")
    composeTestRule.onNodeWithTag(EditProfileScreen.DOB_FIELD).performTextInput("01/01/1990")
    composeTestRule
        .onNodeWithTag(EditProfileScreen.DESCRIPTION_FIELD)
        .performTextInput("A short bio")
    composeTestRule
        .onNodeWithTag(EditProfileScreen.EMAIL_FIELD)
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag(EditProfileScreen.SAVE_BUTTON).performClick()
    verify(navigationActions).navigateTo(Screen.PROFILE)
  }

  @Test
  fun createInvalidProfileNoName() {
    composeTestRule.onNodeWithTag(EditProfileScreen.NAME_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.DOB_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.DESCRIPTION_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.EMAIL_FIELD).performTextClearance()

    composeTestRule.onNodeWithTag(EditProfileScreen.DOB_FIELD).performTextInput("01/01/1990")
    composeTestRule
        .onNodeWithTag(EditProfileScreen.DESCRIPTION_FIELD)
        .performTextInput("A short bio")
    composeTestRule
        .onNodeWithTag(EditProfileScreen.EMAIL_FIELD)
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag(EditProfileScreen.SAVE_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidProfileNoDOB() {
    composeTestRule.onNodeWithTag(EditProfileScreen.NAME_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.DOB_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.DESCRIPTION_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.EMAIL_FIELD).performTextClearance()

    composeTestRule.onNodeWithTag(EditProfileScreen.NAME_FIELD).performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag(EditProfileScreen.DESCRIPTION_FIELD)
        .performTextInput("A short bio")
    composeTestRule
        .onNodeWithTag(EditProfileScreen.EMAIL_FIELD)
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag(EditProfileScreen.SAVE_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidProfileNoDescription() {
    composeTestRule.onNodeWithTag(EditProfileScreen.NAME_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.DOB_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.DESCRIPTION_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.EMAIL_FIELD).performTextClearance()

    composeTestRule.onNodeWithTag(EditProfileScreen.NAME_FIELD).performTextInput("John Doe")
    composeTestRule.onNodeWithTag(EditProfileScreen.DOB_FIELD).performTextInput("01/01/1990")
    composeTestRule
        .onNodeWithTag(EditProfileScreen.EMAIL_FIELD)
        .performTextInput("john.doe@example.com")
    composeTestRule.onNodeWithTag(EditProfileScreen.SAVE_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidProfileNoEmail() {
    composeTestRule.onNodeWithTag(EditProfileScreen.NAME_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.DOB_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.DESCRIPTION_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.EMAIL_FIELD).performTextClearance()

    composeTestRule.onNodeWithTag(EditProfileScreen.NAME_FIELD).performTextInput("John Doe")
    composeTestRule.onNodeWithTag(EditProfileScreen.DOB_FIELD).performTextInput("01/01/1990")
    composeTestRule
        .onNodeWithTag(EditProfileScreen.DESCRIPTION_FIELD)
        .performTextInput("A short bio")
    composeTestRule.onNodeWithTag(EditProfileScreen.SAVE_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun createInvalidProfileAllEmptyFields() {
    composeTestRule.onNodeWithTag(EditProfileScreen.NAME_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.DOB_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.DESCRIPTION_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(EditProfileScreen.EMAIL_FIELD).performTextClearance()

    composeTestRule.onNodeWithTag(EditProfileScreen.SAVE_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<String>())
  }
}
