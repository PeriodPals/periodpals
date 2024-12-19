package com.android.periodpals.model.location

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

class UserLocationModelSupabaseTest {
  private lateinit var userLocationModelSupabase: UserLocationModelSupabase

  private val supabaseClientSuccess =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { _ ->
          respond(
              content =
                  """{"uid":"user123","locationGIS":{"type":"Point","coordinates":[12.34,56.78]}}""",
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
    userLocationModelSupabase = UserLocationModelSupabase(supabaseClientSuccess)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun createLocationSuccess() = runTest {
    val location = LocationGIS("Point", listOf(12.34, 56.78))
    val userLocationDto = UserLocationDto("user123", location)
    var result = false

    userLocationModelSupabase.create(
        locationDto = userLocationDto,
        onSuccess = { result = true },
        onFailure = { fail("Should not call onFailure") },
    )
    assert(result)
  }

  @Test
  fun createLocationFailure() = runTest {
    userLocationModelSupabase = UserLocationModelSupabase(supabaseClientFailure)
    val location = LocationGIS("Point", listOf(12.34, 56.78))
    val userLocationDto = UserLocationDto("user123", location)
    var onFailureCalled = false

    userLocationModelSupabase.create(
        locationDto = userLocationDto,
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }

  @Test
  fun updateLocationSuccess() = runTest {
    val location = LocationGIS("Point", listOf(12.34, 56.78))
    val userLocationDto = UserLocationDto("user123", location)
    var result = false

    userLocationModelSupabase.update(
        locationDto = userLocationDto,
        onSuccess = { result = true },
        onFailure = { fail("Should not call onFailure") },
    )
    assert(result)
  }

  @Test
  fun updateLocationFailure() = runTest {
    userLocationModelSupabase = UserLocationModelSupabase(supabaseClientFailure)
    val location = LocationGIS("Point", listOf(12.34, 56.78))
    val userLocationDto = UserLocationDto("user123", location)
    var onFailureCalled = false

    userLocationModelSupabase.update(
        locationDto = userLocationDto,
        onSuccess = { fail("Should not call onSuccess") },
        onFailure = { onFailureCalled = true },
    )
    assert(onFailureCalled)
  }
}
