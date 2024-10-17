package com.android.periodpals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.android.periodpals.ui.alert.AlertListScreen
import com.android.periodpals.ui.alert.AlertScreen
import com.android.periodpals.ui.authentication.RegisterScreen
import com.android.periodpals.ui.authentication.SignInScreen
import com.android.periodpals.ui.map.MapScreen
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.profile.CreateProfileScreen
import com.android.periodpals.ui.profile.EditProfileScreen
import com.android.periodpals.ui.profile.ProfileScreen
import com.android.periodpals.ui.theme.PeriodPalsAppTheme
import com.android.periodpals.ui.timer.TimerScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      PeriodPalsAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          PeriodPalsApp()
        }
      }
    }
  }
}

@Composable
fun PeriodPalsApp() {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  NavHost(navController = navController, startDestination = Route.AUTH) {
    // Authentication
    navigation(
        startDestination = Screen.AUTH,
        route = Route.AUTH,
    ) {
      composable(Screen.AUTH) { SignInScreen(navigationActions) }
      composable(Screen.REGISTER) { RegisterScreen(navigationActions) }
      composable(Screen.CREATE_PROFILE) { CreateProfileScreen(navigationActions) }
    }

    // Alert push notifications
    navigation(
        startDestination = Screen.ALERT,
        route = Route.ALERT,
    ) {
      composable(Screen.ALERT) { AlertScreen(navigationActions) }
    }

    // Notifications received or pushed
    navigation(
        startDestination = Screen.ALERT_LIST,
        route = Route.ALERT_LIST,
    ) {
      composable(Screen.ALERT_LIST) { AlertListScreen(navigationActions) }
    }

    // Map
    navigation(
        startDestination = Screen.MAP,
        route = Route.MAP,
    ) {
      composable(Screen.MAP) { MapScreen(navigationActions) }
    }

    // Timer
    navigation(
        startDestination = Screen.TIMER,
        route = Route.TIMER,
    ) {
      composable(Screen.TIMER) { TimerScreen(navigationActions) }
    }

    // Profile
    navigation(
        startDestination = Screen.PROFILE,
        route = Route.PROFILE,
    ) {
      composable(Screen.PROFILE) { ProfileScreen(navigationActions) }
      composable(Screen.EDIT_PROFILE) { EditProfileScreen(navigationActions) }
    }
  }
}
