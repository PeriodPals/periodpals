package com.android.periodpals

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.periodpals.model.authentication.AuthenticationModelSupabase
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.services.LocationAccessType
import com.android.periodpals.services.LocationService
import com.android.periodpals.services.LocationServiceImpl
import com.android.periodpals.ui.alert.AlertListScreen
import com.android.periodpals.ui.alert.AlertScreen
import com.android.periodpals.ui.authentication.SignInScreen
import com.android.periodpals.ui.authentication.SignUpScreen
import com.android.periodpals.ui.map.MapScreen
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.profile.CreateProfileScreen
import com.android.periodpals.ui.profile.EditProfileScreen
import com.android.periodpals.ui.profile.ProfileScreen
import com.android.periodpals.ui.theme.PeriodPalsAppTheme
import com.android.periodpals.ui.timer.TimerScreen
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

  private val locationService = LocationServiceImpl(this)

  private val supabaseClient =
      createSupabaseClient(
          supabaseUrl = BuildConfig.SUPABASE_URL,
          supabaseKey = BuildConfig.SUPABASE_KEY,
      ) {
        install(Auth)
      }

  private val authModel = AuthenticationModelSupabase(supabaseClient)
  private val authenticationViewModel = AuthenticationViewModel(authModel)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Initialize osmdroid configuration
    Configuration.getInstance().load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

    setContent {
      PeriodPalsAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          PeriodPalsApp(locationService, authenticationViewModel)
        }
      }
    }
  }
}

@Composable
fun PeriodPalsApp(
    locationService: LocationServiceImpl,
    authenticationViewModel: AuthenticationViewModel,
) {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  NavHost(navController = navController, startDestination = Route.AUTH) {
    // Authentication
    navigation(startDestination = Screen.SIGN_IN, route = Route.AUTH) {
      composable(Screen.SIGN_IN) { SignInScreen(authenticationViewModel, navigationActions) }
      composable(Screen.SIGN_UP) { SignUpScreen(authenticationViewModel, navigationActions) }
      composable(Screen.CREATE_PROFILE) { CreateProfileScreen(navigationActions) }
    }

    // Alert push notifications
    navigation(startDestination = Screen.ALERT, route = Route.ALERT) {
      composable(Screen.ALERT) { AlertScreen(navigationActions) }
    }

    // Notifications received or pushed
    navigation(startDestination = Screen.ALERT_LIST, route = Route.ALERT_LIST) {
      composable(Screen.ALERT_LIST) { AlertListScreen(navigationActions) }
    }

    // Map
    navigation(startDestination = Screen.MAP, route = Route.MAP) {
      composable(Screen.MAP) {
        MapScreen(Modifier.fillMaxSize(), locationService, navigationActions)
      }
    }

    // Timer
    navigation(startDestination = Screen.TIMER, route = Route.TIMER) {
      composable(Screen.TIMER) { TimerScreen(navigationActions) }
    }

    // Profile
    navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
      composable(Screen.PROFILE) { ProfileScreen(navigationActions) }
      composable(Screen.EDIT_PROFILE) { EditProfileScreen(navigationActions) }
    }
  }
}
