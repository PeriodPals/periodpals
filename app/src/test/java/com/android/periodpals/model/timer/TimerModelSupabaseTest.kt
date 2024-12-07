package com.android.periodpals.model.timer

import android.util.Log
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class TimerModelSupabaseTest {
  private lateinit var timerRepositorySupabase: TimerRepositorySupabase

  companion object {
    private const val ID = "timerID"
    private const val ID_2 = "timerID2"
    private const val UID = "mock_uid"
    private const val TIME = 1000L
    private const val INSTRUCTION = "mock_instruction"
  }

  private val defaultTimerDto: TimerDto =
      TimerDto(Timer(id = ID, time = TIME, instructionText = INSTRUCTION))

  private val supabaseClientSuccess =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { _ ->
          respond(
              content =
                  "[" +
                      "{\"id\":\"$ID\"," +
                      "\"uid\":\"$UID\"," +
                      "\"time\":$TIME," +
                      "\"instructionText\":\"$INSTRUCTION\"}" +
                      "]",
              status = HttpStatusCode.OK,
          )
        }
        install(Postgrest)
      }
  private val supabaseClientActiveTimer =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { _ ->
          respond(
              content =
                  "[" +
                      "{\"id\":\"$ID\"," +
                      "\"uid\":\"$UID\"," +
                      "\"time\":$ACTIVE_TIMER_TIME," +
                      "\"instructionText\":\"$INSTRUCTION\"}" +
                      "]",
              status = HttpStatusCode.OK,
          )
        }
        install(Postgrest)
      }

  private val supabaseClientNoTimers =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { _ ->
          respond(
              content = "[]", // No timers
              status = HttpStatusCode.OK,
          )
        }
        install(Postgrest)
      }

  private val supabaseClientMultipleActiveTimers =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { _ ->
          respond(
              content =
                  "[" +
                      "{\"id\":\"$ID\"," +
                      "\"uid\":\"$UID\"," +
                      "\"time\":$ACTIVE_TIMER_TIME," +
                      "\"instructionText\":\"$INSTRUCTION\"}," +
                      "{\"id\":\"$ID_2\"," +
                      "\"uid\":\"$UID\"," +
                      "\"time\":$ACTIVE_TIMER_TIME," +
                      "\"instructionText\":\"$INSTRUCTION\"}" +
                      "]",
              status = HttpStatusCode.OK,
          )
        }
        install(Postgrest)
      }

  private val supabaseClientFailure =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { _ -> respondBadRequest() }
        install(Postgrest)
      }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    Dispatchers.setMain(Dispatchers.Unconfined)
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientSuccess)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun getActiveTimerSuccess() = runTest {
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientActiveTimer)
    var result: Timer? = null

    timerRepositorySupabase.getActiveTimer(
        uid = UID,
        onSuccess = { result = it },
        onFailure = { fail("Should not call onFailure") },
    )
    Log.d("TimerModelSupabaseTest", "getActiveTimerSuccess: result: ${result == null}")

    assert(result != null)
    assert(result!!.instructionText != null)
    assert(result!!.instructionText!!.isNotEmpty())
  }

  @Test
  fun getActiveTimerNoneActive() = runTest {
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientNoTimers)
    var resultNoTimers: Timer? = null

    timerRepositorySupabase.getActiveTimer(
        uid = UID,
        onSuccess = { resultNoTimers = it },
        onFailure = { fail("Should not call onFailure") },
    )
    assert(resultNoTimers == null)
  }

  @Test
  fun getActiveTimerMultipleActive() = runTest {
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientMultipleActiveTimers)
    var result: Timer? = null

    timerRepositorySupabase.getActiveTimer(
        uid = UID,
        onSuccess = { result = it },
        onFailure = { fail("Should not call onFailure") },
    )
    assert(result == null)
  }

  @Test
  fun getActiveTimerExceptionFailure() = runTest {
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientFailure)
    var onFailureCalled = false

    timerRepositorySupabase.getActiveTimer(
        uid = UID,
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }

  @Test
  fun addTimerSuccess() = runTest {
    var result = false

    timerRepositorySupabase.addTimer(
        timerDto = defaultTimerDto,
        onSuccess = { result = true },
        onFailure = { fail("Should not call onFailure") },
    )
    assert(result)
  }

  @Test
  fun addTimerFailure() = runTest {
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientFailure)
    var onFailureCalled = false

    timerRepositorySupabase.addTimer(
        timerDto = defaultTimerDto,
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }

  @Test
  fun updateTimerSuccess() = runTest {
    var result = false
    val updatedTimerDto = defaultTimerDto.copy(time = TIME + 5)

    timerRepositorySupabase.updateTimer(
        timerDto = updatedTimerDto,
        onSuccess = { result = true },
        onFailure = { fail("Should not call onFailure") },
    )
    assert(result)
  }

  @Test
  fun updateTimerFailure() = runTest {
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientFailure)
    var onFailureCalled = false
    val updatedTimerDto = defaultTimerDto.copy(time = TIME + 5)

    timerRepositorySupabase.updateTimer(
        timerDto = updatedTimerDto,
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }

  @Test
  fun getTimersOfUserSuccess() = runTest {
    var result: List<Timer>? = null

    timerRepositorySupabase.getTimersOfUser(
        uid = UID,
        onSuccess = { result = it },
        onFailure = { fail("Should not call onFailure") },
    )
    assert(result != null)
  }

  @Test
  fun getTimersOfUserFailure() = runTest {
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientFailure)
    var onFailureCalled = false

    timerRepositorySupabase.getTimersOfUser(
        uid = UID,
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }

  @Test
  fun deleteTimersFilteredBySuccess() = runTest {
    var result = false

    timerRepositorySupabase.deleteTimersFilteredBy(
        cond = { eq("timerID", ID) },
        onSuccess = { result = true },
        onFailure = { fail("Should not call onFailure") },
    )
    assert(result)
  }

  @Test
  fun deleteTimersFilteredByHasFailed() = runTest {
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientFailure)
    var onFailureCalled = false

    timerRepositorySupabase.deleteTimersFilteredBy(
        cond = { eq("timerID", ID) },
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }
}
