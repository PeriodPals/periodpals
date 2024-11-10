import androidx.compose.ui.test.assertHasClickAction
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
import com.android.periodpals.resources.C.Tag.ProfileScreens
import com.android.periodpals.resources.C.Tag.ProfileScreens.EditProfileScreen
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
  fun setUp() {
    navigationActions = mock(NavigationActions::class.java)
    `when`(navigationActions.currentRoute()).thenReturn(Route.PROFILE)
    composeTestRule.setContent { EditProfileScreen(navigationActions) }
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.onNodeWithTag(EditProfileScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(ProfileScreens.PROFILE_PICTURE).assertIsDisplayed()
    composeTestRule.onNodeWithTag(EditProfileScreen.EDIT_PROFILE_PICTURE).assertIsDisplayed()
    composeTestRule.onNodeWithTag(ProfileScreens.MANDATORY_SECTION).assertIsDisplayed()
    composeTestRule.onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD).assertIsDisplayed()
    composeTestRule.onNodeWithTag(ProfileScreens.YOUR_PROFILE_SECTION).assertIsDisplayed()
    composeTestRule.onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(ProfileScreens.SAVE_BUTTON)
        .assertIsDisplayed()
        .assertTextEquals("Save")
        .assertHasClickAction()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals("Edit Your Profile")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(BottomNavigationMenu.BOTTOM_NAVIGATION_MENU).assertDoesNotExist()
  }

  @Test
  fun editValidProfile() {
    composeTestRule.onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD).performTextInput("John Doe")
    composeTestRule.onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD).performTextInput("01/01/1990")
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performTextInput("A short bio")
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performClick()
    verify(navigationActions).navigateTo(Screen.PROFILE)
  }

  @Test
  fun editInvalidProfileNoName() {
    composeTestRule.onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD).performTextInput("01/01/1990")
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performTextInput("A short bio")
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun editInvalidProfileNoDOB() {
    composeTestRule.onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD).performTextInput("John Doe")
    composeTestRule
        .onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD)
        .performTextInput("A short bio")
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun editInvalidProfileNoDescription() {
    composeTestRule.onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD).performTextInput("John Doe")
    composeTestRule.onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD).performTextInput("01/01/1990")
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<String>())
  }

  @Test
  fun editInvalidProfileAllEmptyFields() {
    composeTestRule.onNodeWithTag(ProfileScreens.NAME_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.DOB_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.DESCRIPTION_INPUT_FIELD).performTextClearance()
    composeTestRule.onNodeWithTag(ProfileScreens.SAVE_BUTTON).performClick()
    verify(navigationActions, never()).navigateTo(any<String>())
  }
}
