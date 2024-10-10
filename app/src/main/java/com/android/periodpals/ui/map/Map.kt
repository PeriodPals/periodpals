package com.android.periodpals.ui.map

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay

class MapScreen : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize osmdroid configuration
        Configuration.getInstance()
            .load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

        // Set Compose content
        setContent {
            MapViewContainer()
        }
    }
}

@Composable
fun MapViewContainer(modifier: Modifier = Modifier) {
    AndroidMapView(modifier)
}

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
}

@Preview(showBackground = true)
@Composable
fun PreviewMapScreen1() {
    // Replace with a simple Box or any placeholder
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Map Preview")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMapScreen2() {
    MapViewContainer()
}