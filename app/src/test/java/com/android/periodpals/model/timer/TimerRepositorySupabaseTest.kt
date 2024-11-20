package com.android.periodpals.model.timer

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class TimerRepositorySupabaseTest {
  private lateinit var timerRepositorySupabase: TimerRepositorySupabase

  companion object {
    const val uid = "mock_uid"
    val status = TimerStatus.RUNNING
    val AVERAGE_TIMER = 3L
    val TIMER_COUNT = 4
    val STARTED_AT = LocalDateTime(2000, 1, 31, 0, 0, 0, 0).toString()
    val UPDATED_AT = "6"
  }

  private var defaultTimer: Timer =
      Timer(
          uid = uid,
          status = status,
          averageTime = AVERAGE_TIMER,
          timerCount = TIMER_COUNT,
          startedAt = STARTED_AT,
          updatedAt = UPDATED_AT)

  // Copy to reset the timer object after updateTimer()
  private val originalTimer: Timer = defaultTimer.copy()

  private val supabaseClientSuccess =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { request ->
          respond(
              content =
                  "[" +
                      "{\"uid\":\"${uid}\"," +
                      "\"status\":\"${status}\"," +
                      "\"averageTime\":\"${AVERAGE_TIMER}\"" +
                      ",\"timerCount\":\"${TIMER_COUNT}\"" +
                      ",\"startedAt\":\"${STARTED_AT}\"" +
                      ",\"updatedAt\":\"${UPDATED_AT}\"" +
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
  fun getTimerSuccess() = runBlocking {
    var result: Timer? = null

    timerRepositorySupabase.getTimer(
        uid = uid, onSuccess = { timer -> result = timer }, onFailure = { fail("Should not fail") })
    assertEquals(defaultTimer, result)
  }
}
