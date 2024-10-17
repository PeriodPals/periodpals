package com.android.periodpals.model.user

import com.android.periodpals.BuildConfig.SUPABASE_KEY
import com.android.periodpals.BuildConfig.SUPABASE_URL
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositorySupabase : UserRepository {

  override suspend fun loadUserProfile(id: Int): UserDto {
    return withContext(Dispatchers.IO) {
      val client = getClient()
      client.postgrest.from("users").select { filter { eq("id", id) } }.decodeSingle<UserDto>()
    }
  }

  override suspend fun createUserProfile(user: User): Boolean {
    return try {
      withContext(Dispatchers.IO) {
        val client = getClient()
        val userDto =
            UserDto(
                id = user.id,
                displayName = user.displayName,
                email = user.email,
                imageUrl = user.imageUrl,
                description = user.description,
                age = user.age)
        client.postgrest.from("users").insert(userDto)
        true
      }
      true
    } catch (e: java.lang.Exception) {
      throw e
    }
  }

  @OptIn(SupabaseInternal::class)
  private fun getClient(): SupabaseClient {
    return createSupabaseClient(supabaseUrl = SUPABASE_URL, supabaseKey = SUPABASE_KEY) {
      install(Postgrest)
    }
  }
}
