package com.android.periodpals.model.timer

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class TimerRepositorySupabaseTest {

  private lateinit var timerRepository: TimerRepositorySupabase

  companion object {
    val startedTime = "a"
    val elapsedTime = 3
    val status = TimerStatus.RUNNING
    val lastTimers = listOf(4, 5, 6)
  }

  private val defaultTimerDto: TimerDto = TimerDto(startedTime, elapsedTime, status, lastTimers)
  private val defaultTimer: Timer = Timer(startedTime, elapsedTime, status, lastTimers)

  private val supabaseClientSuccess =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { request ->
          respond(
              content =
                  "[" +
                      "{\"startTime\":\"${startedTime}\"," +
                      "\"elapsedTime\":${elapsedTime}," +
                      "\"status\":\"${status}\"," +
                      "\"lastTimers\":[4,5,6]}" +
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
    timerRepository = TimerRepositorySupabase(supabaseClientSuccess)
  }

  @Test
  fun getTimerSuccess() = runTest {
    var result: TimerDto? = null

    timerRepository.loadTimer(
        onSuccess = { timer -> result = timer }, onFailure = { fail("Should not fail") })
    assertEquals(TimerDto("1", 3, TimerStatus.RUNNING, listOf(4, 5, 6)), result)
  }

  @Test
  fun getTimerFailure() = runTest {
    timerRepository = TimerRepositorySupabase(supabaseClientFailure)
    timerRepository.loadTimer(
        onSuccess = { fail("Should not succeed") },
        onFailure = { assertEquals("Bad Request", it.message) })
  }

  @Test
  fun upsertTimerSuccess() = runTest {
    val timerDto = TimerDto("1", 3, TimerStatus.RUNNING, listOf(4, 5, 6))
    var result: TimerDto? = null

    timerRepository.upsertTimer(
        timerDto, onSuccess = { timer -> result = timer }, onFailure = { fail("Should not fail") })
    assertEquals(timerDto, result)
  }

  @Test
  fun upsertTimerFailure() = runTest {
    timerRepository = TimerRepositorySupabase(supabaseClientFailure)
    val timerDto = TimerDto("1", 3, TimerStatus.RUNNING, listOf(4, 5, 6))

    timerRepository.upsertTimer(
        timerDto,
        onSuccess = { fail("Should not succeed") },
        onFailure = { assertEquals("Bad Request", it.message) })
  }
}
