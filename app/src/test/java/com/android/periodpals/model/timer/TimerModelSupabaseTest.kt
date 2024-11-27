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
    const val timerID = "timerID"
    const val userID = "mock_userID"
    const val time = 10
  }

  private val defaultTimer: Timer = Timer(timerID = timerID, userID = userID, time = time)

  private val supabaseClientSuccess =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { _ ->
          respond(
              content =
                  "[" +
                      "{\"timerID\":\"$timerID\"," +
                      "\"userID\":\"$userID\"," +
                      "\"time\":$time}" +
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
        timerDto = TimerDto(defaultTimer),
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
        timerDto = TimerDto(defaultTimer),
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }

  @Test
  fun getTimersOfUserSuccess() = runBlocking {
    var result: List<Timer>? = null

    timerRepositorySupabase.getTimersOfUser(
        userID = userID,
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
        userID = userID,
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }

  @Test
  fun deleteTimerSuccess() = runBlocking {
    var result = false

    timerRepositorySupabase.deleteTimerById(
        timerID = timerID,
        onSuccess = { result = true },
        onFailure = { fail("Should not call onFailure") },
    )
    assert(result)
  }

  @Test
  fun deleteTimerHasFailed() = runBlocking {
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientFailure)
    var onFailureCalled = false

    timerRepositorySupabase.deleteTimerById(
        timerID = timerID,
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }
}
