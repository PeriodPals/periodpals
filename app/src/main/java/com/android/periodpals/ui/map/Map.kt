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
private const val SCREEN_TITLE = "Map"
private const val YOUR_LOCATION_MARKER_TITLE = "Your location"

private const val MIN_ZOOM_LEVEL = 5.0
private const val MAX_ZOOM_LEVEL = 18.0
private const val INITIAL_ZOOM_LEVEL = 17.0

private const val CUSTOM_THEME_NAME = "Custom theme"
private const val ALIDADE_LIGHT_URL = "https://tiles.stadiamaps.com/tiles/alidade_smooth/"
private const val ALIDADE_DARK_URL = "https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/"
private const val DARK_THEME_CACHE_NAME = "osmdroid_dark_tiles"
private const val LIGHT_THEME_CACHE_NAME = "osmdroid_light_tiles"

/**
 * Screen that displays the top app bar, bottom navigation bar and a map containing a marker for the
 * user's location.
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
    // setTileCacheDir(context, isDarkTheme) does not work yet
    initializeMap(
        mapView = mapView,
        myLocationOverlay = myLocationOverlay,
        alertsOverlay = alertOverlay,
        location = myLocation,
        isDarkTheme = isDarkTheme)
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
            selectedItem = navigationActions.currentRoute())
      },
      topBar = { TopAppBar(title = SCREEN_TITLE) },
      floatingActionButton = {
        FloatingActionButton(
            onClick = { recenterOnMyLocation(mapView, myLocation) },
            modifier = Modifier.testTag(C.Tag.MapScreen.MAP_BUTTON)) {
              Icon(
                  imageVector = Icons.Outlined.MyLocation,
                  contentDescription = "Recenter on my position")
            }
      },
      content = { paddingValues ->
        MapViewContainer(
            modifier = Modifier.padding(paddingValues),
            myLocationOverlay = myLocationOverlay,
            context = context,
            mapView = mapView,
            myLocation = myLocation,
            myAccuracy = myAccuracy)
      })
}

/**
 * Composable that displays the map.
 *
 * @param modifier any modifiers to adjust how the map is composed in the screen
 * @param mapView primary view for `osmdroid`
 * @param myLocation location of the device
 */
@Composable
fun MapViewContainer(
    modifier: Modifier,
    myLocationOverlay: FolderOverlay,
    context: Context,
    mapView: MapView,
    myLocation: Location,
    myAccuracy: Float
) {

  LaunchedEffect(myLocation) {
    updateMyLocationMarker(
        mapView = mapView,
        overlay = myLocationOverlay,
        context = context,
        myLocation = myLocation,
        myAccuracy = myAccuracy)
  }

  AndroidView(
      modifier = modifier.testTag(C.Tag.MapScreen.MAP_VIEW_CONTAINER), factory = { mapView })
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
      onFailure = {
        Handler(Looper.getMainLooper()).post {
          Toast.makeText(context, "Error loading your data! Try again later.", Toast.LENGTH_SHORT)
              .show()
        }
        Log.d(TAG, "Authentication data is null")
      },
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
 * Initializes the map to a given zoom level at the user's location.
 *
 * @param mapView primary view for `osmdroid`.
 */
private fun initializeMap(
    mapView: MapView,
    myLocationOverlay: FolderOverlay,
    alertsOverlay: FolderOverlay,
    location: Location,
    isDarkTheme: Boolean
) {
  mapView.apply {
    setMultiTouchControls(true)
    minZoomLevel = MIN_ZOOM_LEVEL
    maxZoomLevel = MAX_ZOOM_LEVEL
    this.controller.setZoom(INITIAL_ZOOM_LEVEL)
    this.controller.setCenter(location.toGeoPoint())
    this.zoomController.setVisibility(
        CustomZoomButtonsController.Visibility.NEVER) // hide ugly map buttons
    this.overlays.add(myLocationOverlay)
    this.overlays.add(alertsOverlay)
  }

  setupCustomTileSource(
      mapView = mapView,
      url = ALIDADE_LIGHT_URL // if (isDarkTheme) ALIDADE_DARK_URL else ALIDADE_LIGHT_URL,
      )
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
        title = YOUR_LOCATION_MARKER_TITLE
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
 * Sets a custom tile source from URL.
 *
 * @param mapView The view of the map in which the tile source will be used
 * @param url The url of the tile source
 * @param name The name you want to give to the tile source
 * @param tileImageFileExtension The file extension of the tile images
 * @param minZoom The minimum zoom allowed
 * @param maxZoom The maximum zoom allowed
 * @param tileSize The size of the tiles in pixels
 */
private fun setupCustomTileSource(
    mapView: MapView,
    url: String,
    name: String = CUSTOM_THEME_NAME,
    tileImageFileExtension: String = ".png",
    minZoom: Int = 0,
    maxZoom: Int = 18,
    tileSize: Int = 256,
) {
  val customTileSource =
      object :
          OnlineTileSourceBase(
              name, minZoom, maxZoom, tileSize, tileImageFileExtension, arrayOf(url)) {
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

/**
 * Switches between the caches for dark-themed tiles and light-themed tiles.
 *
 * @param context The context of the activity
 * @param isDarkTheme True if theme is dark otherwise false
 */
private fun setTileCacheDir(context: Context, isDarkTheme: Boolean) {
  TODO("Properly implement the switching between tile caches")
  /*
  val cacheDirName = if (isDarkTheme) DARK_THEME_CACHE_NAME else LIGHT_THEME_CACHE_NAME
  val cacheDir = File(context.getExternalFilesDir(null), cacheDirName)

  Configuration.getInstance().osmdroidTileCache = cacheDir

  Log.d(TAG, "Setting cache to $cacheDirName")*/
}
