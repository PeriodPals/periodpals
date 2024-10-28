package com.android.periodpals.model.user

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of UserRepository using Supabase.
 *
 * @property supabaseClient The Supabase client used for making API calls.
 */
class UserRepositorySupabase(private val supabaseClient: SupabaseClient) : UserRepository {

  override suspend fun loadUserProfile(): UserDto {
    return withContext(Dispatchers.IO) {
      supabaseClient.postgrest["users"].select {}.decodeSingle<UserDto>()
    }
  }

  override suspend fun createUserProfile(user: User): Boolean {
    return try {
      withContext(Dispatchers.IO) {
        val userDto =
            UserDto(
                displayName = user.displayName,
                imageUrl = user.imageUrl,
                description = user.description,
                age = user.age)
        supabaseClient.postgrest["users"].insert(userDto)
        true
      }
      true
    } catch (e: java.lang.Exception) {
      throw e
    }
  }
}
