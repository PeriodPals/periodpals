package com.android.periodpals

import android.os.Bundle
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import com.android.periodpals.ui.navigation.Screen
import com.android.periodpals.ui.theme.PeriodPalsAppTheme
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      PeriodPalsAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          CountriesList()
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
    navigation(
        startDestination = Screen.AUTH,
        route = Route.AUTH,
    ) {
      //        composable(Screen.AUTH) { SignInScreen(navigationActions) }
      //        composable(Screen.REGISTER) { RegisterScreen(navigationActions) }
      //        composable(Screen.CREATE_PROFILE) { CreateProfileScreen(navigationActions) }
    }

    // Alert push notifications
    navigation(
        startDestination = Screen.ALERT,
        route = Route.ALERT,
    ) {
      //        composable(Screen.ALERT) { AlertScreen(navigationActions) }
    }

    // Notifications received or pushed
    navigation(
        startDestination = Screen.NOTIF,
        route = Route.NOTIF,
    ) {
      //        composable(Screen.NOTIF) { NotifScreen(navigationActions) }
    }

    // Map
    navigation(
        startDestination = Screen.MAP,
        route = Route.MAP,
    ) {
      //        composable(Screen.MAP) { MapScreen(navigationActions) }
    }

    // Timer
    navigation(
        startDestination = Screen.TIMER,
        route = Route.TIMER,
    ) {
      //        composable(Screen.TIMER) { TimerScreen(navigationActions) }
    }

    // Profile
    navigation(
        startDestination = Screen.PROFILE,
        route = Route.PROFILE,
    ) {
      //        composable(Screen.PROFILE) { ProfileScreen(navigationActions) }
      //        composable(Screen.EDIT_PROFILE) { EditProfileScreen(navigationActions) }
    }
  }
}

@Composable
fun CountriesList(dispatcher: CoroutineDispatcher = Dispatchers.IO) {
  var countries by remember { mutableStateOf<List<Country>>(listOf()) }
  LaunchedEffect(Unit) {
    withContext(dispatcher) {
      countries = supabase.from("countries").select().decodeList<Country>()
    }
  }
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

val supabase =
    createSupabaseClient(
        supabaseUrl = "https://bhhjdcvdcfrxczbudraf.supabase.co",
        supabaseKey =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJoaGpkY3ZkY2ZyeGN6YnVkcmFmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjc4ODA4MjMsImV4cCI6MjA0MzQ1NjgyM30.teiPmTsMGNbXBx808uX7enVVLdgxqn4ftvSKjIgfCyQ") {
          install(Postgrest)
        }

@Serializable
data class Country(
    val id: Int,
    val name: String,
)
