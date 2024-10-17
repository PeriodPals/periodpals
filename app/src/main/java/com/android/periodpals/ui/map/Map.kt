package com.android.periodpals.ui.map

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.LocationServices
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar

// Define a constant for the default location on EPFL Campus
private val DEFAULT_LOCATION = GeoPoint(46.5191, 6.5668)

// Define a tag for logging
private const val TAG = "MapView"



@Composable
fun MapScreen(navigationActions: NavigationActions, modifier: Modifier = Modifier, locationPermissionGranted: Boolean) {
  val context = LocalContext.current
  val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
  val mapView = remember { MapView(context) }

  // Function to initialize the map
  fun initializeMap() {
    mapView.setTileSource(TileSourceFactory.MAPNIK)
    mapView.controller.setZoom(17.0)

    val scaleBarOverlay = ScaleBarOverlay(mapView)
    mapView.overlays.add(scaleBarOverlay)
  }

  LaunchedEffect(locationPermissionGranted) {

    // Initialize the map
    initializeMap()

    // Center the map on EPFL Campus initially
    mapView.controller.setCenter(DEFAULT_LOCATION)

    // Check if location permission is granted before accessing location
    if (locationPermissionGranted) {
      try {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
              if (location != null) {
                val userLocation = GeoPoint(location.latitude, location.longitude)
                mapView.controller.setCenter(userLocation)

                // Clear existing markers and add a new one for the user's location
                mapView.overlays.clear()
                val userMarker =
                    Marker(mapView).apply {
                      position = userLocation
                      setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                      title = "Your Location"
                    }
                mapView.overlays.add(userMarker)

                // Refresh the map to show the updated location and marker
                mapView.invalidate()
              } else {
                Toast.makeText(context, "Unable to retrieve location.", Toast.LENGTH_SHORT).show()
              }
            }
            .addOnFailureListener { exception ->
              // Updated log statement to use TAG
              Log.e(TAG, "Failed to retrieve location: ${exception.message}")
              Toast.makeText(context, "Failed to retrieve location.", Toast.LENGTH_SHORT).show()
            }
      } catch (e: SecurityException) {
        // Updated log statement to use TAG
        Log.e(TAG, "Location permission not granted: ${e.message}")
        Toast.makeText(context, "Location permission not granted.", Toast.LENGTH_SHORT).show()
      }
    }
  }

  DisposableEffect(Unit) { onDispose { mapView.onDetach() } }

    Scaffold (
        bottomBar = {
            BottomNavigationMenu(
                onTabSelect = { route -> navigationActions.navigateTo(route) },
                tabList = LIST_TOP_LEVEL_DESTINATION,
                selectedItem = navigationActions.currentRoute())
        },
        topBar = {
            TopAppBar(
                title = "Map",
            )
        },
        content = { pd -> Text("Map Screen", modifier = Modifier.fillMaxSize().padding(pd)) }
    )  { innerPadding ->
        // MapView setup and rendering
        AndroidView(
            modifier = modifier.testTag(TAG).padding(innerPadding),
            factory = {
                mapView.apply {
                    // Initialize the map
                    initializeMap()
                }
            })
    }
}
