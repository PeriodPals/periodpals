package com.android.periodpals.ui.timer

import androidx.compose.animation.core.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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

      // Displayed text
      Text(
          text = correct_displayedText(isTimerRunning),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
      )

      // Circle with time and progress bar
      TimerCircle(timeLeft = timeLeft, isTimerRunning)

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

//TODO: Adjust the padding, typography and colors
@Composable
fun TimerCircle(timeLeft: Int, isRunning: Boolean) {
  val totalTime = ONE_HOUR * 6 // Total time (6 hours in seconds)
  val progress = 1f - (timeLeft.toFloat() / totalTime)

  Box(modifier = Modifier.size(200.dp).padding(16.dp), contentAlignment = Alignment.Center) {
    // Background circle (gray)
    Canvas(modifier = Modifier.fillMaxSize()) {
      drawCircle(color = Color.Gray.copy(alpha = 0.2f), radius = size.minDimension / 2)
    }

    // Progress circle (blue)
    Canvas(modifier = Modifier.fillMaxSize()) {
      drawArc(
          color = Color.Blue,
          startAngle = -90f,
          sweepAngle = progress * 360f,
          useCenter = false,
          size = size.copy(size.width * 0.9f, size.height * 0.9f),
          style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx()))
    }

    // Time text inside the circle
    val hours = timeLeft / ONE_HOUR
    val minutes = (timeLeft % ONE_HOUR) / 60
    val seconds = timeLeft % 60
    val timeFormatted = "%02d:%02d:%02d".format(hours, minutes, seconds)

    Text(
        text = timeFormatted,
        textAlign = TextAlign.Center,
        style = TextStyle(fontSize = 40.sp),
        color = Color.Black)

    // Positioning the hourglass icon below the time text
    Box(
        modifier =
            Modifier.align(Alignment.BottomCenter)
                .padding(bottom = 20.dp) // Add some space to ensure it's not overlapping
        ) {
          HourglassAnimation(isRunning)
        }
  }
}

@Composable
fun HourglassAnimation(isTimerRunning: Boolean) {
  // Define the rotation angle that will either rotate or stay static
  val rotationAngle by
      animateFloatAsState(
          targetValue =
              if (isTimerRunning) 360f else 0f, // Rotate if timer is running, otherwise stay at 0
          animationSpec =
              if (isTimerRunning) {
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = 2000, // Rotate every 2 seconds
                            easing = LinearEasing))
              } else {
                // Static rotation, no animation if timer is stopped
                TweenSpec<Float>(durationMillis = 0) // No animation
              },
          label = "")

  // Always show the hourglass
  Box(
      modifier =
          Modifier.size(50.dp) // Set the size of the hourglass
              .graphicsLayer(rotationZ = rotationAngle), // Apply rotation angle to the hourglass
      contentAlignment = Alignment.Center // Center the hourglass in the Box
      ) {
        Icon(
            imageVector = Icons.Filled.HourglassEmpty, // Hourglass icon
            contentDescription = "Hourglass",
            modifier = Modifier.fillMaxSize(),
            tint = Color.Black)
      }
}

// TODO: Implement this logic in the ViewModel to refresh it when needed
@Composable
private fun correct_displayedText(isTimerRunning: Boolean): String {
  return if (isTimerRunning) DISPLAYED_TEXT_TWO else DISPLAYED_TEXT_ONE
}
