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
import com.android.periodpals.ui.authentication.SignUpScreen
import com.android.periodpals.ui.theme.PeriodPalsAppTheme
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
          // TODO: Uncomment the following line
          //          CountriesList()
          SignUpScreen()
        }
      }
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
