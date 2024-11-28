package com.android.periodpals.model.timer

import com.android.periodpals.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
  @Captor private lateinit var onSuccessCaptorLong: ArgumentCaptor<(Long) -> Unit>
  @Captor private lateinit var onSuccessCaptorList: ArgumentCaptor<(List<Timer>) -> Unit>

  @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    timerViewModel = TimerViewModel(timerRepository, timerManager)
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
  fun stopTimerSuccess() = runTest {
    val elapsedTime = 1000L
    doAnswer { it.getArgument<(Long) -> Unit>(0)(elapsedTime) }
        .`when`(timerManager)
        .stopTimerAction(capture(onSuccessCaptorLong), capture(onFailureCaptor))

    timerViewModel.stopTimer(onSuccess = {}, onFailure = { fail("Should not call `onFailure`") })

    verify(timerManager).stopTimerAction(capture(onSuccessCaptorLong), capture(onFailureCaptor))
    onSuccessCaptorLong.value.invoke(elapsedTime)
  }

  @Test
  fun stopTimerFailure() = runTest {
    val exception = Exception("Failed to stop timer")

    doAnswer { it.getArgument<(Exception) -> Unit>(1)(Exception("Failed to stop timer")) }
        .`when`(timerManager)
        .stopTimerAction(capture(onSuccessCaptorLong), capture(onFailureCaptor))

    timerViewModel.stopTimer(onSuccess = { fail("Should not call `onSuccess`") }, onFailure = {})

    verify(timerManager).stopTimerAction(capture(onSuccessCaptorLong), capture(onFailureCaptor))
    onFailureCaptor.value.invoke(exception)
  }

  @Test
  fun fetchTimersOfUserSuccess() = runTest {
    val userID = "testUser"
    val timerList = listOf(Timer(time = 1000L), Timer(time = 2000L))

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Timer>) -> Unit>(1)
          onSuccess(timerList)
          null
        }
        .`when`(timerRepository)
        .getTimersOfUser(eq(userID), capture(onSuccessCaptorList), capture(onFailureCaptor))

    timerViewModel.fetchTimersOfUser(
        userID = userID, onSuccess = {}, onFailure = { fail("Should not call `onFailure`") })

    verify(timerRepository)
        .getTimersOfUser(eq(userID), capture(onSuccessCaptorList), capture(onFailureCaptor))
    onSuccessCaptorList.value.invoke(timerList)

    assertEquals(timerList, timerViewModel.userTimersList)
  }

  @Test
  fun fetchTimersOfUserFailure() = runTest {
    val userID = "testUser"
    val exception = Exception("Failed to fetch timers")

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .`when`(timerRepository)
        .getTimersOfUser(eq(userID), capture(onSuccessCaptorList), capture(onFailureCaptor))

    timerViewModel.fetchTimersOfUser(
        userID = userID, onSuccess = { fail("Should not call `onSuccess`") }, onFailure = {})

    verify(timerRepository)
        .getTimersOfUser(eq(userID), capture(onSuccessCaptorList), capture(onFailureCaptor))
    onFailureCaptor.value.invoke(exception)

    assertEquals(emptyList<Timer>(), timerViewModel.userTimersList)
  }

  @Test
  fun getRemainingTime() = runTest {
    `when`(timerManager.getRemainingTime()).thenReturn(1000L)

    val remainingTime = timerViewModel.getRemainingTime()

    assertEquals(1000L, remainingTime)
    verify(timerManager).getRemainingTime()
  }
}
