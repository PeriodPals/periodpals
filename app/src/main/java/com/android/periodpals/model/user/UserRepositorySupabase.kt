package com.android.periodpals.model.user

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of UserRepository using Supabase.
 *
 * @property supabase The Supabase client used for making API calls.
 */
private const val TAG = "UserRepositorySupabase"

class UserRepositorySupabase(private val supabase: SupabaseClient) : UserRepository {

  override suspend fun loadUserProfile(
      onSuccess: (UserDto) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val result =
          withContext(Dispatchers.IO) {
            supabase.postgrest["users"]
                .select {}
                .decodeSingle<UserDto>() // RLS rules only allows user to check their own line
          }
      Log.d(TAG, "loadUserProfile: Success")
      onSuccess(result)
    } catch (e: Exception) {
      Log.d(TAG, "loadUserProfile: fail to load user profile: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun createUserProfile(
      user: User,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.IO) {
        val userDto =
            UserDto(
                displayName = user.displayName,
                imageUrl = user.imageUrl,
                description = user.description,
                age = user.age)
        supabase.postgrest["users"].insert(userDto)
      }
      Log.d(TAG, "createUserProfile: Success")
      onSuccess()
    } catch (e: java.lang.Exception) {
      Log.d(TAG, "createUserProfile: fail to create user profile: ${e.message}")
      onFailure(e)
    }
  }
}