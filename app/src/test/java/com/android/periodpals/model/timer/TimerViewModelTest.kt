import com.android.periodpals.MainCoroutineRule
import com.android.periodpals.model.timer.Timer
import com.android.periodpals.model.timer.TimerDto
import com.android.periodpals.model.timer.TimerManager
import com.android.periodpals.model.timer.TimerRepositorySupabase
import com.android.periodpals.model.timer.TimerViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Incubating
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class TimerViewModelTest {
  @Mock private lateinit var timerModel: TimerRepositorySupabase
  @Mock private lateinit var timerManager: TimerManager
  @Incubating private lateinit var timerViewModel: TimerViewModel
  @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    timerViewModel = TimerViewModel(timerModel, timerManager)
  }

  @Test
  fun loadTimerIsSuccessful() = runTest {
    val timer = TimerDto(Json.encodeToString(listOf(1L, 2L, 3L)))
    val expected = timer.asTimer()

    doAnswer { it.getArgument<(TimerDto) -> Unit>(0)(timer) }
        .`when`(timerModel)
        .loadTimer(any<(TimerDto) -> Unit>(), any<(Exception) -> Unit>())

    timerViewModel.loadTimer()

    assertEquals(expected, timerViewModel.timer.value)
  }

  @Test
  fun loadTimerHasFailed() = runTest {
    doAnswer { it.getArgument<(Exception) -> Unit>(1)(Exception("failed")) }
        .`when`(timerModel)
        .loadTimer(any<(TimerDto) -> Unit>(), any<(Exception) -> Unit>())

    timerViewModel.loadTimer()

    assertNull(timerViewModel.timer.value)
  }

  @Test
  fun startTimer() {
    val timer = Timer(lastTimers = listOf())
    timerViewModel.setTimerForTesting(timer)

    timerViewModel.startTimer()

    verify(timerManager).startTimerAction()
  }

  @Test
  fun resetTimer() {
    val timer = Timer(lastTimers = listOf())
    timerViewModel.setTimerForTesting(timer)

    timerViewModel.resetTimer()

    verify(timerManager).resetTimerAction()
  }

  @Test
  fun stopTimer_success() = runTest {
    val timer = Timer(lastTimers = listOf(1L, 2L, 3L))
    val timerDto = TimerDto(lastTimers = Json.encodeToString(listOf(1L, 2L, 3L, 3600000L)))
    timerViewModel.setTimerForTesting(timer)

    `when`(timerManager.stopTimerAction()).thenReturn(3600000L) // 1 hour in milliseconds

    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(TimerDto) -> Unit>(1)
          onSuccess(timerDto)
          null
        }
        .`when`(timerModel)
        .upsertTimer(any(), any(), any())

    timerViewModel.stopTimer()

    assertEquals(timer.copy(lastTimers = listOf(1L, 2L, 3L, 3600000L)), timerViewModel.timer.value)
  }

  @Test
  fun stopTimer_failure() = runTest {
    val timer = Timer(lastTimers = listOf())
    timerViewModel.setTimerForTesting(timer)

    `when`(timerManager.stopTimerAction()).thenReturn(3600000L) // 1 hour in milliseconds

    val exception = Exception("Save timer failed")

    doAnswer { invocation ->
          val onFailure = invocation.getArgument<(Exception) -> Unit>(2)
          onFailure(exception)
          null
        }
        .`when`(timerModel)
        .upsertTimer(any(), any(), any())

    timerViewModel.stopTimer()

    assertNull(timerViewModel.timer.value)
  }

  @Test
  fun getRemainingTime() {
    `when`(timerManager.getRemainingTime()).thenReturn(3600000L) // 1 hour in milliseconds

    val remainingTime = timerViewModel.getRemainingTime()

    assertEquals(3600000L, remainingTime)
  }
}
