package com.android.periodpals

// import androidx.navigation.compose.NavHost
// import androidx.navigation.compose.composable
// import androidx.navigation.navigation
// import com.android.periodpals.ui.navigation.Route
// import com.android.periodpals.ui.navigation.Screen
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
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.android.periodpals.ui.map.MapScreen
import com.android.periodpals.model.user.UserRepositorySupabase
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.profile.CreateProfile
import com.android.periodpals.ui.theme.PeriodPalsAppTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {


  var locationPermissionGranted by mutableStateOf(false)

  // Constants for request codes
  companion object {
    private const val LOCATION_PERMISSION_REQUEST_CODE = 1
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Initialize osmdroid configuration
    Configuration.getInstance().load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

    setContent {
      PeriodPalsAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          PeriodPalsApp(locationPermissionGranted)
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
      ActivityCompat.requestPermissions(
          this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
    }
  }

  // Handle permission result and check if permission was granted
  @Deprecated("Deprecated in Java")
  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
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
fun PeriodPalsApp(locationPermissionGranted: Boolean) {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)
  //MapScreen(Modifier.fillMaxSize(), locationPermissionGranted)
  val db = UserViewModel(UserRepositorySupabase())
  CreateProfile(db)
  // CountriesList()

  // TODO: Uncomment what has been implemented

  //    NavHost(navController = navController, startDestination = Route.AUTH) {
  //      // Authentication
  //      navigation(
  //          startDestination = Screen.AUTH,
  //          route = Route.AUTH,
  //      ) {
  //        composable(Screen.AUTH) { SignInScreen(navigationActions) }
  //        composable(Screen.REGISTER) { RegisterScreen(navigationActions) }
  //        composable(Screen.CREATE_PROFILE) { CreateProfileScreen(navigationActions) }
  //      }
  //
  //      // Alert push notifications
  //      navigation(
  //          startDestination = Screen.ALERT,
  //          route = Route.ALERT,
  //      ) {
  //        composable(Screen.ALERT) { AlertScreen(navigationActions) }
  //      }
  //
  //      // Notifications received or pushed
  //      navigation(
  //          startDestination = Screen.ALERT_LIST,
  //          route = Route.ALERT_LIST,
  //      ) {
  //        composable(Screen.ALERT_LIST) { AlertListScreen(navigationActions) }
  //      }
  //
  //      // Map
  //      navigation(
  //          startDestination = Screen.MAP,
  //          route = Route.MAP,
  //      ) {
  //        composable(Screen.MAP) { MapScreen(navigationActions) }
  //      }
  //
  //      // Timer
  //      navigation(
  //          startDestination = Screen.TIMER,
  //          route = Route.TIMER,
  //      ) {
  //        composable(Screen.TIMER) { TimerScreen(navigationActions) }
  //      }
  //
  //      // Profile
  //      navigation(
  //          startDestination = Screen.PROFILE,
  //          route = Route.PROFILE,
  //      ) {
  //        composable(Screen.PROFILE) { ProfileScreen(navigationActions) }
  //        composable(Screen.EDIT_PROFILE) { EditProfileScreen(navigationActions) }
  //      }
  //    }
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
