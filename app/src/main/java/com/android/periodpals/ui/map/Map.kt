package com.android.periodpals.ui.map

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.android.periodpals.BuildConfig
import com.android.periodpals.R
import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.location.Location
import com.android.periodpals.resources.C
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.ui.components.CONTENT
import com.android.periodpals.ui.components.MapBottomSheet
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

private const val TAG = "MapScreen"
private const val SCREEN_TITLE = "Map"
private const val YOUR_LOCATION_MARKER_TITLE = "Your location"

private const val MIN_ZOOM_LEVEL = 5.0
private const val MAX_ZOOM_LEVEL = 19.0
private const val INITIAL_ZOOM_LEVEL = 17.0

private const val LIGHT_TILES_URL = "https://tiles.stadiamaps.com/tiles/alidade_smooth/"
private const val DARK_TILES_URL = "https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/"
private const val DARK_TILES_NAME = "dark_tiles"
private const val LIGHT_TILES_NAME = "light_tiles"

/**
 * Screen that displays the top app bar, bottom navigation bar and a map. The map contains:
 * - the location of the user, along a translucent confidence circle representing the accuracy of
 *   the location
 * - markers for the locations where alerts where posted
 * - a recenter on the current location button.
 *
 * @param gpsService Provides the location of the device and the functions to interact with it
 * @param authenticationViewModel Manages the authentication data
 * @param alertViewModel Manages the alert data
 * @param navigationActions Provides the functions to navigate in the app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
  gpsService: GPSServiceImpl,
  authenticationViewModel: AuthenticationViewModel,
  alertViewModel: AlertViewModel,
  navigationActions: NavigationActions,
) {

  val context = LocalContext.current
  val mapView = remember { MapView(context) }
  val myLocation by gpsService.location.collectAsState()
  val myAccuracy by gpsService.accuracy.collectAsState()
  val isDarkTheme = isSystemInDarkTheme()
  val myLocationOverlay = remember { FolderOverlay() }
  val alertOverlay = remember { FolderOverlay() }

  val sheetState = rememberModalBottomSheetState()
  val scope = rememberCoroutineScope()
  var showBottomSheet by remember { mutableStateOf(false) }
  var content by remember { mutableStateOf(CONTENT.MY_ALERT) }
  val alertState = remember { mutableStateOf<Alert?>(null) }

  LaunchedEffect(Unit) {
    gpsService.askPermissionAndStartUpdates()

    initializeMap(
      mapView = mapView,
      myLocationOverlay = myLocationOverlay,
      alertsOverlay = alertOverlay,
      location = myLocation,
      isDarkTheme = isDarkTheme,
    )
  }

  FetchAlertsAndDrawMarkers(
    context = context,
    mapView = mapView,
    alertOverlay = alertOverlay,
    authenticationViewModel = authenticationViewModel,
    alertViewModel = alertViewModel,
    onMyAlertClick = { alert ->
      showBottomSheet = true
      content = CONTENT.MY_ALERT
      alertState.value = alert
    },
    onPalAlertClick = { alert ->
      showBottomSheet = true
      content = CONTENT.PAL_ALERT
      alertState.value = alert
    },
  )

  Scaffold(
    modifier = Modifier.fillMaxSize().testTag(C.Tag.MapScreen.SCREEN),
    bottomBar = {
      BottomNavigationMenu(
        onTabSelect = { route -> navigationActions.navigateTo(route) },
        tabList = LIST_TOP_LEVEL_DESTINATION,
        selectedItem = navigationActions.currentRoute(),
      )
    },
    topBar = { TopAppBar(title = SCREEN_TITLE) },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { recenterOnMyLocation(mapView, myLocation) },
        modifier = Modifier.testTag(C.Tag.MapScreen.MY_LOCATION_BUTTON),
      ) {
        Icon(
          imageVector = Icons.Outlined.MyLocation,
          contentDescription = "Recenter on my position",
        )
      }
    },
    content = { paddingValues ->
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

      AndroidView(
        modifier =
          Modifier.padding(paddingValues).fillMaxSize().testTag(C.Tag.MapScreen.MAP_VIEW_CONTAINER),
        factory = { mapView },
      )

      if (showBottomSheet) {
        MapBottomSheet(
          sheetState = sheetState,
          onDismissRequest = { showBottomSheet = false },
          onHideRequest = {
            scope
              .launch { sheetState.hide() }
              .invokeOnCompletion {
                if (!sheetState.isVisible) {
                  showBottomSheet = false
                }
              }
          },
          content = content,
          alert = alertState.value!! // TODO Check if this is a good idea
        )
      }
    },
  )
}

/**
 * Fetches the alerts from the database and upon receiving them draws them in the map.
 *
 * @param context The context of the activity
 * @param mapView The view of the map upon which the markers will be drawn
 * @param authenticationViewModel Manages the authentication data
 * @param alertViewModel Manages the alert data
 */
@Composable
private fun FetchAlertsAndDrawMarkers(
  context: Context,
  mapView: MapView,
  alertOverlay: FolderOverlay,
  authenticationViewModel: AuthenticationViewModel,
  alertViewModel: AlertViewModel,
  onMyAlertClick: (Alert) -> Unit,
  onPalAlertClick: (Alert) -> Unit,
) {
  authenticationViewModel.loadAuthenticationUserData(
    onFailure = {
      Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, "Error loading your data! Try again later.", Toast.LENGTH_SHORT)
          .show()
      }
      Log.d(TAG, "Authentication data is null")
    }
  )

  val uid by remember { mutableStateOf(authenticationViewModel.authUserData.value!!.uid) }
  alertViewModel.setUserID(uid)
  alertViewModel.fetchAlerts(
    onSuccess = {

      // For some reason, which I don't understand, accessing myAlerts and palAlerts directly
      // requires to leave and re-enter the map for them to show up. This is the quickest fix I found.
      val allAlerts = alertViewModel.alerts.value
      val myAlerts = allAlerts.filter { it.uid == uid }
      val palAlerts = allAlerts.filter { it.uid != uid }

      updateAlertMarkers(
        mapView = mapView,
        alertOverlay = alertOverlay,
        context = context,
        myAlertList = myAlerts,
        palAlertList = palAlerts,
        onMyAlertClick = onMyAlertClick,
        onPalAlertClick = onPalAlertClick,
      )
    },
    onFailure = { e -> Log.d(TAG, "Error fetching alerts: $e") },
  )
}

/**
 * Initializes the map to a given zoom level at the user's location.
 *
 * @param mapView primary view for `osmdroid`.
 */
private fun initializeMap(
  mapView: MapView,
  myLocationOverlay: FolderOverlay,
  alertsOverlay: FolderOverlay,
  location: Location,
  isDarkTheme: Boolean,
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
  setTileSource(mapView = mapView, isDarkTheme = isDarkTheme)
}

/**
 * Draws the alert markers on the map.
 *
 * @param mapView The view of the map upon which the markers will be drawn
 * @param context The context of the activity
 * @param myAlertList The list containing the alerts
 */
private fun updateAlertMarkers(
  mapView: MapView,
  alertOverlay: FolderOverlay,
  context: Context,
  myAlertList: List<Alert>,
  palAlertList: List<Alert>,
  onMyAlertClick: (Alert) -> Unit,
  onPalAlertClick: (Alert) -> Unit,
) {
  alertOverlay.items.clear()

  // Draw markers for my alerts
  myAlertList.forEach { alert ->
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
  palAlertList.forEach { alert ->
    val alertLocation = Location.fromString(alert.location)
    val alertMarker =
      Marker(mapView).apply {
        position = alertLocation.toGeoPoint()
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        title = "Pal alert"
        icon = ContextCompat.getDrawable(context, R.drawable.marker_red)
        infoWindow = null
        setOnMarkerClickListener {_, _ ->
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
 * @param mapView The view of the map upon which the marker will be drawn.
 * @param context The context of the activity
 * @param myLocation The location of the user
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
      title = YOUR_LOCATION_MARKER_TITLE
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
 */
private fun setTileSource(mapView: MapView, isDarkTheme: Boolean) {

  val fileNameExtension = ".png"
  val tileSize = 256

  val tileName = if (isDarkTheme) DARK_TILES_NAME else LIGHT_TILES_NAME
  val tileUrl = if (isDarkTheme) DARK_TILES_URL else LIGHT_TILES_URL

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
