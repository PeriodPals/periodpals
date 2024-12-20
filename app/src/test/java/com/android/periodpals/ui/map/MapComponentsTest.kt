package com.android.periodpals.ui.map

import androidx.activity.ComponentActivity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.alert.Product
import com.android.periodpals.model.alert.Status
import com.android.periodpals.model.alert.Urgency
import com.android.periodpals.model.chat.ChatViewModel
import com.android.periodpals.model.location.Location
import com.android.periodpals.resources.C.Tag
import com.android.periodpals.ui.components.CONTENT
import com.android.periodpals.ui.components.MapBottomSheet
import com.android.periodpals.ui.components.formatAlertTime
import com.android.periodpals.ui.components.trimLocationText
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MapComponentsTest {

  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  private lateinit var mockNavigationActions: NavigationActions

  private lateinit var mockChatViewModel: ChatViewModel

  private lateinit var mockAlertViewModel: AlertViewModel
  private var mockAlerts =
      listOf(
          Alert(
              id = "1",
              uid = "1",
              name = "Hippo Alpha",
              product = Product.TAMPON,
              urgency = Urgency.HIGH,
              createdAt = "2011-12-03T10:15:30+01:00",
              location = "46.9484,7.4521,Bern",
              message = "I need help!",
              status = Status.CREATED,
          ),
          Alert(
              id = "2",
              uid = "1",
              name = "Hippo Beta",
              product = Product.PAD,
              urgency = Urgency.LOW,
              createdAt = "2011-12-03T10:15:30+01:00",
              location = "46.9484,7.4521,Bern",
              message = "I forgot my pads at home :/",
              status = Status.PENDING,
          ),
      )
  private val mockAlert = mockAlerts.first()

  // Since the SheetState is handled internally by the Jetpack Compose framework,
  // it is incredibly complicated to mock it.
  @OptIn(ExperimentalMaterial3Api::class)
  val sheetState =
      SheetState(
          skipPartiallyExpanded = true,
          density = Density(density = 2.0f, fontScale = 1.0f),
          skipHiddenState = false,
      )

  @Before
  fun setup() {

    mockAlertViewModel = mock(AlertViewModel::class.java)
    whenever(mockAlertViewModel.myAlerts).thenReturn(mutableStateOf(mockAlerts))
    whenever(mockAlertViewModel.palAlerts).thenReturn(mutableStateOf(mockAlerts))
    whenever(mockAlertViewModel.selectedAlert).thenReturn(mutableStateOf(mockAlerts.first()))

    mockNavigationActions = mock(NavigationActions::class.java)
    whenever(mockNavigationActions.currentRoute()).thenReturn(Screen.MAP)

    mockChatViewModel = mock(ChatViewModel::class.java)
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun `bottom sheet appears with correct info`() {

    composeTestRule.setContent {
      MapBottomSheet(
          sheetState = sheetState,
          content = CONTENT.MY_ALERT,
          onSheetDismissRequest = {},
          alertToDisplay = mockAlertViewModel.selectedAlert.value,
          onEditClick = {},
          onAcceptClick = {},
          onResolveClick = {})
    }

    composeTestRule.onNodeWithTag(Tag.MapScreen.BOTTOM_SHEET).assertIsDisplayed()
    composeTestRule.onNodeWithTag(Tag.MapScreen.PROFILE_PICTURE).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(Tag.MapScreen.PROFILE_NAME)
        .assertIsDisplayed()
        .assertTextEquals(mockAlert.name)
    composeTestRule
        .onNodeWithTag(Tag.MapScreen.ALERT_LOCATION_TEXT)
        .assertIsDisplayed()
        .assertTextEquals(trimLocationText(Location.fromString(mockAlert.location).name))
    composeTestRule
        .onNodeWithTag(Tag.MapScreen.ALERT_TIME_TEXT)
        .assertIsDisplayed()
        .assertTextEquals(formatAlertTime(mockAlert.createdAt))
    composeTestRule
        .onNodeWithTag(Tag.MapScreen.ALERT_MESSAGE)
        .assertIsDisplayed()
        .assertTextEquals(mockAlert.message)
    composeTestRule.onNodeWithTag(Tag.MapScreen.ALERT_PRODUCT_ICON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(Tag.MapScreen.ALERT_URGENCY_ICON).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun `bottom sheet appears with correct buttons when clicking on my alert`() {

    composeTestRule.setContent {
      MapBottomSheet(
          sheetState = sheetState,
          content = CONTENT.MY_ALERT,
          onSheetDismissRequest = {},
          alertToDisplay = mockAlertViewModel.selectedAlert.value,
          onEditClick = {},
          onAcceptClick = {},
          onResolveClick = {})
    }

    composeTestRule.onNodeWithTag(Tag.MapScreen.BOTTOM_SHEET).assertIsDisplayed()
    composeTestRule.onNodeWithTag(Tag.MapScreen.EDIT_ALERT_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(Tag.MapScreen.RESOLVE_ALERT_BUTTON).assertIsDisplayed()
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Test
  fun `bottom sheet appears with correct buttons when clicking on a pal alert`() {
    composeTestRule.setContent {
      MapBottomSheet(
          sheetState = sheetState,
          content = CONTENT.PAL_ALERT,
          onSheetDismissRequest = {},
          alertToDisplay = mockAlertViewModel.selectedAlert.value,
          onEditClick = {},
          onAcceptClick = {},
          onResolveClick = {})
    }

    composeTestRule.onNodeWithTag(Tag.MapScreen.BOTTOM_SHEET).assertIsDisplayed()
    composeTestRule.onNodeWithTag(Tag.MapScreen.ACCEPT_ALERT_BUTTON).assertIsDisplayed()
  }
}
