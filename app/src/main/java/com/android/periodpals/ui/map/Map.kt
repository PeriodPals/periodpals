package com.android.periodpals.ui.map

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.LocationServices
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay

@Composable
fun MapViewContainer(modifier: Modifier = Modifier, locationPermissionGranted: Boolean) {
    AndroidMapView(modifier, locationPermissionGranted)
}

@Composable
fun AndroidMapView(modifier: Modifier = Modifier, locationPermissionGranted: Boolean) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val mapView = remember { MapView(context) }

    LaunchedEffect(locationPermissionGranted) {
        // Set the tile source and zoom level
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(17.0)

        // Center the map on EPFL Campus initially
        val epflLocation = GeoPoint(46.5191, 6.5668)
        mapView.controller.setCenter(epflLocation)

        // Add a scale bar
        val scaleBarOverlay = ScaleBarOverlay(mapView)
        mapView.overlays.add(scaleBarOverlay)

        // **Check if location permission is granted before accessing location**
        if (locationPermissionGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val userLocation = GeoPoint(location.latitude, location.longitude)
                        mapView.controller.setCenter(userLocation)

                        // Clear existing markers and add a new one for the user's location
                        mapView.overlays.clear()
                        val userMarker = Marker(mapView).apply {
                            position = userLocation
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = "Your Location"
                        }
                        mapView.overlays.add(userMarker)

                        // Refresh the map to show the updated location and marker
                        mapView.invalidate()
                    } else {
                        Toast.makeText(context, "Unable to retrieve location.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }.addOnFailureListener { exception ->
                    Log.e("MapView", "Failed to retrieve location: ${exception.message}")
                    Toast.makeText(context, "Failed to retrieve location.", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: SecurityException) {
                Log.e("MapView", "Location permission not granted: ${e.message}")
                Toast.makeText(context, "Location permission not granted.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }

    // MapView setup and rendering
    AndroidView(
        modifier = modifier,
        factory = {
            mapView.apply {
                // Set the tile source and zoom level
                setTileSource(TileSourceFactory.MAPNIK)
                controller.setZoom(17.0)

                // Add scale bar
                val scaleBarOverlay = ScaleBarOverlay(this)
                overlays.add(scaleBarOverlay)
            }
        }
    )
}

/*
@Composable
fun AndroidMapView(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val mapView = MapView(context).apply {
                // Set the tile source and zoom level
                setTileSource(TileSourceFactory.MAPNIK)
                controller.setZoom(17.0)

                // Center the map on EPFL Campus
                val epflLocation = GeoPoint(46.5191, 6.5668)
                controller.setCenter(epflLocation)

                // Add a marker at EPFL
                val marker = Marker(this).apply {
                    position = epflLocation
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "EPFL Campus"
                }
                overlays.add(marker)

                // Add a scale bar
                val scaleBarOverlay = ScaleBarOverlay(this)
                overlays.add(scaleBarOverlay)
            }
            mapView
        }
    )
}*/