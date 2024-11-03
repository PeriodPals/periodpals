package com.android.periodpals.model.user

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserRepositorySupabaseTest {

  private lateinit var userRepositorySupabase: UserRepositorySupabase

  private val supabaseClient =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { _ ->
          respond(
              content =
                  "[" +
                      "{\"displayName\":\"test\"," +
                      "\"imageUrl\":\"test\"," +
                      "\"description\":\"test\"" +
                      ",\"age\":\"test\"}" +
                      "]")
        }
        install(Postgrest)
      }

  @Before
  fun setUp() {
    userRepositorySupabase = mockk<UserRepositorySupabase>()
  }

  @Test
  fun `loadUserProfile successful`() {
    var result: UserDto? = null
    val expected = UserDto("test", "test", "test", "test")

    runBlocking {
      // val userRepositorySupabase = UserRepositorySupabase(supabaseClient)
      UserRepositorySupabase(supabaseClient).loadUserProfile({ result = it }, {})
      assertEquals(expected, result)
    }
  }

  @Test
  fun `createUserProfile successful`() {
    var result = false
    val expected = User("test", "test", "", "test")

    runBlocking {
      val userRepositorySupabase = UserRepositorySupabase(supabaseClient)
      userRepositorySupabase.createUserProfile(expected, { result = true }, {})
      assert(result)
    }
  }
}
