package com.android.periodpals.ui.timer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.android.periodpals.resources.C.Tag.TimerScreen
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens

private const val SCREEN_TITLE = "Tampon Timer"

/** TODO: Placeholder Screen, waiting for implementation */
@Composable
fun TimerScreen(
    navigationActions: NavigationActions,
) {

  val timeLeft = 3600
  val isTimerRunning = true

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(TimerScreen.SCREEN),
      topBar = { TopAppBar(title = SCREEN_TITLE) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
  ) { paddingValues ->
    Column(
        modifier =
            Modifier.fillMaxSize()
                .padding(paddingValues)
                .padding(
                    horizontal = MaterialTheme.dimens.medium3,
                    vertical = MaterialTheme.dimens.small3,
                )
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(MaterialTheme.dimens.small2, Alignment.CenterVertically),
    ) {
      // TODO: delete when implementing the screen
      Text("Timer Screen", modifier = Modifier.fillMaxSize().testTag(TimerScreen.TIMER_TEXT))

      // Display the remaining time in a readable format (HH:mm:ss)
      val hours = timeLeft / 3600
      val minutes = (timeLeft % 3600) / 60
      val seconds = timeLeft % 60
      val timeFormatted = "%02d:%02d:%02d".format(hours, minutes, seconds)

      // Instruction text
      Text(
          text = "Start your tampon timer.\n" + "You’ll be reminded to change it !",
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
      )

      Text(
          text = timeFormatted,
          style = TextStyle(fontSize = 40.sp),
          modifier = Modifier.fillMaxWidth())

      Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium3)) {

        // Start/Pause Button
        Button(
            onClick = {
              if (isTimerRunning) {
                // TODO: pause the timer
              } else {
                // TODO: start the timer
              }
            }) {
              Text(if (isTimerRunning) "Pause" else "Start")
            }
      }
    }
  }
}
