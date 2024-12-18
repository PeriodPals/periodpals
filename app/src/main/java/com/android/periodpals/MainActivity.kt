package com.android.periodpals

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.periodpals.model.alert.AlertModelSupabase
import com.android.periodpals.model.alert.AlertViewModel
import com.android.periodpals.model.authentication.AuthenticationModelSupabase
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.chat.ChatViewModel
import com.android.periodpals.model.location.LocationViewModel
import com.android.periodpals.model.timer.TimerManager
import com.android.periodpals.model.timer.TimerRepositorySupabase
import com.android.periodpals.model.timer.TimerViewModel
import com.android.periodpals.model.user.UserModelPowerSync
import com.android.periodpals.model.user.UserRepository
import com.android.periodpals.model.user.UserRepositorySupabase
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.resources.localSchema
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.services.PushNotificationsService
import com.android.periodpals.services.PushNotificationsServiceImpl
import com.android.periodpals.ui.alert.AlertListsScreen
import com.android.periodpals.ui.alert.CreateAlertScreen
import com.android.periodpals.ui.alert.EditAlertScreen
import com.android.periodpals.ui.authentication.SignInScreen
import com.android.periodpals.ui.authentication.SignUpScreen
import com.android.periodpals.ui.chat.ChatScreen
import com.android.periodpals.ui.map.MapScreen
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.profile.CreateProfileScreen
import com.android.periodpals.ui.profile.EditProfileScreen
import com.android.periodpals.ui.profile.ProfileScreen
import com.android.periodpals.ui.settings.SettingsScreen
import com.android.periodpals.ui.theme.PeriodPalsAppTheme
import com.android.periodpals.ui.timer.TimerScreen
import com.google.android.gms.common.GoogleApiAvailability
import com.powersync.DatabaseDriverFactory
import com.powersync.PowerSyncDatabase
import com.powersync.compose.rememberDatabaseDriverFactory
import com.powersync.connector.supabase.SupabaseConnector
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

  lateinit var gpsService: GPSServiceImpl

  fun setGPSService(service: GPSServiceImpl) {
      this.gpsService = service
  }

  override fun onCreate(savedInstanceState: Bundle
  ?) {
    super.onCreate(savedInstanceState)

    // Initialize osmdroid configuration getSharedPreferences(this)
    Configuration.getInstance().load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

    // Check if Google Play Services are available
    GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)

    setContent {
      PeriodPalsAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          PeriodPalsApp(this)
        }
      }
    }
  }

  override fun onStop() {
    super.onStop()
    gpsService.switchFromPreciseToApproximate()
  }

  override fun onRestart() {
    super.onRestart()
    gpsService.switchFromApproximateToPrecise()
  }

  override fun onDestroy() {
    super.onDestroy()
    gpsService.cleanup()
  }
}

@Composable
fun PeriodPalsApp(
    main: MainActivity
) {
  // Supabase Client init with the necessary extentions installed
  val supabaseClient = remember {
      createSupabaseClient(
          supabaseUrl = BuildConfig.SUPABASE_URL,
          supabaseKey = BuildConfig.SUPABASE_KEY,
      ) {
          install(Auth)
          install(Postgrest)
          install(Storage)
      }
  }

  // PowerSync x Supabase Local-first db
  val supabaseConnector = remember {
      SupabaseConnector(
          powerSyncEndpoint = BuildConfig.POWERSYNC_URL, supabaseClient = supabaseClient)
  }
  val dbDriver = rememberDatabaseDriverFactory()
  val db = remember {
      PowerSyncDatabase(dbDriver, schema = localSchema)
  }

  // View Models
  val authModel = remember { AuthenticationModelSupabase(supabaseClient)}
  val authenticationViewModel = remember { AuthenticationViewModel(authModel) }

  val userModel = remember { UserModelPowerSync(db, supabaseConnector, supabaseClient) }
  val userViewModel = remember { UserViewModel(userModel) }

  val alertModel = remember { AlertModelSupabase(supabaseClient) }
  val alertViewModel = remember { AlertViewModel(alertModel) }

  val timerModel = remember { TimerRepositorySupabase(supabaseClient) }
  val timerManager = remember { TimerManager(main) }
  val timerViewModel = remember { TimerViewModel(timerModel, timerManager) }

  val pushNotificationsService = remember { PushNotificationsServiceImpl(main, userViewModel) }

  val gpsService = remember { GPSServiceImpl(main, userViewModel) }

  val chatViewModel = remember { ChatViewModel() }

  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  val locationViewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory)

  //Clean up
  main.setGPSService(gpsService)
  runBlocking { withContext(Dispatchers.IO) { db.connect(supabaseConnector) } }


  NavHost(navController = navController, startDestination = Route.AUTH) {
    // Authentication
    navigation(startDestination = Screen.SIGN_IN, route = Route.AUTH) {
      composable(Screen.SIGN_IN) { SignInScreen(authenticationViewModel, navigationActions) }
      composable(Screen.SIGN_UP) { SignUpScreen(authenticationViewModel, navigationActions) }
      composable(Screen.CREATE_PROFILE) { CreateProfileScreen(userViewModel, navigationActions) }
    }

    // Alert push notifications
    navigation(startDestination = Screen.ALERT, route = Route.ALERT) {
      composable(Screen.ALERT) {
        CreateAlertScreen(
            locationViewModel,
            gpsService,
            alertViewModel,
            authenticationViewModel,
            userViewModel,
            navigationActions,
        )
      }
    }

    // Notifications received or pushed
    navigation(startDestination = Screen.ALERT_LIST, route = Route.ALERT_LIST) {
      composable(Screen.ALERT_LIST) {
        AlertListsScreen(
            alertViewModel,
            authenticationViewModel,
            locationViewModel,
            gpsService,
            navigationActions)
      }
      composable(Screen.EDIT_ALERT) {
        EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
      }
      composable(Screen.CHAT) { ChatScreen(chatViewModel, navigationActions) }
    }

    // Map
    navigation(startDestination = Screen.MAP, route = Route.MAP) {
      composable(Screen.MAP) {
        MapScreen(gpsService, authenticationViewModel, alertViewModel, navigationActions)
      }
    }

    // Timer
    navigation(startDestination = Screen.TIMER, route = Route.TIMER) {
      composable(Screen.TIMER) {
        TimerScreen(authenticationViewModel, timerViewModel, navigationActions)
      }
    }

    // Profile
    navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
      composable(Screen.PROFILE) {
        ProfileScreen(userViewModel, pushNotificationsService, navigationActions)
      }
      composable(Screen.EDIT_PROFILE) { EditProfileScreen(userViewModel, navigationActions) }
      composable(Screen.SETTINGS) {
        SettingsScreen(userViewModel, authenticationViewModel, navigationActions)
      }
    }
  }
}
