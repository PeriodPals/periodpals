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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.android.periodpals.model.location.UserLocationModelSupabase
import com.android.periodpals.model.location.UserLocationViewModel
import com.android.periodpals.model.timer.TimerManager
import com.android.periodpals.model.timer.TimerRepositorySupabase
import com.android.periodpals.model.timer.TimerViewModel
import com.android.periodpals.model.user.UserAuthenticationState
import com.android.periodpals.model.user.UserRepositorySupabase
import com.android.periodpals.model.user.UserViewModel
import com.android.periodpals.services.GPSServiceImpl
import com.android.periodpals.services.NetworkChangeListener
import com.android.periodpals.services.PushNotificationsService
import com.android.periodpals.services.PushNotificationsServiceImpl
import com.android.periodpals.ui.alert.AlertListsScreen
import com.android.periodpals.ui.alert.CreateAlertScreen
import com.android.periodpals.ui.alert.EditAlertScreen
import com.android.periodpals.ui.authentication.SignInScreen
import com.android.periodpals.ui.authentication.SignUpScreen
import com.android.periodpals.ui.chat.ChannelsScreenContainer
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
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.models.InitializationState
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory
import io.getstream.chat.android.state.plugin.config.StatePluginConfig
import io.getstream.chat.android.state.plugin.factory.StreamStatePluginFactory
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import org.osmdroid.config.Configuration

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
  private lateinit var gpsService: GPSServiceImpl
  private lateinit var pushNotificationsService: PushNotificationsServiceImpl
  private lateinit var chatViewModel: ChatViewModel
  private lateinit var timerManager: TimerManager
  private lateinit var networkChangeListener: NetworkChangeListener

  private val supabaseClient =
      createSupabaseClient(
          supabaseUrl = BuildConfig.SUPABASE_URL,
          supabaseKey = BuildConfig.SUPABASE_KEY,
      ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
      }

  private val authModel = AuthenticationModelSupabase(supabaseClient)
  private val authenticationViewModel = AuthenticationViewModel(authModel)

  private val userModel = UserRepositorySupabase(supabaseClient)
  private val userViewModel = UserViewModel(userModel)

  private val userLocationModel = UserLocationModelSupabase(supabaseClient)
  private val userLocationViewModel = UserLocationViewModel(userLocationModel)

  private val alertModel = AlertModelSupabase(supabaseClient)
  private val alertViewModel = AlertViewModel(alertModel)

  private val timerModel = TimerRepositorySupabase(supabaseClient)

  private lateinit var timerViewModel: TimerViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    gpsService = GPSServiceImpl(this, authenticationViewModel, userLocationViewModel)
    pushNotificationsService =
        PushNotificationsServiceImpl(this, authenticationViewModel, userViewModel)
    timerManager = TimerManager(this)
    timerViewModel = TimerViewModel(timerModel, timerManager)
    networkChangeListener = NetworkChangeListener(this)
    networkChangeListener.startListening()

    // Initialize osmdroid configuration getSharedPreferences(this)
    Configuration.getInstance().load(this, getSharedPreferences("osmdroid", Context.MODE_PRIVATE))

    // Check if Google Play Services are available
    GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)

    // Set up the OfflinePlugin for offline storage
    val offlinePluginFactory = StreamOfflinePluginFactory(appContext = applicationContext)
    val statePluginFactory =
        StreamStatePluginFactory(config = StatePluginConfig(), appContext = this)

    // Set up the chat client for API calls and with the plugin for offline storage
    val chatClient =
        ChatClient.Builder(BuildConfig.STREAM_SDK_KEY, applicationContext)
            .withPlugins(offlinePluginFactory, statePluginFactory)
            .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
            .build()

    chatViewModel = ChatViewModel(chatClient)

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
              chatClient,
              chatViewModel,
              networkChangeListener)
        }
      }
    }
  }

  override fun onStop() {
    super.onStop()
    gpsService.switchFromPreciseToApproximate()
    networkChangeListener.stopListening()
  }

  override fun onRestart() {
    super.onRestart()
    gpsService.switchFromApproximateToPrecise()
    networkChangeListener.startListening()
  }

  override fun onDestroy() {
    super.onDestroy()
    gpsService.cleanup()
    networkChangeListener.stopListening()
  }
}

/**
 * Handles the navigation logic based on the user's authentication state.
 *
 * This function observes the `userAuthenticationState` from the `AuthenticationViewModel` and
 * navigates to the appropriate screen based on the current state.
 *
 * @param authenticationViewModel The ViewModel that holds the user's authentication state.
 * @param navigationActions The actions used to navigate between screens.
 */
fun userAuthStateLogic(
    authenticationViewModel: AuthenticationViewModel,
    navigationActions: NavigationActions,
) {
  when (authenticationViewModel.userAuthenticationState.value) {
    is UserAuthenticationState.SuccessIsLoggedIn -> navigationActions.navigateTo(Screen.PROFILE)
    else -> Log.d("UserAuthStateLogic", "User is not logged in")
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
    chatClient: ChatClient,
    chatViewModel: ChatViewModel,
    networkChangeListener: NetworkChangeListener
) {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  val locationViewModel: LocationViewModel = viewModel(factory = LocationViewModel.Factory)

  userAuthStateLogic(authenticationViewModel, navigationActions)

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
            networkChangeListener,
            navigationActions,
        )
      }
    }

    // Notifications received or pushed
    navigation(startDestination = Screen.ALERT_LIST, route = Route.ALERT_LIST) {
      composable(Screen.ALERT_LIST) {
        AlertListsScreen(
            alertViewModel,
            userViewModel,
            authenticationViewModel,
            locationViewModel,
            gpsService,
            chatViewModel,
            networkChangeListener,
            navigationActions,
        )
      }
      composable(Screen.EDIT_ALERT) {
        EditAlertScreen(locationViewModel, gpsService, alertViewModel, navigationActions)
      }

      composable(Screen.CHAT) {
        val clientInitialisationState by chatClient.clientState.initializationState.collectAsState()
        val clientConnectionState by chatClient.clientState.connectionState.collectAsState()
        val context = LocalContext.current

        Log.d(TAG, "Client initialization state: $clientInitialisationState")

        ChatTheme {
          when (clientInitialisationState) {
            InitializationState.COMPLETE -> {
              Log.d(TAG, "Client initialization completed")
              Log.d(TAG, "Client connection state $clientConnectionState")

              ChannelsScreenContainer(navigationActions = navigationActions) {
                io.getstream.chat.android.compose.ui.channels.ChannelsScreen(
                    isShowingHeader = false,
                    onChannelClick = { channel ->
                      val intent = ChannelActivity.getIntent(context, channel.cid)
                      context.startActivity(intent)
                    },
                    onBackPressed = { navigationActions.navigateTo(Screen.ALERT_LIST) },
                )
              }
            }
            InitializationState.INITIALIZING -> {
              Log.d(TAG, "Client initializing")
            }
            InitializationState.NOT_INITIALIZED -> {
              Log.d(TAG, "Client not initialized yet.")
            }
          }
        }
      }
    }

    // Map
    navigation(startDestination = Screen.MAP, route = Route.MAP) {
      composable(Screen.MAP) {
        MapScreen(
            gpsService,
            authenticationViewModel,
            alertViewModel,
            locationViewModel,
            chatViewModel,
            userViewModel,
            networkChangeListener,
            navigationActions)
      }
    }

    // Timer
    navigation(startDestination = Screen.TIMER, route = Route.TIMER) {
      composable(Screen.TIMER) {
        TimerScreen(
            authenticationViewModel, timerViewModel, networkChangeListener, navigationActions)
      }
    }

    // Profile
    navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
      composable(Screen.PROFILE) {
        ProfileScreen(
            userViewModel,
            authenticationViewModel,
            pushNotificationsService,
            chatViewModel,
            networkChangeListener,
            navigationActions,
        )
      }
      composable(Screen.EDIT_PROFILE) { EditProfileScreen(userViewModel, navigationActions) }
      composable(Screen.SETTINGS) {
        SettingsScreen(userViewModel, authenticationViewModel, navigationActions)
      }
    }
  }
}
