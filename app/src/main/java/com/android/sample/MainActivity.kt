package com.android.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.android.sample.resources.C
import com.android.sample.ui.navigation.NavigationActions
import com.android.sample.ui.navigation.Route
import com.android.sample.ui.navigation.Screen
import com.android.sample.ui.theme.SampleAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SampleAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize().semantics { testTag = C.Tag.main_screen_container },
            color = MaterialTheme.colorScheme.background) {
              PeriodPalApp()
            }
      }
    }
  }
}

@Composable
fun PeriodPalApp() {
  val navController = rememberNavController()
  val navigationActions = NavigationActions(navController)

  // TODO: Uncomment what has been implemented
  NavHost(navController = navController, startDestination = Route.AUTH) {
    // Authentication
    navigation(startDestination = Screen.AUTH, route = Route.AUTH) {
      //      composable(Screen.AUTH) { SignInScreen(navigationActions) }
    }

    // Overview
    navigation(startDestination = Screen.OVERVIEW, route = Route.OVERVIEW) {
      //      composable(Screen.OVERVIEW) { OverviewScreen(navigationActions) }
      //      composable(Screen.REQUEST) { RequestScreen(navigationActions) }
      //      composable(Screen.OFFER) { OfferScreen(navigationActions) }
      //      composable(Screen.SETTINGS) { SettingsScreen(navigationActions) }
    }

    // Profile
    navigation(startDestination = Screen.PROFILE, route = Route.PROFILE) {
      //      composable(Screen.PROFILE) { ProfileScreen(navigationActions) }
      //      composable(Screen.CREATE_PROFILE) { CreateProfileScreen(navigationActions) }
      //      composable(Screen.EDIT_PROFILE) { EditProfileScreen(navigationActions) }
    }
  }
}
