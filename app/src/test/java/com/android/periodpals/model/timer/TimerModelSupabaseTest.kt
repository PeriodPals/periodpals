package com.android.periodpals.model.timer

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class TimerModelSupabaseTest {
  private lateinit var timerRepositorySupabase: TimerRepositorySupabase

  companion object {
    val lastTimersString = Json.encodeToString(listOf(4L, 5L, 6L))
  }

  private val defaultTimerDto = TimerDto(lastTimers = lastTimersString)

  private val supabaseClientSuccess =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { _ ->
          respond(content = "[{\"lastTimers\":\"${lastTimersString}\"}]")
        }
        install(Postgrest)
      }
  private val supabaseClientFailure =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { _ -> respondBadRequest() }
        install(Postgrest)
      }
  @OptIn(ExperimentalCoroutinesApi::class) private val testDispatcher = UnconfinedTestDispatcher()

  @OptIn(ExperimentalCoroutinesApi::class)
  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    timerRepositorySupabase = TimerRepositorySupabase(supabaseClientSuccess)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun loadTimerSuccessfull() {
    var result: TimerDto? = null

    runTest {
      val timerRepositorySupabase = TimerRepositorySupabase(supabaseClientSuccess)
      timerRepositorySupabase.loadTimer(
          onSuccess = { result = it },
          onFailure = { fail("Should not call onFailure") },
      )
      assertEquals(defaultTimerDto, result)
    }
  }

  @Test
  fun loadTimerhasFailed() {
    var onFailureCalled = false

    runTest {
      val timerRepositorySupabase = TimerRepositorySupabase(supabaseClientFailure)
      timerRepositorySupabase.loadTimer(
          onSuccess = { fail("Should not call onSuccess") },
          onFailure = { onFailureCalled = true },
      )
      assert(onFailureCalled)
    }
  }

  @Test
  fun upsertTimerSuccessfull() {
    var result: TimerDto? = null

    runTest {
      val timerRepositorySupabase = TimerRepositorySupabase(supabaseClientSuccess)
      timerRepositorySupabase.upsertTimer(
          timerDto = defaultTimerDto,
          onSuccess = { result = it },
          onFailure = { fail("Should not call onFailure") },
      )
      assertEquals(defaultTimerDto, result)
    }
  }

  @Test
  fun upsertTimerHasFailed() {
    var onFailureCalled = false

    runTest {
      val timerRepositorySupabase = TimerRepositorySupabase(supabaseClientFailure)
      timerRepositorySupabase.upsertTimer(
          timerDto = defaultTimerDto,
          onSuccess = { fail("Should not call onSuccess") },
          onFailure = { onFailureCalled = true },
      )
      assert(onFailureCalled)
    }
  }

  @Test
  fun deleteTimerSuccessfull() {
    var result = false

    runTest {
      val timerRepositorySupabase = TimerRepositorySupabase(supabaseClientSuccess)
      timerRepositorySupabase.deleteTimer(
          onSuccess = { result = true },
          onFailure = { fail("Should not call onFailure") },
      )
      assert(result)
    }
  }

  @Test
  fun deleteTimerHasFailed() {
    var onFailureCalled = false

    runTest {
      val timerRepositorySupabase = TimerRepositorySupabase(supabaseClientFailure)
      timerRepositorySupabase.deleteTimer(
          onSuccess = { fail("Should not call onSuccess") },
          onFailure = { onFailureCalled = true },
      )
      assert(onFailureCalled)
    }
  }
}
