package com.android.periodpals.model.user

import io.github.jan.supabase.postgrest.Postgrest
import java.util.UUID
import kotlin.math.absoluteValue
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class UserRepositorySupabaseTest {

  private lateinit var userRepository: UserRepositorySupabase
  private lateinit var mockPostgrest: Postgrest

  @Before
  fun setUp() {
    mockPostgrest = mock(Postgrest::class.java)
    userRepository = UserRepositorySupabase()
  }

  @Test
  fun loadUserProfileTest() = runBlocking {
    val userId = 1
    val userDto =
        UserDto(
            userId,
            "test",
            "test@email.com",
            "android.resource://com.android.periodpals/2131165319",
            "test",
            "10/17/2024")

    val result = userRepository.loadUserProfile(userId)

    assertEquals(userDto, result)
  }

  @Test
  fun createUserProfileTest() = runBlocking {
    val userId = UUID.randomUUID().mostSignificantBits.toInt().absoluteValue
    val user = User(userId, "John Doe", "john.doe@example.com", "", "Description", "10/17/2024")
    val userDto =
        UserDto(user.id, user.displayName, user.email, user.imageUrl, user.description, user.age)

    userRepository.createUserProfile(user)

    val result = userRepository.loadUserProfile(userId)

    assertEquals(userDto, result)
  }
}
