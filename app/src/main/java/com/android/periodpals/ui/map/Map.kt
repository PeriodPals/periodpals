package com.android.periodpals.ui.map

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.android.periodpals.model.location.GPSLocation
import com.android.periodpals.resources.C
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay

private const val SCREEN_TITLE = "Map"

private const val INITIAL_ZOOM_LEVEL = 17.0

@Composable
fun MapScreen(gpsService: GPSServiceImpl, navigationActions: NavigationActions) {

  val context = LocalContext.current
  val mapView = remember { MapView(context) }
  val scaleBarOverlay = remember { ScaleBarOverlay(mapView) }
  val location by gpsService.location.collectAsState()

  // Only executed once
  LaunchedEffect (Unit) {
    gpsService.askUserForLocationPermission()
    initializeMap(mapView, scaleBarOverlay)
  }

  Scaffold(
    modifier = Modifier.fillMaxSize().testTag(C.Tag.MapScreen.SCREEN),
    bottomBar = {
      BottomNavigationMenu(
        onTabSelect = { route -> navigationActions.navigateTo(route) },
        tabList = LIST_TOP_LEVEL_DESTINATION,
        selectedItem = navigationActions.currentRoute()
      )
    },
    topBar = { TopAppBar(title = SCREEN_TITLE) },
    content = { paddingValues ->
      MapViewContainer(
        modifier = Modifier.padding(paddingValues),
        mapView = mapView,
        location = location
      )
    }
  )
}

@Composable
fun MapViewContainer(
    modifier: Modifier,
    mapView: MapView,
    location: GPSLocation
) {
  val geoPoint = location.toGeoPoint()

  // Update map center and markers when location changes
  LaunchedEffect (location) {
    mapView.controller.setCenter(geoPoint)
    updateMapMarkers(mapView, geoPoint)
  }

  AndroidView(modifier = modifier.testTag(C.Tag.MapScreen.MAP_VIEW_CONTAINER), factory = { mapView })
}

/** Initializes the map to a given zoom level and with a scale bar.
 * @param mapView primary view for `osmdroid`.
 * @param scaleBarOverlay scale bar that is displayed at the top left corner of the map.
 */
private fun initializeMap(mapView: MapView, scaleBarOverlay: ScaleBarOverlay) {
  mapView.setTileSource(TileSourceFactory.MAPNIK)
  mapView.controller.setZoom(INITIAL_ZOOM_LEVEL)
  mapView.overlays.add(scaleBarOverlay)
}

/** Updates the map markers.
 * @param mapView primary view for `osmdroid`.
 * @param geoPoint GPS location of the user.
 */
private fun updateMapMarkers(mapView: MapView, geoPoint: GeoPoint) {
  mapView.overlays.clear()
  val userMarker =
    Marker(mapView).apply {
      position = geoPoint
      setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
      title = "Your location"
    }
  mapView.overlays.add(userMarker)
  mapView.invalidate()
}

