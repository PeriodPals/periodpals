package com.android.periodpals.ui.map

import android.content.Context
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.android.periodpals.BuildConfig
import com.android.periodpals.ChannelActivity
import com.android.periodpals.R
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.alert.Product
import com.android.periodpals.model.alert.Urgency
import com.android.periodpals.model.alert.productToPeriodPalsIcon
import com.android.periodpals.model.alert.stringToProduct
import com.android.periodpals.model.alert.stringToUrgency
import com.android.periodpals.model.alert.urgencyToPeriodPalsIcon
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.chat.ChatViewModel
import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.C
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.services.NetworkChangeListener
import com.android.periodpals.ui.components.CONTENT
import com.android.periodpals.ui.components.FILTERS_NO_PREFERENCE_TEXT
import com.android.periodpals.ui.components.FilterDialog
import com.android.periodpals.ui.components.FilterFab
import com.android.periodpals.ui.components.MapBottomSheet
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

private const val TAG = "MapScreen"

private const val MIN_ZOOM_LEVEL = 5.0
private const val MAX_ZOOM_LEVEL = 19.0
private const val INITIAL_ZOOM_LEVEL = 17.0
private const val DEFAULT_RADIUS = 100.0

/**
 * Screen that displays the top app bar, bottom navigation bar, the map and two FABs.
 *
 * The map contains:
 * - the location of the user, along a translucent confidence circle representing the accuracy of
 *   the location
 * - markers for the locations where alerts where posted
 * - a recenter on the current location button.
 *
 * @param gpsService Provides the location of the device and the functions to interact with it
 * @param authenticationViewModel Manages the authentication data
 * @param alertViewModel Manages the alert data
 * @param locationViewModel Manages the location data
 * @param navigationActions Provides the functions to navigate in the app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    gpsService: GPSServiceImpl,
    authenticationViewModel: AuthenticationViewModel,
    alertViewModel: AlertViewModel,
    locationViewModel: LocationViewModel,
    chatViewModel: ChatViewModel,
    userViewModel: UserViewModel,
    networkChangeListener: NetworkChangeListener,
    navigationActions: NavigationActions,
) {

  // To manage the map state
  val context = LocalContext.current
  val mapView = remember { MapView(context) }
  val myLocation by gpsService.location.collectAsState()
  val myAccuracy by gpsService.accuracy.collectAsState()
  val isDarkTheme = isSystemInDarkTheme()
  val myLocationOverlay = remember { FolderOverlay() }
  val alertOverlay = remember { FolderOverlay() }

  // To manage the bottom sheet state
  val sheetState = rememberModalBottomSheetState()
  var showBottomSheet by remember { mutableStateOf(false) }
  var content by remember { mutableStateOf(CONTENT.MY_ALERT) }

  // To manage the alert filter state
  var isFilterApplied by remember { mutableStateOf(false) }
  var showFilterDialog by remember { mutableStateOf(false) }
  var selectedLocation by remember { mutableStateOf<Location?>(null) }
  var radiusInMeters by remember { mutableDoubleStateOf(DEFAULT_RADIUS) }
  var productFilter by remember { mutableStateOf<Product?>(Product.NO_PREFERENCE) }
  var urgencyFilter by remember { mutableStateOf<Urgency?>(null) }

  // Fetch alerts
  authenticationViewModel.loadAuthenticationUserData(
      onFailure = { Log.d(TAG, "Authentication data is null") })
  val uid by remember { mutableStateOf(authenticationViewModel.authUserData.value!!.uid) }
  alertViewModel.setUserID(uid)

  LaunchedEffect(Unit) {
    alertViewModel.fetchAlerts(
        onSuccess = { Log.d(TAG, "Successfully fetched alerts") },
        onFailure = { e -> Log.d(TAG, "Error fetching alerts: $e") },
    )
  }
  val myAlerts = alertViewModel.myAlerts.value
  val palAlerts = alertViewModel.palAlerts.value

  LaunchedEffect(myAlerts, palAlerts) {
    updateAlertMarkers(
        mapView = mapView,
        alertOverlay = alertOverlay,
        context = context,
        myAlertsList = myAlerts,
        palAlertsList = palAlerts,
        onMyAlertClick = { alert ->
          showBottomSheet = true
          content = CONTENT.MY_ALERT
          alertViewModel.selectAlert(alert)
        },
        onPalAlertClick = { alert ->
          showBottomSheet = true
          content = CONTENT.PAL_ALERT
          alertViewModel.selectAlert(alert)
        },
    )
  }

  LaunchedEffect(myLocation) {
    updateMyLocationMarker(
        mapView = mapView,
        overlay = myLocationOverlay,
        context = context,
        myLocation = myLocation,
        myAccuracy = myAccuracy,
        onLocationClickCallback = { showBottomSheet = true },
    )
  }

  LaunchedEffect(Unit) {
    gpsService.askPermissionAndStartUpdates()

    initializeMap(
        mapView = mapView,
        myLocationOverlay = myLocationOverlay,
        alertsOverlay = alertOverlay,
        location = myLocation,
        isDarkTheme = isDarkTheme,
        context = context,
    )
  }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(C.Tag.MapScreen.SCREEN),
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute(),
            networkChangeListener = networkChangeListener)
      },
      topBar = { TopAppBar(title = context.getString(R.string.map_screen_title)) },
      floatingActionButton = {
        Column(
            verticalArrangement =
                Arrangement.spacedBy(MaterialTheme.dimens.small3, Alignment.CenterVertically)) {

              // Recenter button
              FloatingActionButton(
                  onClick = { recenterOnMyLocation(mapView, myLocation) },
                  modifier = Modifier.testTag(C.Tag.MapScreen.MY_LOCATION_BUTTON),
              ) {
                Icon(
                    imageVector = Icons.Outlined.MyLocation,
                    contentDescription = "Recenter on my position",
                )
              }

              // Filter button
              FilterFab(isFilterApplied) { showFilterDialog = true }
            }
      },
      content = { paddingValues ->
        AndroidView(
            modifier =
                Modifier.padding(paddingValues)
                    .fillMaxSize()
                    .testTag(C.Tag.MapScreen.MAP_VIEW_CONTAINER),
            factory = { mapView },
        )

        if (showBottomSheet) {
          MapBottomSheet(
              sheetState = sheetState,
              content = content,
              onSheetDismissRequest = { showBottomSheet = false },
              alertToDisplay = alertViewModel.selectedAlert.value,
              onEditClick = {
                alertViewModel.selectAlert(alertViewModel.selectedAlert.value!!)
                navigationActions.navigateTo(Screen.EDIT_ALERT)
              },
              onAcceptClick = {
                val authUserData = authenticationViewModel.authUserData.value
                authUserData?.let {
                  Log.d(TAG, "Accepting alert from ${authUserData.uid}")
                  val channelCid =
                      chatViewModel.createChannel(
                          myUid = authUserData.uid,
                          palUid = alertViewModel.selectedAlert.value!!.uid,
                          myName = userViewModel.user.value!!.name,
                          palName = alertViewModel.selectedAlert.value!!.name)
                  Log.d(TAG, "Channel CID: $channelCid")
                  channelCid?.let {
                    val intent = ChannelActivity.getIntent(context, channelCid)
                    context.startActivity(intent)
                  }
                } ?: Log.e(TAG, "authUserData is null")
              },
              onResolveClick = {
                alertViewModel.deleteAlert(
                    alertViewModel.selectedAlert.value!!.id,
                    onSuccess = { showBottomSheet = false },
                    onFailure = { e ->
                      Log.e(TAG, "deleteAlert: fail to delete alert: ${e.message}")
                    })
              },
          )
        }

        if (showFilterDialog) {
          FilterDialog(
              context = context,
              currentRadius = radiusInMeters,
              location = selectedLocation,
              product =
                  productFilter?.let { productToPeriodPalsIcon(it).textId }
                      ?: FILTERS_NO_PREFERENCE_TEXT,
              urgency =
                  urgencyFilter?.let { urgencyToPeriodPalsIcon(it).textId }
                      ?: FILTERS_NO_PREFERENCE_TEXT,
              onDismiss = { showFilterDialog = false },
              onLocationSelected = { selectedLocation = it },
              onSave = { radius, product, urgency ->
                radiusInMeters = radius
                productFilter = stringToProduct(product)
                urgencyFilter = stringToUrgency(urgency)
                isFilterApplied =
                    (radius != 100.0) ||
                        (productFilter != Product.NO_PREFERENCE) ||
                        (urgencyFilter != null)

                selectedLocation?.let {
                  alertViewModel.fetchAlertsWithinRadius(
                      location = it,
                      radius = radiusInMeters,
                      onSuccess = {
                        Log.d(TAG, "Successfully fetched alerts within radius: $radiusInMeters")
                      },
                      onFailure = { e -> Log.e(TAG, "Error fetching alerts within radius", e) },
                  )
                } ?: Log.d(TAG, "Selected location is null")

                // if a product filter was selected, show only alerts with said product marked as
                // needed
                // (or alerts with no product preference)
                // if an urgency filter was selected, show only alerts with said urgency
                alertViewModel.setFilter {
                  (productFilter == Product.NO_PREFERENCE ||
                      (it.product == productFilter || it.product == Product.NO_PREFERENCE)) &&
                      (urgencyFilter == null || it.urgency == urgencyFilter)
                }
              },
              onReset = {
                radiusInMeters = DEFAULT_RADIUS
                selectedLocation = null
                isFilterApplied = false
                alertViewModel.removeFilters()
                productFilter = Product.NO_PREFERENCE
                urgencyFilter = null
              },
              locationViewModel = locationViewModel,
              gpsService = gpsService,
          )
        }
      },
  )
}

/**
 * Initializes the map with the given parameters.
 *
 * @param mapView Primary view for `osmdroid`.
 * @param myLocationOverlay Overlay upon which the current location marker is drawn
 * @param alertsOverlay Overlay upon which the alert markers are drawn
 * @param location GPS location of the user
 * @param isDarkTheme Reflects the system's theme
 * @param context The context of the activity.
 */
private fun initializeMap(
    mapView: MapView,
    myLocationOverlay: FolderOverlay,
    alertsOverlay: FolderOverlay,
    location: Location,
    isDarkTheme: Boolean,
    context: Context,
) {
  mapView.apply {
    setMultiTouchControls(true)
    minZoomLevel = MIN_ZOOM_LEVEL
    maxZoomLevel = MAX_ZOOM_LEVEL
    this.controller.setZoom(INITIAL_ZOOM_LEVEL)
    this.controller.setCenter(location.toGeoPoint())
    this.zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
    this.overlays.add(myLocationOverlay)
    this.overlays.add(alertsOverlay)
  }
  setTileSource(mapView = mapView, isDarkTheme = isDarkTheme, context = context)
}

/**
 * Draws the alert markers on the map.
 *
 * @param mapView View of the map upon which the markers will be drawn
 * @param alertOverlay Map overlay upon which alerts are drawn
 * @param context Context of the activity
 * @param myAlertsList Contains alerts posted by this user
 * @param palAlertsList Contains alerts posted by other users
 * @param onMyAlertClick Callback run when clicking on an alert posted by this user
 * @param onPalAlertClick Callback run when clicking on an alert posted by another user
 */
private fun updateAlertMarkers(
    mapView: MapView,
    alertOverlay: FolderOverlay,
    context: Context,
    myAlertsList: List<Alert>,
    palAlertsList: List<Alert>,
    onMyAlertClick: (Alert) -> Unit,
    onPalAlertClick: (Alert) -> Unit,
) {
  alertOverlay.items.clear()

  // Draw markers for my alerts
  myAlertsList.forEach { alert ->
    val alertLocation = Location.fromString(alert.location)
    val alertMarker =
        Marker(mapView).apply {
          position = alertLocation.toGeoPoint()
          setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
          title = "Alert"
          icon = ContextCompat.getDrawable(context, R.drawable.marker_blue)
          infoWindow = null // Hide the pop-up that appears when you click on a marker
          setOnMarkerClickListener { _, _ ->
            onMyAlertClick(alert)
            true // Return true to consume the event
          }
        }
    alertOverlay.add(alertMarker)
  }

  // Draw markers for pal alerts
  palAlertsList.forEach { alert ->
    val alertLocation = Location.fromString(alert.location)
    val alertMarker =
        Marker(mapView).apply {
          position = alertLocation.toGeoPoint()
          setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
          title = "Pal alert"
          icon = ContextCompat.getDrawable(context, R.drawable.marker_red)
          infoWindow = null
          setOnMarkerClickListener { _, _ ->
            onPalAlertClick(alert)
            true
          }
        }
    alertOverlay.add(alertMarker)
  }
  mapView.invalidate()
}

/**
 * Creates a new marker on the updated location
 *
 * @param mapView View of the map
 * @param overlay Overlay upon which the current location marker is drawn
 * @param context Context of the activity
 * @param myLocation GPS location of the user
 * @param myAccuracy Accuracy of the GPS location reading
 * @param onLocationClickCallback Run when clicking on "my location"marker
 */
private fun updateMyLocationMarker(
    mapView: MapView,
    overlay: FolderOverlay,
    context: Context,
    myLocation: Location,
    myAccuracy: Float,
    onLocationClickCallback: () -> Unit,
) {
  overlay.items.clear()
  val newMarker =
      Marker(mapView).apply {
        position = myLocation.toGeoPoint()
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        title = context.getString(R.string.map_your_location_marker_title)
        icon = ContextCompat.getDrawable(context, R.drawable.location)
        infoWindow = null // Hide the pop-up that appears when you click on a marker
        setOnMarkerClickListener { _, _ ->
          onLocationClickCallback()
          true // Return true to consume the event
        }
      }

  // Draws a translucent circle around the user location based on the accuracy of the location
  val accuracyCircle =
      Polygon(mapView).apply {
        points = Polygon.pointsAsCircle(myLocation.toGeoPoint(), myAccuracy.toDouble())
        fillPaint.color = ContextCompat.getColor(context, R.color.blue)
        fillPaint.alpha = 70
        strokeColor = ContextCompat.getColor(context, R.color.blue)
        strokeWidth = 0.0F
      }

  overlay.add(accuracyCircle)
  overlay.add(newMarker)
  mapView.invalidate()
}

/**
 * Depending on the system's theme, sets the tile source to
 * - a light one
 * - a dark one
 *
 * @param mapView The view of the map in which the tile source will be used
 * @param isDarkTheme True if the device is in dark theme
 * @param context The context of the activity
 */
private fun setTileSource(mapView: MapView, isDarkTheme: Boolean, context: Context) {

  val fileNameExtension = ".png"
  val tileSize = 256

  val tileName =
      if (isDarkTheme) context.getString(R.string.dark_tiles_name)
      else context.getString(R.string.light_tiles_name)
  val tileUrl =
      if (isDarkTheme) context.getString(R.string.dark_tiles_url)
      else context.getString(R.string.light_tiles_url)

  val customTileSource =
      object :
          OnlineTileSourceBase(
              tileName,
              MIN_ZOOM_LEVEL.toInt(),
              MAX_ZOOM_LEVEL.toInt(),
              tileSize,
              fileNameExtension,
              arrayOf(tileUrl),
          ) {
        override fun getTileURLString(pMapTileIndex: Long): String {
          // Construct URL for the API request
          val constructedUrl =
              baseUrl +
                  MapTileIndex.getZoom(pMapTileIndex) +
                  "/" +
                  MapTileIndex.getX(pMapTileIndex) +
                  "/" +
                  MapTileIndex.getY(pMapTileIndex) +
                  mImageFilenameEnding +
                  "?api_key=" +
                  BuildConfig.STADIA_MAPS_KEY
          return constructedUrl
        }
      }
  mapView.setTileSource(customTileSource)
}

/**
 * Recenters the map on the user location.
 *
 * @param mapView The view of the map
 * @param myLocation The location of the user.
 */
private fun recenterOnMyLocation(mapView: MapView, myLocation: Location) {
  mapView.controller.apply {
    animateTo(myLocation.toGeoPoint())
    setZoom(INITIAL_ZOOM_LEVEL)
  }
}
