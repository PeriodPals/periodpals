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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

// Displayed text
private const val DISPLAYED_TEXT_ONE =
    "Start your tampon timer.\n" + "You’ll be reminded to change it !"
private const val DISPLAYED_TEXT_TWO =
    "You’ve got this. Stay strong !!\n" + "Don’t forget to stay hydrated !"
// TODO implement the logic about the time to display
private const val DISPLAYED_TEXT_THREE =
    "It has been more than" + 3 + "hours.\n" + "It will soon be time to change it !"
private const val DISPLAYED_TEXT_FOUR = "It’s about time to change it.\n" + "Don’t wait too long !"
private const val DISPLAYED_TEXT_FIVE =
    "It has been a long time.\n" + "Take a break and go remove it !"
private const val DISPLAYED_TEXT_SIX =
    "It has been a really long time.\n" + "Hurry up and go remove it !"
private const val DISPLAYED_TEXT_SEVEN = "It has been too long.\n" + "Please hurry, go remove it !"

private const val ONE_HOUR = 3600

/** TODO: Placeholder Screen, waiting for implementation */
@Composable
fun TimerScreen(
    navigationActions: NavigationActions,
) {

  // TODO: Retrieve these values from the ViewModel
  val timeLeft = ONE_HOUR * 6
  val isTimerRunning = false

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
      val hours = timeLeft / ONE_HOUR
      val minutes = (timeLeft % ONE_HOUR) / 60
      val seconds = timeLeft % 60
      val timeFormatted = "%02d:%02d:%02d".format(hours, minutes, seconds)

      // Displayed text
      // TODO: Implement this logic in the ViewModel to refresh it when needed
      var displayedText = DISPLAYED_TEXT_ONE

      Text(
          text = correct_displayedText(timeLeft, displayedText),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
      )

      // TODO: Create an hourglass that rotates every two seconds as time passes
      // TODO: Create a circle with a progress bar indicating the passing time

      Text(
          text = timeFormatted,
          textAlign = TextAlign.Center,
          style = TextStyle(fontSize = 40.sp),
          modifier = Modifier.fillMaxWidth())

      Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium3)) {

        // Start/Stop Button
        Button(
            onClick = {
              if (isTimerRunning) {
                // TODO: stop the timer
              } else {
                // TODO: start the timer
              }
            },
            colors =
                ButtonDefaults.buttonColors(
                    // TODO: Adjust the padding
                    containerColor =
                        if (isTimerRunning) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary,
                    contentColor =
                        if (isTimerRunning) MaterialTheme.colorScheme.onError
                        else MaterialTheme.colorScheme.onPrimary)) {
              // TODO: Adjust the padding and typography
              Text(
                  text = if (isTimerRunning) "STOP" else "START",
                  style = MaterialTheme.typography.bodyLarge)
            }
      }
    }
  }
}

@Composable
private fun correct_displayedText(timeLeft: Int, displayedText: String): String {
  var displayedText1 = displayedText
  if (timeLeft > ONE_HOUR * 4.5) {
    displayedText1 = DISPLAYED_TEXT_ONE
  } else if (timeLeft > ONE_HOUR * 3) {
    displayedText1 = DISPLAYED_TEXT_TWO
  } else if (timeLeft > ONE_HOUR * 2) {
    displayedText1 = DISPLAYED_TEXT_THREE
  } else if (timeLeft > ONE_HOUR * 1.5) {
    displayedText1 = DISPLAYED_TEXT_FOUR
  } else if (timeLeft > ONE_HOUR) {
    displayedText1 = DISPLAYED_TEXT_FIVE
  } else if (timeLeft > ONE_HOUR * 0.5) {
    displayedText1 = DISPLAYED_TEXT_SIX
  } else if (timeLeft > 0) {
    displayedText1 = DISPLAYED_TEXT_SEVEN
  } else {
    displayedText1 = DISPLAYED_TEXT_ONE
  }
  return displayedText1
}
