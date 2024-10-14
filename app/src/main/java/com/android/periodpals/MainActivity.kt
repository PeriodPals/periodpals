package com.android.periodpals

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.periodpals.ui.map.MapScreen
import com.android.periodpals.ui.theme.PeriodPalsAppTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

  var locationPermissionGranted by mutableStateOf(false)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Initialize osmdroid configuration
    Configuration.getInstance().load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

    setContent {
      PeriodPalsAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          // CountriesList()
          MapScreen(modifier = Modifier.fillMaxSize(), locationPermissionGranted)
        }
      }
    }

    // Check and request location permission
    checkLocationPermission()
  }

  // Check if location permission is granted or request it if not
  private fun checkLocationPermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED) {
      // **Permission is granted, update state**
      locationPermissionGranted = true
    } else {
      // **Request permission**
      ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
    }
  }

  // Handle permission result and check if permission was granted
  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == 1 &&
        grantResults.isNotEmpty() &&
        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      // **Permission granted, update state**
      locationPermissionGranted = true
    } else {
      // **Permission denied, notify user**
      Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
    }
  }
}

@Composable
fun CountriesList(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  var countries by remember { mutableStateOf<List<Country>>(listOf()) }
  LaunchedEffect(Unit) { withContext(dispatcher) { countries = listOf(Country(1, "eyyo pogger")) } }
  LazyColumn {
    items(
        countries.size,
    ) { idx ->
      Text(
          countries[idx].name,
          modifier = Modifier.padding(8.dp),
      )
    }
  }
}

@Serializable
data class Country(
    val id: Int,
    val name: String,
)
