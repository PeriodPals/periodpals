package com.android.periodpals.ui.map

import android.content.Context
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.android.periodpals.services.NetworkChangeListener
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar
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
@Composable
fun MapScreen(
    gpsService: GPSServiceImpl,
    authenticationViewModel: AuthenticationViewModel,
    alertViewModel: AlertViewModel,
    networkChangeListener: NetworkChangeListener,
    navigationActions: NavigationActions
) {

  val context = LocalContext.current
  val mapView = remember { MapView(context) }
  val myLocation by gpsService.location.collectAsState()
  val myAccuracy by gpsService.accuracy.collectAsState()
  val isDarkTheme = isSystemInDarkTheme()
  val myLocationOverlay = remember { FolderOverlay() }
  val alertOverlay = remember { FolderOverlay() }

  LaunchedEffect(Unit) {
    gpsService.askPermissionAndStartUpdates()

    initializeMap(
        mapView = mapView,
        myLocationOverlay = myLocationOverlay,
        alertsOverlay = alertOverlay,
        location = myLocation,
        isDarkTheme = isDarkTheme,
        context = context)
  }

  FetchAlertsAndDrawMarkers(
      context = context,
      mapView = mapView,
      alertOverlay = alertOverlay,
      authenticationViewModel = authenticationViewModel,
      alertViewModel = alertViewModel)

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
        FloatingActionButton(
            onClick = { recenterOnMyLocation(mapView, myLocation) },
            modifier = Modifier.testTag(C.Tag.MapScreen.MY_LOCATION_BUTTON)) {
              Icon(
                  imageVector = Icons.Outlined.MyLocation,
                  contentDescription = "Recenter on my position")
            }
      },
      content = { paddingValues ->
        LaunchedEffect(myLocation) {
          updateMyLocationMarker(
              mapView = mapView,
              overlay = myLocationOverlay,
              context = context,
              myLocation = myLocation,
              myAccuracy = myAccuracy)
        }

        AndroidView(
            modifier =
                Modifier.padding(paddingValues)
                    .fillMaxSize()
                    .testTag(C.Tag.MapScreen.MAP_VIEW_CONTAINER),
            factory = { mapView })
      })
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
    alertViewModel: AlertViewModel
) {
  authenticationViewModel.loadAuthenticationUserData(
      onFailure = { Log.d(TAG, "Authentication data is null") },
  )

  val uid by remember { mutableStateOf(authenticationViewModel.authUserData.value!!.uid) }
  alertViewModel.setUserID(uid)
  alertViewModel.fetchAlerts(
      onSuccess = {
        val alerts = alertViewModel.alerts.value
        updateAlertMarkers(
            mapView = mapView, alertOverlay = alertOverlay, context = context, alertList = alerts)
      },
      onFailure = { e -> Log.d(TAG, "Error fetching alerts: $e") })
}

/**
 * Initializes the map with the given parameters.
 *
 * @param mapView The primary view for `osmdroid`.
 * @param myLocationOverlay The overlay for the user's location.
 * @param alertsOverlay The overlay for the alerts.
 * @param location The initial location to center the map on.
 * @param isDarkTheme True if the device is in dark theme.
 * @param context The context of the activity.
 */
private fun initializeMap(
    mapView: MapView,
    myLocationOverlay: FolderOverlay,
    alertsOverlay: FolderOverlay,
    location: Location,
    isDarkTheme: Boolean,
    context: Context
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
 * @param mapView The view of the map upon which the markers will be drawn
 * @param context The context of the activity
 * @param alertList The list containing the alerts
 */
private fun updateAlertMarkers(
    mapView: MapView,
    alertOverlay: FolderOverlay,
    context: Context,
    alertList: List<Alert>
) {
  alertOverlay.items.clear()
  alertList.forEach { alert ->
    val alertLocation = Location.fromString(alert.location)
    val alertMarker =
        Marker(mapView).apply {
          position = alertLocation.toGeoPoint()
          setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
          title = "Alert"
          icon = ContextCompat.getDrawable(context, R.drawable.alert_marker)
          infoWindow = null // Hide the pop-up that appears when you click on a marker
          setOnMarkerClickListener { marker, mapView ->
            // TODO Implement what happens when you click on an alert item
            Log.d(TAG, "You clicked on an alert item!")

            true // Return true to consume the event
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
    myAccuracy: Float
) {
  overlay.items.clear()
  val newMarker =
      Marker(mapView).apply {
        position = myLocation.toGeoPoint()
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        title = context.getString(R.string.map_your_location_marker_title)
        icon = ContextCompat.getDrawable(context, R.drawable.location)
        infoWindow = null // Hide the pop-up that appears when you click on a marker
        setOnMarkerClickListener { marker, mapView ->
          // TODO Implement what happens when clicking on the location marker
          Log.d(TAG, "You clicked on your location marker!")

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
              arrayOf(tileUrl)) {
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
