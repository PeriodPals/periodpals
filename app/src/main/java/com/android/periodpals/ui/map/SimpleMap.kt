package com.android.periodpals.ui.map

import android.Manifest
import android.graphics.Color
import android.location.Location
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.android.periodpals.BuildConfig
import com.android.periodpals.R
import com.android.periodpals.services.GPSServiceImpl
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.ramani.compose.CameraPosition
import org.ramani.compose.LocationRequestProperties
import org.ramani.compose.LocationStyling
import org.ramani.compose.MapLibre
import org.ramani.compose.Symbol

@Composable
fun SimpleMapScreen(gpsService: GPSServiceImpl) {

  LaunchedEffect(Unit) { gpsService.askPermissionAndStartUpdates() }

  val locationProperties = rememberSaveable { gpsService.locationPropertiesState }
  val cameraPosition = rememberSaveable { mutableStateOf(CameraPosition()) }
  val userLocation = rememberSaveable { mutableStateOf(Location(null)) }

  Box {
    Surface(
      modifier = Modifier.fillMaxSize()
    ) {
      MapLibre(
        modifier = Modifier.fillMaxSize(),
        styleBuilder = Style.Builder().fromUri(
          "https://tiles.stadiamaps.com/styles/alidade_smooth.json?api_key="
                  + BuildConfig.STADIA_MAPS_KEY
        ),
        cameraPosition = cameraPosition.value,
        locationStyling =
        LocationStyling(
          enablePulse = true, pulseColor = Color.BLUE),
        locationRequestProperties = locationProperties.value,
        userLocation = userLocation
      ) {
        Symbol(
          center = com.android.periodpals.model.location.Location.DEFAULT_LOCATION.toLatLng(),
          size = 0.5F,
          color = "Red",
          isDraggable = false,
          imageId = R.drawable.red_marker,
        )
      }
    }
  }
}