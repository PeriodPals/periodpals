package com.android.periodpals.ui.map

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
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
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

private const val TAG = "MapScreen"
private const val SCREEN_TITLE = "Map"
private const val YOUR_LOCATION_MARKER_TITLE = "Your location"
private const val INITIAL_ZOOM_LEVEL = 17.0

/**
 * Screen that displays the top app bar, bottom navigation bar and a map containing a marker for the
 * user's location.
 *
 * @param gpsService Provides the location of the device and the functions to interact with it
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
    onSuccess = { alertViewModel.alerts.value },
    onFailure = { e -> Log.d(TAG, "Error fetching alerts: $e") })

  val myAlertsList = alertViewModel.myAlerts.value
  val palsAlertsList = alertViewModel.palAlerts.value

  LaunchedEffect(Unit) {
    gpsService.askPermissionAndStartUpdates()
    initializeMap(mapView, myLocation)
  }

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
        onClick = {
          mapView.controller.apply {
            setCenter(myLocation.toGeoPoint())
            setZoom(INITIAL_ZOOM_LEVEL)
          }
        }
      ) {
        Icon(imageVector = Icons.Outlined.MyLocation, contentDescription = "Recenter on my position")
      }
    },
    content = { paddingValues ->
      MapViewContainer(
        modifier = Modifier.padding(paddingValues),
        context = context,
        mapView = mapView,
        myLocation = myLocation,
        alertList = palsAlertsList)
    }
  )
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
  context: Context,
  mapView: MapView,
  myLocation: Location,
  alertList: List<Alert>
) {

  var myLocationMarker by remember { mutableStateOf<Marker?>(null) }

  LaunchedEffect(Unit) {
    mapView.overlays.clear()
    alertList.forEach { alert ->
      val alertLocation = Location.fromString(alert.location)
      val alertMarker = Marker(mapView).apply {
        position = alertLocation.toGeoPoint()
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        title = "Alert"
        icon = ContextCompat.getDrawable(context, R.drawable.alert_marker)
      }
    mapView.overlays.add(alertMarker)
    }
    mapView.invalidate()
  }

  LaunchedEffect(myLocation) {
    myLocationMarker?.let { mapView.overlays.remove(it) }
    myLocationMarker = Marker(mapView).apply {
      position = myLocation.toGeoPoint()
      setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
      title = YOUR_LOCATION_MARKER_TITLE
      icon = ContextCompat.getDrawable(context, R.drawable.location)
    }
    mapView.overlays.add(myLocationMarker)
    mapView.invalidate()
    Log.d(TAG, "You moved to: $myLocation)")
  }

  AndroidView(
      modifier = modifier.testTag(C.Tag.MapScreen.MAP_VIEW_CONTAINER), factory = { mapView })
}

/**
 * Initializes the map to a given zoom level at the user's location.
 *
 * @param mapView primary view for `osmdroid`.
 */
private fun initializeMap(mapView: MapView, location: Location) {
  mapView.apply {
    setTileSource(TileSourceFactory.MAPNIK)
    setMultiTouchControls(true)
    this.controller.setZoom(INITIAL_ZOOM_LEVEL)
    this.controller.setCenter(location.toGeoPoint())
  }
}