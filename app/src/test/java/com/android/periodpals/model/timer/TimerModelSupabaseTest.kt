package com.android.periodpals.model.timer

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
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

  private val defaultTimer: Timer = Timer(time = TIME)

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

  @Before
  fun setUp() {
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientSuccess)
  }

  @Test
  fun addTimerSuccess() = runBlocking {
    var result = false

    timerRepositorySupabase.addTimer(
        timer = defaultTimer,
        onSuccess = { result = true },
        onFailure = { fail("Should not call onFailure") },
    )
    assert(result)
  }

  @Test
  fun addTimerFailure() = runBlocking {
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientFailure)
    var onFailureCalled = false

    timerRepositorySupabase.addTimer(
        timer = defaultTimer,
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }

  @Test
  fun getTimersOfUserSuccess() = runBlocking {
    var result: List<Timer>? = null

    timerRepositorySupabase.getTimersOfUser(
        userID = USER_ID,
        onSuccess = { result = it },
        onFailure = { fail("Should not call onFailure") },
    )
    assert(result != null)
  }

  @Test
  fun getTimersOfUserFailure() = runBlocking {
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
  fun deleteTimerByTimerIdSuccess() = runBlocking {
    var result = false

    timerRepositorySupabase.deleteTimerByTimerId(
        timerID = TIMER_ID,
        onSuccess = { result = true },
        onFailure = { fail("Should not call onFailure") },
    )
    assert(result)
  }

  @Test
  fun deleteTimerByTimerIdHasFailed() = runBlocking {
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientFailure)
    var onFailureCalled = false

    timerRepositorySupabase.deleteTimerByTimerId(
        timerID = TIMER_ID,
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }

  @Test
  fun deleteTimersByUserIdSuccess() = runBlocking {
    var result = false

    timerRepositorySupabase.deleteTimersByUserId(
        userID = USER_ID,
        onSuccess = { result = true },
        onFailure = { fail("Should not call onFailure") },
    )
    assert(result)
  }

  @Test
  fun deleteTimersByUserIdHasFailed() = runBlocking {
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientFailure)
    var onFailureCalled = false

    timerRepositorySupabase.deleteTimersByUserId(
        userID = USER_ID,
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }
}
