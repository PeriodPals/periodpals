package com.android.periodpals

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import com.android.periodpals.model.user.UserRepositorySupabase
import com.android.periodpals.model.user.UserViewModel
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
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

  private lateinit var gpsService: GPSServiceImpl
  private lateinit var pushNotificationsService: PushNotificationsServiceImpl
  private lateinit var chatViewModel: ChatViewModel
  private lateinit var timerManager: TimerManager

  private val supabaseClient =
      createSupabaseClient(
          supabaseUrl = BuildConfig.SUPABASE_URL,
          supabaseKey = BuildConfig.SUPABASE_KEY,
      ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
        install(GoTrue)
      }

  private val authModel = AuthenticationModelSupabase(supabaseClient)
  private val authenticationViewModel = AuthenticationViewModel(authModel)

  private val userModel = UserRepositorySupabase(supabaseClient)
  private val userViewModel = UserViewModel(userModel)

  private val alertModel = AlertModelSupabase(supabaseClient)
  private val alertViewModel = AlertViewModel(alertModel)

  private val timerModel = TimerRepositorySupabase(supabaseClient)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    gpsService = GPSServiceImpl(this)
    pushNotificationsService = PushNotificationsServiceImpl(this, userViewModel)
    timerManager = TimerManager(this)
    val timerViewModel = TimerViewModel(timerModel, timerManager)

    // Initialize osmdroid configuration getSharedPreferences(this)
    Configuration.getInstance().load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

    // Check if Google Play Services are available
    GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)

    chatViewModel = ChatViewModel()

    setContent {
      PeriodPalsAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          PeriodPalsApp(
              gpsService,
              pushNotificationsService,
              authenticationViewModel,
              userViewModel,
              alertViewModel,
              timerViewModel,
              chatViewModel)
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
    gpsService: GPSServiceImpl,
    pushNotificationsService: PushNotificationsService,
    authenticationViewModel: AuthenticationViewModel,
    userViewModel: UserViewModel,
    alertViewModel: AlertViewModel,
    timerViewModel: TimerViewModel,
    chatViewModel: ChatViewModel
) {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  val locationViewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory)

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
        AlertListsScreen(navigationActions, alertViewModel, authenticationViewModel)
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
