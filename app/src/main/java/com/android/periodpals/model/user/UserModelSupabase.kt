package com.android.periodpals.model.user

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "UserRepositorySupabase"
private const val USERS = "users"

/**
 * Implementation of UserRepository using Supabase.
 *
 * @property supabase The Supabase client used for making API calls.
 */
class UserRepositorySupabase(private val supabase: SupabaseClient) : UserRepository {

  override suspend fun loadUserProfile(
      idUser: String,
      onSuccess: (UserDto) -> Unit,
      onFailure: (Exception) -> Unit,
  ) {
    try {
      val result =
          withContext(Dispatchers.Main) {
            supabase.postgrest[USERS]
                .select { filter { eq("user_id", idUser) } }
                .decodeSingle<UserDto>() // RLS rules only allows user to check their own line
          }
      Log.d(TAG, "loadUserProfile: Success")
      onSuccess(result)
    } catch (e: Exception) {
      Log.d(TAG, "loadUserProfile: fail to load user profile: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun loadUserProfiles(
      onSuccess: (List<UserDto>) -> Unit,
      onFailure: (Exception) -> Unit,
  ) {
    try {
      val result =
          withContext(Dispatchers.IO) { supabase.postgrest[USERS].select {}.decodeList<UserDto>() }
      Log.d(TAG, "loadUserProfiles: Success")
      onSuccess(result)
    } catch (e: Exception) {
      Log.d(TAG, "loadUserProfiles: fail to load user profile: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun createUserProfile(
      user: User,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit,
  ) {
    try {
      withContext(Dispatchers.Main) {
        val userDto =
            UserDto(
                name = user.name,
                imageUrl = user.imageUrl,
                description = user.description,
                dob = user.dob,
                preferred_distance = user.preferredDistance,
                fcm_token = user.fcmToken,
                locationGIS = user.locationGIS,
            )
        supabase.postgrest[USERS].insert(userDto)
      }
      Log.d(TAG, "createUserProfile: Success")
      onSuccess()
    } catch (e: java.lang.Exception) {
      Log.d(TAG, "createUserProfile: fail to create user profile: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun upsertUserProfile(
      userDto: UserDto,
      onSuccess: (UserDto) -> Unit,
      onFailure: (Exception) -> Unit,
  ) {
    try {
      withContext(Dispatchers.Main) {
        val result = supabase.postgrest[USERS].upsert(userDto) { select() }.decodeSingle<UserDto>()
        Log.d(TAG, "upsertUserProfile: Success")
        onSuccess(result)
      }
    } catch (e: Exception) {
      Log.d(TAG, "upsertUserProfile: fail to create user profile: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun deleteUserProfile(
      idUser: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit,
  ) {
    try {
      withContext(Dispatchers.Main) {
        supabase.postgrest[USERS].delete { filter { eq("user_id", idUser) } }
      }
      Log.d(TAG, "deleteUserProfile: Success")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "deleteUserProfile: fail to delete user profile: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun uploadFile(
      filePath: String,
      bytes: ByteArray,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit,
  ) {
    try {
      withContext(Dispatchers.Main) {
        supabase.storage.from("avatars").upload("$filePath.jpg", bytes) { upsert = true }
      }
      Log.d(TAG, "uploadFile: Success")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "uploadFile: fail to upload file: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun downloadFile(
      filePath: String,
      onSuccess: (bytes: ByteArray) -> Unit,
      onFailure: (Exception) -> Unit,
  ) {
    try {
      withContext(Dispatchers.IO) {
        val file = supabase.storage.from("avatars").downloadPublic("$filePath.jpg")
        Log.d(TAG, "downloadFile: Success")
        onSuccess(file)
      }
    } catch (e: Exception) {
      Log.d(TAG, "downloadFile: fail to download file: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun downloadFilePublic(
      filePath: String,
      onSuccess: (bytes: ByteArray) -> Unit,
      onFailure: (Exception) -> Unit,
  ) {
    try {
      val file =
          withContext(Dispatchers.IO) {
            supabase.storage.from("avatars").downloadPublic("$filePath.jpg")
          }
      Log.d(TAG, "downloadFile: Success")
      onSuccess(file)
    } catch (e: Exception) {
      Log.d(TAG, "downloadFile: fail to download file: ${e.message}")
      onFailure(e)
    }
  }
}
