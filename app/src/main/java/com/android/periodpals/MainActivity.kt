package com.android.periodpals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.android.periodpals.ui.profile.CreateProfile
import com.android.periodpals.ui.theme.PeriodPalsAppTheme

// import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      PeriodPalsAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          // CountriesList()
          CreateProfile()
        }
      }
    }
  }
}

/**
 * @Composable fun CountriesList(dispatcher: CoroutineDispatcher = Dispatchers.IO) { var countries
 *   by remember { mutableStateOf<List<Country>>(listOf()) } LaunchedEffect(Unit) {
 *   withContext(dispatcher) { countries = supabase.from("countries").select().decodeList<Country>()
 *   } } LazyColumn { items( countries.size, ) { idx -> Text( countries[idx].name, modifier =
 *   Modifier.padding(8.dp), ) } } }
 *
 * val supabase = createSupabaseClient( supabaseUrl = "https://bhhjdcvdcfrxczbudraf.supabase.co",
 * supabaseKey =
 * "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJoaGpkY3ZkY2ZyeGN6YnVkcmFmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjc4ODA4MjMsImV4cCI6MjA0MzQ1NjgyM30.teiPmTsMGNbXBx808uX7enVVLdgxqn4ftvSKjIgfCyQ")
 * { install(Postgrest) }
 *
 * @Serializable data class Country( val id: Int, val name: String, )
 */
