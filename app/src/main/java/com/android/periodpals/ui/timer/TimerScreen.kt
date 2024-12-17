package com.android.periodpals.ui.timer

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import com.android.periodpals.R
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.timer.COUNTDOWN_DURATION
import com.android.periodpals.model.timer.HOUR
import com.android.periodpals.model.timer.MINUTE
import com.android.periodpals.model.timer.SECOND
import com.android.periodpals.model.timer.TimerViewModel
import com.android.periodpals.resources.C.Tag.TimerScreen
import com.android.periodpals.resources.ComponentColor.getErrorButtonColors
import com.android.periodpals.resources.ComponentColor.getFilledPrimaryButtonColors
import com.android.periodpals.resources.ComponentColor.getInverseSurfaceButtonColors
import com.android.periodpals.ui.navigation.BottomNavigationMenu
import com.android.periodpals.ui.navigation.LIST_TOP_LEVEL_DESTINATION
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.TopAppBar
import com.android.periodpals.ui.theme.dimens
import kotlin.math.abs

private const val TAG = "TimerScreen"

/**
 * Composable function for the Timer screen.
 *
 * @param authenticationViewModel The ViewModel that contains the authentication logic and data.
 * @param timerViewModel The ViewModel that contains the timer's logic and data.
 * @param navigationActions The navigation actions to handle navigation events.
 */
@Composable
fun TimerScreen(
    authenticationViewModel: AuthenticationViewModel,
    timerViewModel: TimerViewModel,
    navigationActions: NavigationActions,
) {
  val authUserData by remember { mutableStateOf(authenticationViewModel.authUserData) }
  val activeTimer by remember { mutableStateOf(timerViewModel.activeTimer) }
  val remainingTime by timerViewModel.remainingTime.observeAsState(COUNTDOWN_DURATION)
  val isRunning by remember { mutableStateOf(timerViewModel.isRunning) }
  val userAverageTimer by remember { mutableStateOf(timerViewModel.userAverageTimer) }

  val context = LocalContext.current

  authenticationViewModel.loadAuthenticationUserData(
      onSuccess = {
        Log.d(TAG, "Successfully loaded user data")
        timerViewModel.loadActiveTimer(uid = authUserData.value?.uid ?: "")
      })

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag(TimerScreen.SCREEN),
      topBar = { TopAppBar(title = context.getString(R.string.timer_screen_title)) },
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
          text =
              activeTimer.value?.instructionText
                  ?: context.getString(R.string.displayed_text_start),
          modifier = Modifier.testTag(TimerScreen.DISPLAYED_TEXT),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
      )

      // Circle with time and progress bar
      TimerCircle(
          timeLeft = remainingTime,
          isRunning = isRunning,
          totalTime = COUNTDOWN_DURATION,
      )

      // Buttons (start, or reset and stop)
      Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium3)) {
        if (isRunning.value) {
          // Reset Button
          TimerButton(
              text = context.getString(R.string.timer_reset),
              modifier = Modifier.testTag(TimerScreen.RESET_BUTTON),
              onClick = { timerViewModel.resetTimer() },
              colors = getInverseSurfaceButtonColors(),
          )

          // Stop Button
          TimerButton(
              text = context.getString(R.string.timer_stop),
              modifier = Modifier.testTag(TimerScreen.STOP_BUTTON),
              onClick = { timerViewModel.stopTimer(uid = authUserData.value?.uid ?: "") },
              colors = getErrorButtonColors(),
          )
        } else {
          // Start Button
          TimerButton(
              text = context.getString(R.string.timer_start),
              modifier = Modifier.testTag(TimerScreen.START_BUTTON),
              onClick = { timerViewModel.startTimer() },
              colors = getFilledPrimaryButtonColors(),
          )
        }
      }

      // Useful tip
      UsefulTip(context)

      // Average time
      Text(
          text = "Your average time is ${formatedTime(userAverageTimer.value.toInt())}",
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}

/**
 * Formats the time in milliseconds to a string in the format "HH:MM:SS".
 *
 * @param timeToFormat Time in milliseconds to be formatted.
 * @return Formatted time string in the format "HH:MM:SS".
 */
@Composable
private fun formatedTime(timeToFormat: Int): String {
  val sign = if (timeToFormat < 0) "-" else ""
  val hours = abs(timeToFormat) / HOUR
  val minutes = abs((timeToFormat % HOUR)) / MINUTE
  val seconds = abs(timeToFormat) % MINUTE / SECOND
  return "%s%02d:%02d:%02d".format(sign, hours, minutes, seconds)
}

/**
 * Displays a circular timer with a progress indicator, time remaining, and an hourglass animation.
 *
 * @param timeLeft Remaining time in seconds.
 * @param isRunning Boolean indicating if the timer is active.
 * @param totalTime Total time in seconds for the timer.
 *
 * ### Components:
 * - **Background Circle**: Static gray circle as the timer's background.
 * - **Progress Circle**: Animated blue arc showing the elapsed progress.
 * - **Time Display**: Centered formatted time remaining.
 * - **Hourglass Animation**: Animated hourglass placed at the bottom center.
 */
@Composable
fun TimerCircle(timeLeft: Long, isRunning: MutableState<Boolean>, totalTime: Long) {
  val progress = (timeLeft.toFloat() / totalTime)

  Box(
      modifier = Modifier.size(MaterialTheme.dimens.timerSize).padding(MaterialTheme.dimens.small2),
      contentAlignment = Alignment.Center,
  ) {
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
        text = formatedTime(timeLeft.toInt()),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    )

    // Hourglass
    Box(
        modifier =
            Modifier.align(Alignment.BottomCenter).padding(bottom = MaterialTheme.dimens.small3),
    ) {
      HourglassAnimation(isRunning)
    }
  }
}

/**
 * Displays an animated hourglass that rotates when the timer is running.
 *
 * @param isRunning Boolean indicating if the timer is active. If true, the hourglass rotates
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
// TODO: fix and update the hourglass icon image based on the remaining time
@Composable
fun HourglassAnimation(isRunning: MutableState<Boolean>) {
  // Define the rotation angle that will either rotate or stay static
  val rotationAngle by
      animateFloatAsState(
          targetValue =
              // Rotate if timer is running, otherwise stay at 0
              if (isRunning.value) 360f else 0f,
          animationSpec =
              if (isRunning.value) {
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = 2000, // Rotate every 2 seconds
                            easing = LinearEasing))
              } else {
                // Static rotation, no animation if timer is stopped
                TweenSpec(durationMillis = 0) // No animation
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
 * Displays a button with a text label that triggers an action when clicked.
 *
 * @param text The text displayed on the button.
 * @param onClick The action to be executed when the button is clicked.
 * @param colors The color scheme for the button.
 * @param modifier The modifier to be applied to the button.
 */
@Composable
fun TimerButton(
    text: String,
    onClick: () -> Unit,
    colors: ButtonColors,
    modifier: Modifier = Modifier
) {
  Button(
      modifier = modifier.wrapContentSize(), enabled = true, onClick = onClick, colors = colors) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
        )
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
fun UsefulTip(context: Context) {
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
      text = context.getString(R.string.timer_useful_tip_text),
      modifier = Modifier.testTag(TimerScreen.USEFUL_TIP_TEXT),
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.bodyMedium,
  )

  HorizontalDivider(
      thickness = MaterialTheme.dimens.borderLine, color = MaterialTheme.colorScheme.outlineVariant)
}
