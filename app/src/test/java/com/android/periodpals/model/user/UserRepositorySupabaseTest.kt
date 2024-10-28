package com.android.periodpals.model.user

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.mockk.coEvery
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
                      "\"email\":\"test\"," +
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
    coEvery { userRepositorySupabase.loadUserProfile() } returns
            UserDto("test", "test", "test", "test", "test")
  }

  @Test
  fun `load user profile returns correct value`() {
    val userDto = UserDto("test", "test", "test", "test", "test")
    runBlocking {
      val result = userRepositorySupabase.loadUserProfile()
      assertEquals(userDto, result)
    }
  }

  @Test
  fun `load user profile is successful`() {
    val userId = 1
    val userDto = UserDto("test", "test", "test", "test", "test")

    runBlocking {
      val userRepositorySupabase = UserRepositorySupabase(supabaseClient)
      val result = userRepositorySupabase.loadUserProfile()
      assertEquals(userDto, result)
    }
  }

  @Test
  fun `create user profile is successful`() {
    val userDto = User("test", "test", "", "test", "test")

    runBlocking {
      val userRepositorySupabase = UserRepositorySupabase(supabaseClient)
      val result = userRepositorySupabase.createUserProfile(userDto)
      assertEquals(true, result)
    }
  }
}
