package com.android.periodpals.model.user

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class UserRepositorySupabaseTest {

  private lateinit var userRepositorySupabase: UserRepositorySupabase

  companion object {
    val name = "test_name"
    val imageUrl = "test_image"
    val description = "test_description"
    val dob = "test_dob"
    val id = "test_id"
    val preferredDistance = 500
    val fcmToken = "test_fcm_token"
  }

  private val defaultUserDto: UserDto =
      UserDto(name, imageUrl, description, dob, preferredDistance, fcmToken)
  private val defaultUser: User =
      User(name, imageUrl, description, dob, preferredDistance, fcmToken)

  private val supabaseClientSuccess =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { _ ->
          respond(
              content =
                  "[" +
                      "{\"name\":\"${name}\"," +
                      "\"imageUrl\":\"${imageUrl}\"," +
                      "\"description\":\"${description}\"," +
                      "\"dob\":\"${dob}\"," +
                      "\"fcm_token\":\"${fcmToken}\"," +
                      "\"preferred_distance\":\"${preferredDistance}\"}" +
                      "]")
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
    userRepositorySupabase = mockk<UserRepositorySupabase>()
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun loadUserProfileIsSuccessful() {
    var result: UserDto? = null

    runTest {
      val userRepositorySupabase = UserRepositorySupabase(supabaseClientSuccess)
      userRepositorySupabase.loadUserProfile({ result = it }, { fail("should not call onFailure") })
      assertEquals(defaultUserDto, result)
    }
  }

  @Test
  fun loadUserProfileHasFailed() {
    var onFailureCalled = false

    runTest {
      val userRepositorySupabase = UserRepositorySupabase(supabaseClientFailure)
      userRepositorySupabase.loadUserProfile(
          { fail("should not call onSuccess") },
          { onFailureCalled = true },
      )
      assert(onFailureCalled)
    }
  }

  @Test
  fun createUserProfileIsSuccessful() {
    var result = false

    runTest {
      val userRepositorySupabase = UserRepositorySupabase(supabaseClientSuccess)
      userRepositorySupabase.createUserProfile(
          defaultUser,
          { result = true },
          { fail("should not call onFailure") },
      )
      assert(result)
    }
  }

  @Test
  fun createUserProfileHasFailed() {
    var result = false

    runTest {
      val userRepositorySupabase = UserRepositorySupabase(supabaseClientFailure)
      userRepositorySupabase.createUserProfile(
          defaultUser,
          { fail("should not call onSuccess") },
          { result = true },
      )
      assert(result)
    }
  }

  @Test
  fun upsertUserProfileIsSuccessful() {
    var result: UserDto? = null

    runTest {
      val userRepositorySupabase = UserRepositorySupabase(supabaseClientSuccess)
      userRepositorySupabase.upsertUserProfile(
          defaultUserDto,
          { result = it },
          { fail("should not call onFailure") },
      )
      assertEquals(defaultUserDto, result)
    }
  }

  @Test
  fun upsertUserProfileHasFailed() {
    var result = false

    runTest {
      val userRepositorySupabase = UserRepositorySupabase(supabaseClientFailure)
      userRepositorySupabase.upsertUserProfile(
          defaultUserDto,
          { fail("should not call onSuccess") },
          { result = true },
      )
      assert(result)
    }
  }

  @Test
  fun deleteUserProfileIsSuccessful() {
    var result = false

    runTest {
      val userRepositorySupabase = UserRepositorySupabase(supabaseClientSuccess)
      userRepositorySupabase.deleteUserProfile(
          id,
          { result = true },
          { fail("should not call onFailure") },
      )
      assert(result)
    }
  }

  @Test
  fun deleteUserProfileHasFailed() {
    var result = false

    runTest {
      val userRepositorySupabase = UserRepositorySupabase(supabaseClientFailure)
      userRepositorySupabase.deleteUserProfile(
          id,
          { fail("should not call onSuccess") },
          { result = true },
      )
      assert(result)
    }
  }

  @Test
  fun uploadFileHasFailed() {
    var result = false

    runTest {
      val userRepositorySupabase = UserRepositorySupabase(supabaseClientFailure)
      userRepositorySupabase.uploadFile(
          "test", byteArrayOf(), { fail("should not call onSuccess") }, { result = true })
      assert(result)
    }
  }

  @Test
  fun downloadFileHasFailed() {
    var result = false

    runTest {
      val userRepositorySupabase = UserRepositorySupabase(supabaseClientFailure)
      userRepositorySupabase.downloadFile(
          "test", { fail("should not call onSuccess") }, { result = true })
      assert(result)
    }
  }
}
