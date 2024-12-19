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
import com.android.periodpals.R
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.timer.COUNTDOWN_DURATION
import com.android.periodpals.model.timer.Timer
import com.android.periodpals.model.timer.TimerViewModel
import com.android.periodpals.model.user.AuthenticationUserData
import com.android.periodpals.resources.C.Tag.TimerScreen
import com.android.periodpals.resources.C.Tag.TopAppBar
import com.android.periodpals.services.NetworkChangeListener
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Route
import io.github.kakaocup.kakao.common.utilities.getResourceString
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TimerScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  @Mock private lateinit var authenticationViewModel: AuthenticationViewModel
  @Mock private lateinit var timerViewModel: TimerViewModel
  @Mock private lateinit var navigationActions: NavigationActions
  @Mock private lateinit var networkChangeListener: NetworkChangeListener
  private val authUserData = mutableStateOf(AuthenticationUserData(UID, EMAIL))
  private val activeTimer = mutableStateOf<Timer?>(ACTIVE_TIMER)
  private val isRunning = mutableStateOf(false)
  private val remainingTime = MutableLiveData(COUNTDOWN_DURATION)
  private val userAverageTimer =
      mutableStateOf(4.0 * 60 * 60 * 1000 + 56.0 * 60 * 1000 + 37.4 * 1000)

  companion object {
    private const val UID = "uid"
    private const val EMAIL = "john.doe@example.com"
    private const val ACTIVE_TIMER_TIME = -1L
    private const val INSTRUCTION_TEXT = "Timer 1"
    private val ACTIVE_TIMER = Timer(time = ACTIVE_TIMER_TIME, instructionText = INSTRUCTION_TEXT)
  }

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    `when`(authenticationViewModel.authUserData).thenReturn(authUserData)
    `when`(timerViewModel.activeTimer).thenReturn(activeTimer)
    `when`(timerViewModel.isRunning).thenReturn(isRunning)
    `when`(timerViewModel.remainingTime).thenReturn(remainingTime)
    `when`(timerViewModel.userAverageTimer).thenReturn(userAverageTimer)
    `when`(navigationActions.currentRoute()).thenReturn(Route.TIMER)
    `when`(networkChangeListener.isNetworkAvailable).thenReturn(MutableStateFlow(false))
  }

  @Test
  fun allComponentsAreDisplayed() {
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, networkChangeListener, navigationActions)
    }

    composeTestRule.onNodeWithTag(TimerScreen.SCREEN).assertIsDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.TOP_BAR).assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TopAppBar.TITLE_TEXT)
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.timer_screen_title))
    composeTestRule.onNodeWithTag(TopAppBar.GO_BACK_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.SETTINGS_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TopAppBar.CHAT_BUTTON).assertIsNotDisplayed()
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
        .assertTextEquals(getResourceString(R.string.timer_start))
        .assertHasClickAction()
    composeTestRule.onNodeWithTag(TimerScreen.RESET_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TimerScreen.STOP_BUTTON).assertIsNotDisplayed()
    composeTestRule.onNodeWithTag(TimerScreen.USEFUL_TIP).performScrollTo().assertIsDisplayed()
    composeTestRule
        .onNodeWithTag(TimerScreen.USEFUL_TIP_TEXT)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.timer_useful_tip_text))
  }

  @Test
  fun loadUserDataSuccessRightAverage() {
    `when`(authenticationViewModel.loadAuthenticationUserData(any(), any())).thenAnswer {
      (it.arguments[0] as () -> Unit).invoke()
    }
    `when`(timerViewModel.loadActiveTimer(eq(UID), any(), any())).thenAnswer {
      (it.arguments[1] as () -> Unit).invoke()
    }

    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel,networkChangeListener,  navigationActions)
    }

    verify(authenticationViewModel).loadAuthenticationUserData(any(), any())
    verify(timerViewModel).loadActiveTimer(eq(UID), any(), any())
    assertEquals(ACTIVE_TIMER, timerViewModel.activeTimer.value)
    assertEquals(
        4.0 * 60 * 60 * 1000 + 56.0 * 60 * 1000 + 37.4 * 1000,
        timerViewModel.userAverageTimer.value,
        0.0)
  }

  @Test
  fun loadUserDataFailure() {
    `when`(authenticationViewModel.loadAuthenticationUserData(any(), any())).thenAnswer {
      (it.arguments[1] as (Exception) -> Unit).invoke(Exception("Test Exception"))
    }
    `when`(timerViewModel.activeTimer).thenReturn(mutableStateOf(null))
    `when`(timerViewModel.userAverageTimer).thenReturn(mutableStateOf(0.0))

    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, networkChangeListener,  navigationActions)
    }

    verify(authenticationViewModel).loadAuthenticationUserData(any(), any())
    verify(timerViewModel, never()).loadActiveTimer(eq(UID), any(), any())
    assertEquals(null, timerViewModel.activeTimer.value)
    assertEquals(0.0, timerViewModel.userAverageTimer.value, 0.0)
  }

  @Test
  fun nullUserData() {
    val authUserData = mutableStateOf<AuthenticationUserData?>(null)
    `when`(authenticationViewModel.authUserData).thenReturn(authUserData)
    `when`(timerViewModel.activeTimer).thenReturn(mutableStateOf(null))
    `when`(timerViewModel.userAverageTimer).thenReturn(mutableStateOf(0.0))

    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, networkChangeListener,  navigationActions)
    }

    verify(authenticationViewModel).loadAuthenticationUserData(any(), any())
    verify(timerViewModel, never()).loadActiveTimer(any(), any(), any())
    assertEquals(null, timerViewModel.activeTimer.value)
    assertEquals(0.0, timerViewModel.userAverageTimer.value, 0.0)
  }

  @Test
  fun startButtonStartsTimer() {
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, networkChangeListener, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.START_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.timer_start))
        .performClick()
    verify(timerViewModel).startTimer(any(), any())
  }

  @Test
  fun startButtonDoesNotStartTimerManagerSuccess() {
    `when`(timerViewModel.startTimer(any(), any())).thenAnswer {
      (it.arguments[1] as (Exception) -> Unit).invoke(Exception("TimerManager failure"))
    }
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, networkChangeListener, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.START_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.timer_start))
        .performClick()
    verify(timerViewModel).startTimer(any(), any())
    assertEquals(false, timerViewModel.isRunning.value)
  }

  @Test
  fun startButtonFailsWithTimerViewModelFailure() {
    `when`(timerViewModel.startTimer(any(), any()))
        .thenThrow(RuntimeException("TimerViewModel failure"))
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, networkChangeListener, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.START_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.timer_start))
        .assertHasClickAction()
    verify(timerViewModel, never()).startTimer(any(), any())
    assertEquals(false, isRunning.value)
  }

  @Test
  fun resetButtonResetsTimer() {
    isRunning.value = true
    `when`(timerViewModel.resetTimer(any(), any())).thenAnswer {
      isRunning.value = false
      null
    }
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, networkChangeListener, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.RESET_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.timer_reset))
        .performClick()
    verify(timerViewModel).resetTimer(any(), any())
    assertEquals(false, isRunning.value)
  }

  @Test
  fun resetButtonDoesNotResetTimerManagerSuccess() {
    isRunning.value = true
    `when`(timerViewModel.resetTimer(any(), any())).thenAnswer {
      (it.arguments[1] as (Exception) -> Unit).invoke(Exception("TimerManager failure"))
    }
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, networkChangeListener, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.RESET_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.timer_reset))
        .performClick()
    verify(timerViewModel).resetTimer(any(), any())
    assertEquals(true, isRunning.value)
  }

  @Test
  fun resetButtonFailsWithTimerViewModelFailure() {
    isRunning.value = true
    `when`(timerViewModel.resetTimer(any(), any()))
        .thenThrow(RuntimeException("TimerViewModel failure"))
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, networkChangeListener, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.RESET_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.timer_reset))
        .assertHasClickAction()
    verify(timerViewModel, never()).resetTimer(any(), any())
    assertEquals(true, isRunning.value)
  }

  @Test
  fun stopButtonStopsTimer() {
    isRunning.value = true
    `when`(timerViewModel.stopTimer(any(), any(), any())).thenAnswer {
      isRunning.value = false
      null
    }
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, networkChangeListener, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.STOP_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.timer_stop))
        .performClick()

    verify(timerViewModel).stopTimer(eq(UID), any(), any())
    assertEquals(false, isRunning.value)
  }

  @Test
  fun stopButtonDoesNotStopTimerManagerSuccess() {
    isRunning.value = true
    `when`(timerViewModel.stopTimer(any(), any(), any())).thenAnswer {
      (it.arguments[2] as (Exception) -> Unit).invoke(Exception("TimerManager failure"))
    }
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, networkChangeListener, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.STOP_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.timer_stop))
        .performClick()
    verify(timerViewModel).stopTimer(eq(UID), any(), any())
    assertEquals(true, isRunning.value)
  }

  @Test
  fun stopButtonFailsWithTimerViewModelFailure() {
    isRunning.value = true
    `when`(timerViewModel.stopTimer(any(), any(), any()))
        .thenThrow(RuntimeException("TimerViewModel failure"))
    composeTestRule.setContent {
      TimerScreen(authenticationViewModel, timerViewModel, networkChangeListener, navigationActions)
    }

    composeTestRule
        .onNodeWithTag(TimerScreen.STOP_BUTTON)
        .performScrollTo()
        .assertIsDisplayed()
        .assertTextEquals(getResourceString(R.string.timer_stop))
        .assertHasClickAction()
    verify(timerViewModel, never()).stopTimer(any(), any(), any())
    assertEquals(true, isRunning.value)
  }
}
