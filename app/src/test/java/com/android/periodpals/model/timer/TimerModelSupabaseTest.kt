package com.android.periodpals.model.timer

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
    private const val TIMER_ID = "timerID"
    private const val USER_ID = "mock_userID"
    private const val TIME = 10L
  }

  private val defaultTimerDto: TimerDto = TimerDto(Timer(time = TIME))

  private val supabaseClientSuccess =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { _ ->
          respond(
              content =
                  "[" +
                      "{\"timerID\":\"$TIMER_ID\"," +
                      "\"userID\":\"$USER_ID\"," +
                      "\"time\":$TIME}" +
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
  fun getTimersOfUserSuccess() = runTest {
    var result: List<Timer>? = null

    timerRepositorySupabase.getTimersOfUser(
        userID = USER_ID,
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
        userID = USER_ID,
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }

  @Test
  fun deleteTimersFilteredBySuccess() = runTest {
    var result = false

    timerRepositorySupabase.deleteTimersFilteredBy(
        cond = { eq("timerID", TIMER_ID) },
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
        cond = { eq("timerID", TIMER_ID) },
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }
}
