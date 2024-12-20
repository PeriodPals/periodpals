package com.android.periodpals.ui.map

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import com.android.periodpals.R
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.alert.LIST_OF_PRODUCTS
import com.android.periodpals.model.alert.LIST_OF_URGENCIES
import com.android.periodpals.model.alert.Product
import com.android.periodpals.model.alert.Status
import com.android.periodpals.model.alert.Urgency
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.chat.ChatViewModel
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.model.user.AuthenticationUserData
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C.Tag.AlertInputs
import com.android.periodpals.resources.C.Tag.AlertListsScreen
import com.android.periodpals.resources.C.Tag.MapScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.services.NetworkChangeListener
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import io.github.kakaocup.kakao.common.utilities.getResourceString
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

private const val MOCK_ACCURACY = 15.0f

private const val LOCATION = "Bern"
private val PRODUCT = LIST_OF_PRODUCTS[0].textId // Tampon
private val URGENCY = LIST_OF_URGENCIES[0].textId // High

@RunWith(RobolectricTestRunner::class)
class MapScreenTest {
  @get:Rule val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  private lateinit var mockNavigationActions: NavigationActions

  private lateinit var mockGpsService: GPSServiceImpl
  private var mockLocationFlow = MutableStateFlow(Location.DEFAULT_LOCATION)
  private var mockAccuracyFlow = MutableStateFlow(MOCK_ACCURACY)

  private lateinit var mockNetworkChangeListener: NetworkChangeListener

  private lateinit var mockAuthenticationViewModel: AuthenticationViewModel
  private var mockUserData =
      mutableStateOf(AuthenticationUserData(uid = "451", email = "ray@bradbury.com"))

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
          ))

  private val onSuccessCaptor = argumentCaptor<() -> Unit>()

  private lateinit var mockLocationViewModel: LocationViewModel

  private lateinit var mockChatViewModel: ChatViewModel

  private lateinit var mockUserViewModel: UserViewModel

  private val name = "John Doe"
  private val imageUrl = "https://example.com"
  private val description = "A short description"
  private val dob = "01/01/2000"
  private val preferredDistance = 500

  private val userState =
      mutableStateOf(
          User(
              name = name,
              imageUrl = imageUrl,
              description = description,
              dob = dob,
              preferredDistance = preferredDistance,
          ))

  @Before
  fun setup() {

    mockGpsService = mock(GPSServiceImpl::class.java)
    whenever(mockGpsService.location).thenReturn(mockLocationFlow)
    whenever(mockGpsService.accuracy).thenReturn(mockAccuracyFlow)

    mockNavigationActions = mock(NavigationActions::class.java)
    whenever(mockNavigationActions.currentRoute()).thenReturn(Screen.MAP)

    mockAuthenticationViewModel = mock(AuthenticationViewModel::class.java)
    whenever(mockAuthenticationViewModel.authUserData).thenReturn(mockUserData)

    mockAlertViewModel = mock(AlertViewModel::class.java)
    whenever(mockAlertViewModel.myAlerts).thenReturn(mutableStateOf(mockAlerts))
    whenever(mockAlertViewModel.palAlerts).thenReturn(mutableStateOf(mockAlerts))

    mockLocationViewModel = mock(LocationViewModel::class.java)
    whenever(mockLocationViewModel.locationSuggestions)
        .thenReturn(MutableStateFlow(listOf(Location.DEFAULT_LOCATION)))
    whenever(mockLocationViewModel.query)
        .thenReturn(MutableStateFlow(Location.DEFAULT_LOCATION.name))

    mockChatViewModel = mock(ChatViewModel::class.java)

    mockUserViewModel = mock(UserViewModel::class.java)
    `when`(mockUserViewModel.user).thenReturn(userState)

    mockNetworkChangeListener = mock(NetworkChangeListener::class.java)
    whenever(mockNetworkChangeListener.isNetworkAvailable).thenReturn(MutableStateFlow(true))

    composeTestRule.setContent {
      MapScreen(
          gpsService = mockGpsService,
          authenticationViewModel = mockAuthenticationViewModel,
          alertViewModel = mockAlertViewModel,
          locationViewModel = mockLocationViewModel,
          chatViewModel = mockChatViewModel,
          userViewModel = mockUserViewModel,
          networkChangeListener = mockNetworkChangeListener,
          navigationActions = mockNavigationActions)
    }
  }

  @Test
  fun `all components are correctly displayed`() {

    // TopAppBar
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.map_screen_title))
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()

    // MapScreen
    composeTestRule.onNodeWithTag(MapScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(MapScreen.MAP_VIEW_CONTAINER).assertIsDisplayed()
    composeTestRule.onNodeWithTag(MapScreen.MY_LOCATION_BUTTON).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_FAB).assertIsDisplayed()
    composeTestRule.onNodeWithTag(MapScreen.BOTTOM_SHEET).assertIsNotDisplayed()
  }

  @Test
  fun `map asks for permissions and starts updates`() {
    composeTestRule.onNodeWithTag(MapScreen.SCREEN).assertIsDisplayed()

    verify(mockGpsService).askPermissionAndStartUpdates()
  }

  @Test
  fun `map fetches alerts`() {
    composeTestRule.onNodeWithTag(MapScreen.SCREEN).assertIsDisplayed()

    verify(mockAuthenticationViewModel).loadAuthenticationUserData(any(), any())
    verify(mockAlertViewModel).setUserID(mockUserData.value.uid)
    verify(mockAlertViewModel).fetchAlerts(any(), any())
  }

  /*
  Since we cannot directly check that the markers are being rendered in the map, at least verify
  that the alert list is being accessed by the map.
  */
  @Test
  fun `map accesses alerts list`() {
    composeTestRule.onNodeWithTag(MapScreen.SCREEN).assertIsDisplayed()

    verify(mockAlertViewModel).setUserID(mockUserData.value.uid)
    verify(mockAlertViewModel).fetchAlerts(onSuccessCaptor.capture(), any())

    onSuccessCaptor.allValues.last().invoke()
    verify(mockAlertViewModel).myAlerts
    verify(mockAlertViewModel).palAlerts
  }

  /*
  Since we cannot directly check that the markers are being rendered in the map, at least verify
  that the location and accuracy are being accessed by the map.
  */
  @Test
  fun `map accesses user location and accuracy`() {
    composeTestRule.onNodeWithTag(MapScreen.SCREEN).assertIsDisplayed()

    composeTestRule.waitForIdle()
    verify(mockGpsService).location
    verify(mockGpsService).accuracy
  }

  @Test
  fun `map accesses user location when clicking on recenter button`() {
    composeTestRule.onNodeWithTag(MapScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(MapScreen.MY_LOCATION_BUTTON).assertIsDisplayed()

    composeTestRule.onNodeWithTag(MapScreen.MY_LOCATION_BUTTON).performClick()
    composeTestRule.waitForIdle()
    verify(mockGpsService).location
  }

  @Test
  fun `clicking on the filter fab shows the filter dialog`() {
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_FAB).assertIsDisplayed().performClick()

    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_DIALOG).assertIsDisplayed()
  }

  @Test
  fun `saving the filter calls alertsWithinRadius`() {
    composeTestRule.onNodeWithTag(MapScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_FAB).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag(AlertInputs.LOCATION_FIELD).performTextInput(LOCATION)
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + Location.DEFAULT_LOCATION.name)
        .performClick()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_RADIUS_SLIDER).performSemanticsAction(
        SemanticsActions.SetProgress) {
          it(200.0f)
        }
    composeTestRule.onNodeWithTag(AlertInputs.PRODUCT_FIELD).performClick()
    composeTestRule.onNodeWithText(PRODUCT).performScrollTo().performClick()
    composeTestRule.onNodeWithTag(AlertInputs.URGENCY_FIELD).performClick()
    composeTestRule.onNodeWithText(URGENCY).performScrollTo().performClick()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_APPLY_BUTTON).performClick()

    verify(mockAlertViewModel)
        .fetchAlertsWithinRadius(
            eq(Location.DEFAULT_LOCATION), eq(200.0), any<() -> Unit>(), any<(Exception) -> Unit>())
    verify(mockAlertViewModel).setFilter(any<(Alert) -> Boolean>())

    composeTestRule.onNodeWithTag(MapScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_FAB_BUBBLE).assertIsDisplayed()
  }

  @Test
  fun `clicking on reset removes filters`() {
    composeTestRule.onNodeWithTag(MapScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_FAB).assertIsDisplayed().performClick()
    composeTestRule.onNodeWithTag(AlertInputs.LOCATION_FIELD).performTextInput(LOCATION)
    composeTestRule
        .onNodeWithTag(AlertInputs.DROPDOWN_ITEM + Location.DEFAULT_LOCATION.name)
        .performClick()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_FAB).performClick()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_RESET_BUTTON).performClick()
    verify(mockAlertViewModel).removeFilters()

    composeTestRule.onNodeWithTag(MapScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(AlertListsScreen.FILTER_FAB_BUBBLE).assertIsNotDisplayed()
  }
}
