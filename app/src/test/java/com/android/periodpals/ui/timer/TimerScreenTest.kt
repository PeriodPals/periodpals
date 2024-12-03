package com.android.periodpals.ui.timer

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.MutableLiveData
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.timer.COUNTDOWN_DURATION
import com.android.periodpals.model.timer.TimerViewModel
import com.android.periodpals.model.user.AuthenticationUserData
import com.android.periodpals.resources.C.Tag.TimerScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TimerScreenTest {
  private lateinit var authenticationViewModel: AuthenticationViewModel
  private lateinit var timerViewModel: TimerViewModel
  private lateinit var navigationActions: NavigationActions
  private val remainingTime = MutableLiveData(COUNTDOWN_DURATION)
  private val userAverageTimer =
      mutableStateOf(4.0 * 60 * 60 * 1000 + 56.0 * 60 * 1000 + 37.4 * 1000)

  companion object {
    private const val UID = "uid"
    private const val EMAIL = "john.doe@example.com"
    private val authUserData = mutableStateOf(AuthenticationUserData(UID, EMAIL))
  }

  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun setUp() {
    authenticationViewModel = mock(AuthenticationViewModel::class.java)
    navigationActions = mock(NavigationActions::class.java)
    timerViewModel = mock(TimerViewModel::class.java)

    `when`(authenticationViewModel.authUserData).thenReturn(authUserData)
    `when`(navigationActions.currentRoute()).thenReturn(Route.TIMER)
    `when`(timerViewModel.remainingTime).thenReturn(remainingTime)
    `when`(timerViewModel.userAverageTimer).thenReturn(userAverageTimer)
  }

  @Test
  fun allComponentsAreDisplayed() {
    `when`(timerViewModel.timerRunning()).thenReturn(false)
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, navigationActions)
    }

    composeTestRule.onNodeWithTag(TimerScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals("Tampon Timer")
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.EDIT_BUTTON).assertIsNotDisplayed()

    composeTestRule.onNodeWithTag(TimerScreen.DISPLAYED_TEXT).performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TimerScreen.CIRCULAR_PROGRESS_INDICATOR)
        .performScrollTo()
        .assertIsDisplayed()
    composeTestRule.onNodeWithTag(TimerScreen.HOURGLASS).performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TimerScreen.START_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()
    composeTestRule.onNodeWithTag(TimerScreen.RESET_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TimerScreen.STOP_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TimerScreen.USEFUL_TIP).performScrollTo().assertIsDisplayed()
  }

  @Test
  fun loadUserDataSuccessRightAverage() {
    `when`(authenticationViewModel.loadAuthenticationUserData(any(), any())).thenAnswer {
      (it.arguments[0] as () -> Unit).invoke()
    }
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, navigationActions)
    }

    verify(authenticationViewModel).loadAuthenticationUserData(any(), any())
    verify(timerViewModel).computeAverageTime(eq(UID), any(), any())
    assertEquals(4.0 * 60 * 60 * 1000 + 56.0 * 60 * 1000 + 37.4 * 1000, userAverageTimer.value, 0.0)
  }

  @Test
  fun loadUserDataFailure() {
    `when`(authenticationViewModel.loadAuthenticationUserData(any(), any())).thenAnswer {
      (it.arguments[1] as (Exception) -> Unit).invoke(Exception("Test Exception"))
    }
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, navigationActions)
    }

    verify(authenticationViewModel).loadAuthenticationUserData(any(), any())
    verify(timerViewModel).computeAverageTime(eq("uid"), any(), any())
  }

  @Test
  fun nullUserData() {
    val authUserData = mutableStateOf(AuthenticationUserData("", EMAIL))
    `when`(authenticationViewModel.authUserData).thenReturn(authUserData)
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, navigationActions)
    }

    verify(authenticationViewModel).loadAuthenticationUserData(any(), any())
    verify(timerViewModel).computeAverageTime(eq(""), any(), any())
  }

  @Test
  fun startButtonStartsTimer() {
    `when`(timerViewModel.timerRunning()).thenReturn(false)
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.START_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    verify(timerViewModel).startTimer(any(), any())
  }

  @Test
  fun startButtonDoesNotStartTimerManagerSuccess() {
    `when`(timerViewModel.timerRunning()).thenReturn(false)
    `when`(timerViewModel.startTimer(any(), any())).thenAnswer {
      (it.arguments[1] as (Exception) -> Unit).invoke(Exception("TimerManager failure"))
    }
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.START_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    verify(timerViewModel).startTimer(any(), any())
    assertEquals(false, timerViewModel.timerRunning())
  }

  @Test
  fun startButtonFailsWithTimerViewModelFailure() {
    `when`(timerViewModel.timerRunning()).thenReturn(false)
    `when`(timerViewModel.startTimer(any(), any()))
        .thenThrow(RuntimeException("TimerViewModel failure"))
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.START_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()

    verify(timerViewModel, never()).startTimer(any(), any())
    assertEquals(false, timerViewModel.timerRunning())
  }

  @Test
  fun resetButtonResetsTimer() {
    `when`(timerViewModel.timerRunning()).thenReturn(true)
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.RESET_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    verify(timerViewModel).resetTimer(any(), any())
  }

  @Test
  fun resetButtonDoesNotResetTimerManagerSuccess() {
    `when`(timerViewModel.timerRunning()).thenReturn(true)
    `when`(timerViewModel.resetTimer(any(), any())).thenAnswer {
      (it.arguments[1] as (Exception) -> Unit).invoke(Exception("TimerManager failure"))
    }
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.RESET_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    verify(timerViewModel).resetTimer(any(), any())
    assertEquals(true, timerViewModel.timerRunning())
  }

  @Test
  fun resetButtonFailsWithTimerViewModelFailure() {
    `when`(timerViewModel.timerRunning()).thenReturn(true)
    `when`(timerViewModel.resetTimer(any(), any()))
        .thenThrow(RuntimeException("TimerViewModel failure"))
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.RESET_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()

    verify(timerViewModel, never()).resetTimer(any(), any())
    assertEquals(true, timerViewModel.timerRunning())
  }

  @Test
  fun stopButtonStopsTimer() {
    `when`(timerViewModel.timerRunning()).thenReturn(true)
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.STOP_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    verify(timerViewModel).stopTimer(eq(UID), any(), any())
  }

  @Test
  fun stopButtonDoesNotStopTimerManagerSuccess() {
    `when`(timerViewModel.timerRunning()).thenReturn(true)
    `when`(timerViewModel.stopTimer(any(), any(), any())).thenAnswer {
      (it.arguments[2] as (Exception) -> Unit).invoke(Exception("TimerManager failure"))
    }
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.STOP_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .performClick()

    verify(timerViewModel).stopTimer(eq(UID), any(), any())
    assertEquals(true, timerViewModel.timerRunning())
  }

  @Test
  fun stopButtonFailsWithTimerViewModelFailure() {
    `when`(timerViewModel.timerRunning()).thenReturn(true)
    `when`(timerViewModel.stopTimer(any(), any(), any()))
        .thenThrow(RuntimeException("TimerViewModel failure"))
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.STOP_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertHasClickAction()

    verify(timerViewModel, never()).stopTimer(any(), any(), any())
    assertEquals(true, timerViewModel.timerRunning())
  }
}
