package com.android.periodpals.ui.timer

import androidx.compose.animation.core.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
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
    "You’ve got this. Stay strong !\n" + "Don’t forget to stay hydrated !"
// TODO implement the logic about the time to display
private const val DISPLAYED_TEXT_THREE =
    "It has been more than" + 3 + "hours.\n" + "It will soon be time to change it !"
private const val DISPLAYED_TEXT_FOUR = "It’s about time to change it.\n" + "Don’t wait too long !"
private const val DISPLAYED_TEXT_FIVE =
    "It has been a long time.\n" + "Take a break and go remove it !"
private const val DISPLAYED_TEXT_SIX =
    "It has been a really long time.\n" + "Hurry up and go remove it !"
private const val DISPLAYED_TEXT_SEVEN = "It has been too long.\n" + "Please hurry, go remove it !"

// Useful tip text
private const val usefulTipText =
    "Leaving a tampon in for over 3-4 hours too often can cause irritation and infections. Regular changes are essential to avoid risks. Choosing cotton or natural tampons helps reduce irritation and improve hygiene."

private const val ONE_HOUR = 3600

/**
 * Composable function for the Timer screen.
 *
 * @param navigationActions The navigation actions to handle navigation events.
 */
@Composable
fun TimerScreen(
    navigationActions: NavigationActions,
) {

  // TODO: Retrieve these values from the ViewModel
  var timeLeft by remember { mutableIntStateOf(ONE_HOUR * 6) }
  var averageTime by remember { mutableIntStateOf(ONE_HOUR * 6) }
  var isTimerRunning by remember { mutableStateOf(false) }
  val totalTime by remember { mutableIntStateOf(ONE_HOUR * 6) }

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(TimerScreen.SCREEN),
      topBar = { TopAppBar(title = SCREEN_TITLE) },
      bottomBar = {
        BottomNavigationMenu(
            onTabSelect = { route -> navigationActions.navigateTo(route) },
            tabList = LIST_TOP_LEVEL_DESTINATION,
            selectedItem = navigationActions.currentRoute())
      },
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.onSurface,
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

      // Displayed text
      Text(
          text = correct_displayedText(isTimerRunning),
          modifier = Modifier.testTag(TimerScreen.DISPLAYED_TEXT),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
      )

      // Circle with time and progress bar
      TimerCircle(timeLeft = timeLeft, isTimerRunning, totalTime)

      Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium3)) {

        // Start/Stop Button
        Button(
            modifier = Modifier.wrapContentSize().testTag(TimerScreen.START_STOP_BUTTON),
            enabled = true,
            onClick = {
              if (isTimerRunning) {
                // TODO: stop the timer
                isTimerRunning = false
              } else {
                // TODO: start the timer
                isTimerRunning = true
              }
            },
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        if (isTimerRunning) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary,
                    contentColor =
                        if (isTimerRunning) MaterialTheme.colorScheme.onError
                        else MaterialTheme.colorScheme.onPrimary)) {
              Text(
                  text = if (isTimerRunning) "STOP" else "START",
                  textAlign = TextAlign.Center,
                  style = MaterialTheme.typography.headlineSmall)
            }
      }

      // Useful tip
      UsefulTip()

      // Average time
      Text(
          text = "Your average time is ${formatedTime(averageTime)}",
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium)
    }
  }
}

/**
 * Determines the appropriate text to display based on the timer's running state.
 *
 * For now, it only does this:
 *
 * @param isTimerRunning A Boolean indicating whether the timer is currently running.
 *     - `true`: The timer is running.
 *     - `false`: The timer is stopped.
 *
 * @return A String containing the corresponding message:
 *     - Returns `DISPLAYED_TEXT_TWO` if the timer is running.
 *     - Returns `DISPLAYED_TEXT_ONE` if the timer is not running.
 */
// TODO: Implement this logic in the ViewModel to refresh it when needed
@Composable
private fun correct_displayedText(isTimerRunning: Boolean): String {
  return if (isTimerRunning) DISPLAYED_TEXT_TWO else DISPLAYED_TEXT_ONE
}

/**
 * Formats a given time in seconds into a human-readable string in the format HH:MM:SS.
 *
 * @param timeToFormat An integer representing the time in seconds to format.
 * @return A formatted string in the form of "HH:MM:SS":
 *     - `HH`: Hours (padded to two digits).
 *     - `MM`: Minutes (padded to two digits).
 *     - `SS`: Seconds (padded to two digits).
 */
@Composable
private fun formatedTime(timeToFormat: Int): String {
  val hours = timeToFormat / ONE_HOUR
  val minutes = (timeToFormat % ONE_HOUR) / 60
  val seconds = timeToFormat % 60
  val timeFormatted = "%02d:%02d:%02d".format(hours, minutes, seconds)
  return timeFormatted
}

/**
 * Displays a circular timer with a progress indicator, time remaining, and an hourglass animation.
 *
 * @param timeLeft Remaining time in seconds.
 * @param isTimerRunning Boolean indicating if the timer is active.
 * @param totalTime Total time in seconds for the timer.
 *
 * ### Components:
 * - **Background Circle**: Static gray circle as the timer's background.
 * - **Progress Circle**: Animated blue arc showing the elapsed progress.
 * - **Time Display**: Centered formatted time remaining.
 * - **Hourglass Animation**: Animated hourglass placed at the bottom center.
 */
@Composable
fun TimerCircle(timeLeft: Int, isTimerRunning: Boolean, totalTime: Int) {
  val progress = (timeLeft.toFloat() / totalTime)

  Box(
      modifier = Modifier.size(MaterialTheme.dimens.timerSize).padding(MaterialTheme.dimens.small2),
      contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { progress },
            modifier =
                Modifier.fillMaxSize()
                    .testTag(TimerScreen.CIRCULAR_PROGRESS_INDICATOR)
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = MaterialTheme.dimens.small2,
            trackColor = MaterialTheme.colorScheme.primaryContainer,
            strokeCap = StrokeCap.Round,
        )

        // Time displayed
        Text(
            text = formatedTime(timeLeft),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer)

        // Hourglass
        Box(
            modifier =
                Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = MaterialTheme.dimens.small3)) {
              HourglassAnimation(isTimerRunning)
            }
      }
}

/**
 * Displays an animated hourglass that rotates when the timer is running.
 *
 * @param isTimerRunning Boolean indicating if the timer is active. If true, the hourglass rotates
 *   continuously; otherwise, it remains static.
 *
 * ### Behavior:
 * - **Rotation Animation**: The hourglass rotates 360 degrees every 2 seconds while the timer is
 *   running.
 * - **Static Display**: The hourglass remains stationary when the timer is stopped.
 *
 * ### Components:
 * - **Hourglass Icon**: A centered hourglass icon that rotates based on the timer's state.
 * - **Rotation Animation**: Applied to the icon using `animateFloatAsState`.
 */
// TODO: update the hourglass icon image based on the remaining time
@Composable
fun HourglassAnimation(isTimerRunning: Boolean) {
  // Define the rotation angle that will either rotate or stay static
  val rotationAngle by
      animateFloatAsState(
          targetValue =
              // Rotate if timer is running, otherwise stay at 0
              if (isTimerRunning) 360f else 0f,
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
          label = "hourglassRotation")

  // Hourglass displayed with rotation
  Box(
      modifier = Modifier.size(MaterialTheme.dimens.iconButtonSize),
      contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.Filled.HourglassEmpty,
            contentDescription = "Hourglass",
            modifier = Modifier.fillMaxSize().testTag(TimerScreen.HOURGLASS).rotate(rotationAngle),
            tint = MaterialTheme.colorScheme.onPrimaryContainer)
      }
}

/**
 * Displays a "Useful Tip" section with an icon, title, and description text, separated by
 * horizontal dividers.
 *
 * ### Components:
 * - **Icon and Title**: A lightbulb icon and "Useful Tip" text.
 * - **Dividers**: Horizontal lines for visual separation.
 * - **Description**: Centered text containing the tip.
 *
 * ### Tags:
 * - Icon: `TimerScreen.USEFUL_TIP`
 */
@Composable
fun UsefulTip() {
  Row(
      modifier = Modifier.testTag(TimerScreen.USEFUL_TIP),
      horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small2)) {
        Icon(
            imageVector = Icons.Filled.Lightbulb,
            contentDescription = "Lightbulb Icon",
            tint = Color(0XFFFBBC05),
            modifier = Modifier.size(MaterialTheme.dimens.iconSize),
        )
        Text(
            text = "Useful Tip",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
        )
      }

  HorizontalDivider(
      thickness = MaterialTheme.dimens.borderLine, color = MaterialTheme.colorScheme.outlineVariant)

  Text(
      text = usefulTipText,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.bodyMedium,
  )

  HorizontalDivider(
      thickness = MaterialTheme.dimens.borderLine, color = MaterialTheme.colorScheme.outlineVariant)
}
