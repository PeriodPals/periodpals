package com.android.periodpals.model.timer

import com.android.periodpals.MainCoroutineRule
import java.util.Date
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.capture
import org.mockito.kotlin.eq

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {
  @Mock private lateinit var timerRepository: TimerRepository
  @Mock private lateinit var timerManager: TimerManager
  private lateinit var timerViewModel: TimerViewModel

  @Captor private lateinit var onSuccessCaptor: ArgumentCaptor<() -> Unit>
  @Captor private lateinit var onFailureCaptor: ArgumentCaptor<(Exception) -> Unit>
  @Captor private lateinit var onSuccessCaptorTimer: ArgumentCaptor<(Timer?) -> Unit>
  @Captor private lateinit var onSuccessCaptorLong: ArgumentCaptor<(Long) -> Unit>
  @Captor private lateinit var onSuccessCaptorList: ArgumentCaptor<(List<Timer>) -> Unit>

  companion object {
    private const val UID = "testUser"
    private const val TIME = 1000L

    private const val FIRST_REMINDER = 3 * 60 * 60 * 1000
    private const val REMINDERS_INTERVAL = 30 * 60 * 1000
  }

  @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    timerViewModel = TimerViewModel(timerRepository, timerManager)
  }

  @Test
  fun timeTaskRunWhenTimerCounting() = runTest {
    `when`(timerManager.timerCounting()).thenReturn(true)
    `when`(timerManager.startTime()).thenReturn(Date())

    timerViewModel.TimeTask().run()

    assertEquals(COUNTDOWN_DURATION, timerViewModel.remainingTime.value)
    verify(timerManager).startTime()
  }

  @Test
  fun timeTaskRunWhenTimerNotCounting() = runTest {
    `when`(timerManager.timerCounting()).thenReturn(false)

    timerViewModel.TimeTask().run()

    assertEquals(COUNTDOWN_DURATION, timerViewModel.remainingTime.value)
  }

  @Test
  fun loadActiveTimerSuccess() = runTest {
    val timer = Timer(time = ACTIVE_TIMER_TIME, instructionText = "Timer 1")

    doAnswer { it.getArgument<(Timer?) -> Unit>(1)(timer) }
        .`when`(timerRepository)
        .getActiveTimer(eq(UID), capture(onSuccessCaptorTimer), capture(onFailureCaptor))

    timerViewModel.loadActiveTimer(
        uid = UID, onSuccess = {}, onFailure = { fail("Should not call `onFailure`") })

    // Verify and capture the argument
    verify(timerRepository)
        .getActiveTimer(eq(UID), capture(onSuccessCaptorTimer), capture(onFailureCaptor))
    assertEquals(timer, timerViewModel.activeTimer.value)
  }

  @Test
  fun loadActiveTimerNoneActiveSuccess() = runTest {
    doAnswer { it.getArgument<(Timer?) -> Unit>(1)(null) }
        .`when`(timerRepository)
        .getActiveTimer(eq(UID), capture(onSuccessCaptorTimer), capture(onFailureCaptor))

    timerViewModel.loadActiveTimer(
        uid = UID, onSuccess = {}, onFailure = { fail("Should not call `onFailure`") })

    // Verify and capture the argument
    verify(timerRepository)
        .getActiveTimer(eq(UID), capture(onSuccessCaptorTimer), capture(onFailureCaptor))
    assertEquals(null, timerViewModel.activeTimer.value)
  }

  @Test
  fun loadActiveTimerFailure() = runTest {
    val exception = Exception("Failed to load active timer")

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .`when`(timerRepository)
        .getActiveTimer(eq(UID), capture(onSuccessCaptorTimer), capture(onFailureCaptor))

    timerViewModel.loadActiveTimer(
        uid = UID, onSuccess = { fail("Should not call `onSuccess`") }, onFailure = {})

    verify(timerRepository)
        .getActiveTimer(eq(UID), capture(onSuccessCaptorTimer), capture(onFailureCaptor))
    onFailureCaptor.value.invoke(exception)

    assertEquals(null, timerViewModel.activeTimer.value)
  }

  @Test
  fun loadActiveTimerWithNullUID() = runTest {
    val exception = Exception("UID is null")

    doAnswer { it.getArgument<(Exception) -> Unit>(2)(exception) }
        .`when`(timerRepository)
        .getActiveTimer(
            eq(null.toString()), capture(onSuccessCaptorTimer), capture(onFailureCaptor))

    timerViewModel.loadActiveTimer(
        uid = null.toString(), onSuccess = { fail("Should not call `onSuccess`") }, onFailure = {})

    verify(timerRepository)
        .getActiveTimer(
            eq(null.toString()), capture(onSuccessCaptorTimer), capture(onFailureCaptor))
    onFailureCaptor.value.invoke(exception)

    assertEquals(null, timerViewModel.activeTimer.value)
  }

  @Test
  fun startTimerSuccess() = runTest {
    doNothing()
        .`when`(timerManager)
        .startTimerAction(capture(onSuccessCaptor), capture(onFailureCaptor))

    timerViewModel.startTimer(onSuccess = {}, onFailure = { fail("Should not call `onFailure`") })

    verify(timerManager).startTimerAction(capture(onSuccessCaptor), capture(onFailureCaptor))
    onSuccessCaptor.value.invoke()
  }

  @Test
  fun startTimerFailure() = runTest {
    val exception = Exception("Failed to start timer")

    doAnswer { it.getArgument<(Exception) -> Unit>(1)(Exception("Failed to start timer")) }
        .`when`(timerManager)
        .startTimerAction(capture(onSuccessCaptor), capture(onFailureCaptor))

    timerViewModel.startTimer(onSuccess = { fail("Should not call `onSuccess`") }, onFailure = {})

    verify(timerManager).startTimerAction(capture(onSuccessCaptor), capture(onFailureCaptor))
    onFailureCaptor.value.invoke(exception)
  }

  @Test
  fun startTimerWithNullValues() = runTest {
    `when`(timerManager.startTimerAction(capture(onSuccessCaptor), capture(onFailureCaptor)))
        .thenAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(1)
          onFailure(Exception("SharedPreferences.Editor is null"))
          null
        }

    var failureException: Exception? = null
    timerViewModel.startTimer(
        onSuccess = { fail("Should not call `onSuccess`") },
        onFailure = { e -> failureException = e })

    assertNotNull(failureException)
    assertEquals("SharedPreferences.Editor is null", failureException?.message)
  }

  @Test
  fun resetTimerSuccess() = runTest {
    doNothing()
        .`when`(timerManager)
        .resetTimerAction(capture(onSuccessCaptor), capture(onFailureCaptor))

    timerViewModel.resetTimer(onSuccess = {}, onFailure = { fail("Should not call `onFailure`") })

    verify(timerManager).resetTimerAction(capture(onSuccessCaptor), capture(onFailureCaptor))
    onSuccessCaptor.value.invoke()
  }

  @Test
  fun resetTimerFailure() = runTest {
    val exception = Exception("Failed to reset timer")

    doAnswer { it.getArgument<(Exception) -> Unit>(1)(Exception("Failed to reset timer")) }
        .`when`(timerManager)
        .resetTimerAction(capture(onSuccessCaptor), capture(onFailureCaptor))

    timerViewModel.resetTimer(onSuccess = { fail("Should not call `onSuccess`") }, onFailure = {})

    verify(timerManager).resetTimerAction(capture(onSuccessCaptor), capture(onFailureCaptor))
    onFailureCaptor.value.invoke(exception)
  }

  @Test
  fun resetTimerWithNullValues() = runTest {
    `when`(timerManager.resetTimerAction(capture(onSuccessCaptor), capture(onFailureCaptor)))
        .thenAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(1)
          onFailure(Exception("SharedPreferences.Editor is null"))
          null
        }

    var failureException: Exception? = null
    timerViewModel.resetTimer(
        onSuccess = { fail("Should not call `onSuccess`") },
        onFailure = { e -> failureException = e })

    assertNotNull(failureException)
    assertEquals("SharedPreferences.Editor is null", failureException?.message)
  }

  @Test
  fun stopTimerSuccess() = runTest {
    val elapsedTime = 1000L
    doAnswer { it.getArgument<(Long) -> Unit>(0)(elapsedTime) }
        .`when`(timerManager)
        .stopTimerAction(capture(onSuccessCaptorLong), capture(onFailureCaptor))

    timerViewModel.stopTimer(
        uid = UID,
        onSuccess = {},
        onFailure = { fail("Should not call `onFailure`") },
    )

    verify(timerManager).stopTimerAction(capture(onSuccessCaptorLong), capture(onFailureCaptor))
    onSuccessCaptorLong.value.invoke(elapsedTime)
  }

  @Test
  fun stopTimerFailure() = runTest {
    val exception = Exception("Failed to stop timer")

    doAnswer { it.getArgument<(Exception) -> Unit>(1)(Exception("Failed to stop timer")) }
        .`when`(timerManager)
        .stopTimerAction(capture(onSuccessCaptorLong), capture(onFailureCaptor))

    timerViewModel.stopTimer(
        uid = UID,
        onSuccess = { fail("Should not call `onSuccess`") },
        onFailure = {},
    )

    verify(timerManager).stopTimerAction(capture(onSuccessCaptorLong), capture(onFailureCaptor))
    onFailureCaptor.value.invoke(exception)
  }

  @Test
  fun stopTimerWithExtremeElapsedTime() = runTest {
    val elapsedTime = Long.MAX_VALUE
    doAnswer { it.getArgument<(Long) -> Unit>(0)(elapsedTime) }
        .`when`(timerManager)
        .stopTimerAction(capture(onSuccessCaptorLong), capture(onFailureCaptor))

    timerViewModel.stopTimer(
        uid = UID,
        onSuccess = {},
        onFailure = { fail("Should not call `onFailure`") },
    )

    verify(timerManager).stopTimerAction(capture(onSuccessCaptorLong), capture(onFailureCaptor))
    onSuccessCaptorLong.value.invoke(elapsedTime)
  }

  @Test
  fun computeAverageTimeOfUserSuccess() = runTest {
    val userID = "testUser"
    val timerList =
        listOf(
            Timer(time = TIME, instructionText = null), Timer(time = 2000L, instructionText = null))

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Timer>) -> Unit>(1)
          onSuccess(timerList)
          null
        }
        .`when`(timerRepository)
        .getTimersOfUser(eq(userID), capture(onSuccessCaptorList), capture(onFailureCaptor))

    timerViewModel.computeAverageTime(
        uid = userID, onSuccess = {}, onFailure = { fail("Should not call `onFailure`") })

    verify(timerRepository)
        .getTimersOfUser(eq(userID), capture(onSuccessCaptorList), capture(onFailureCaptor))
    onSuccessCaptorList.value.invoke(timerList)
  }

  @Test
  fun computeAverageTimeOfUserFailure() = runTest {
    val userID = "testUser"
    val exception = Exception("Failed to fetch timers")

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .`when`(timerRepository)
        .getTimersOfUser(eq(userID), capture(onSuccessCaptorList), capture(onFailureCaptor))

    timerViewModel.computeAverageTime(
        uid = userID, onSuccess = { fail("Should not call `onSuccess`") }, onFailure = {})

    verify(timerRepository)
        .getTimersOfUser(eq(userID), capture(onSuccessCaptorList), capture(onFailureCaptor))
    onFailureCaptor.value.invoke(exception)
  }

  @Test
  fun computeAverageTimeOfUserWithNullUserID() = runTest {
    val exception = Exception("UserID is null")

    doAnswer { it.getArgument<(Exception) -> Unit>(2)(exception) }
        .`when`(timerRepository)
        .getTimersOfUser(
            eq(null.toString()), capture(onSuccessCaptorList), capture(onFailureCaptor))

    timerViewModel.computeAverageTime(
        uid = null.toString(), onSuccess = { fail("Should not call `onSuccess`") }, onFailure = {})

    verify(timerRepository)
        .getTimersOfUser(
            eq(null.toString()), capture(onSuccessCaptorList), capture(onFailureCaptor))
    onFailureCaptor.value.invoke(exception)

    assertEquals(0.0, timerViewModel.userAverageTimer.value, 0.0)
  }
}
