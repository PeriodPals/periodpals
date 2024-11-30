package com.android.periodpals.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import com.android.periodpals.BuildConfig
import com.android.periodpals.resources.C
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.LocationStyling
import org.ramani.compose.MapLibre

private const val SCREEN_TITLE = "Map"
private const val RECENTER_ZOOM_LEVEL = 17.0
private const val DEFAULT_ZOOM_LEVEL = 5.0
private val DEFAULT_CAMERA_COORDINATES = LatLng(46.8956,8.2461)
private const val TILE_STYLE_URL = "https://tiles.stadiamaps.com/styles/alidade_smooth.json?api_key="

/**
 * Screen that displays the top app bar, bottom navigation bar and a map containing a marker for the
 * user's location.
 *
 * @param gpsService Provides the location of the device and the functions to interact with it
 * @param navigationActions Provides the functions to navigate in the app
 */
@Composable
fun MapScreen(gpsService: GPSServiceImpl, navigationActions: NavigationActions) {

  val locationProperties = rememberSaveable { gpsService.locationPropertiesState }
  val userLocation = rememberSaveable { mutableStateOf(android.location.Location(null)) }
  val cameraPosition = rememberSaveable {
    mutableStateOf(CameraPosition(target = DEFAULT_CAMERA_COORDINATES, zoom = DEFAULT_ZOOM_LEVEL))
  }

  LaunchedEffect(Unit) {
    gpsService.askPermissionAndStartUpdates()
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
          cameraPosition.value = CameraPosition(cameraPosition.value).apply {
            this.target = LatLng(
              userLocation.value.latitude,
              userLocation.value.longitude
            )
            this.zoom = RECENTER_ZOOM_LEVEL
          }
        }
      ) {
        Icon(imageVector = Icons.Outlined.MyLocation, contentDescription = "My location")
      }
    },
    content = { paddingValues ->
      MapLibre(
        modifier = Modifier.padding(paddingValues).fillMaxSize().testTag(C.Tag.MapScreen.MAP_LIBRE),
        styleBuilder = Style.Builder().fromUri(TILE_STYLE_URL + BuildConfig.STADIA_MAPS_KEY),
        cameraPosition = cameraPosition.value,
        locationStyling = LocationStyling(
          enablePulse = true,
          pulseColor = MaterialTheme.colorScheme.primary.toArgb()
        ),
        locationRequestProperties = locationProperties.value,
        userLocation = userLocation
      )
    }
  )
}
