package com.android.periodpals.model.user

import android.util.Log
import com.android.periodpals.model.location.LocationGIS
import com.powersync.PowerSyncDatabase
import com.powersync.connector.supabase.SupabaseConnector
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Implementation of UserRepository using PowerSync with Supabase.
 *
 * @property db PowerSync's database used to locally cache everything that calls syncs to online
 *   servers.
 * @property supabase The Supabase client used for making API calls.
 */
private const val TAG = "UserModelPowerSync"
private const val USERS = "users"

class UserModelPowerSync(
    private val db: PowerSyncDatabase,
    private val connector: SupabaseConnector,
    private val supabase: SupabaseClient
) : UserRepository {

  suspend fun syncSupabase() {
    try {
      connector.uploadData(db)
      Log.d(TAG, "syncSupabase: Success")
    } catch (e: Exception) {
      Log.d(TAG, "syncSupabase: Failure ${e.message}")
    }
  }

  override suspend fun loadUserProfile(
      onSuccess: (UserDto) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val currUser =
          supabase.auth.currentUserOrNull()?.id
              ?: throw Exception("Supabase does not have a user logged in")

      val user: UserDto =
          db.get(
              "SELECT name, imageUrl, description, dob, fcm_token, locationGIS FROM $USERS WHERE user_id = ?",
              listOf(currUser)) {
                UserDto(
                    name = it.getString(0)!!,
                    imageUrl = it.getString(1)!!,
                    description = it.getString(2)!!,
                    dob = it.getString(3)!!,
                    fcm_token = it.getString(4),
                    locationGIS = Json.decodeFromString<LocationGIS>(it.getString(5)!!))
              }

      Log.d(TAG, "loadUserProfile: Success")
      onSuccess(user)
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
      db.writeTransaction { tx ->
        tx.execute(
            "INSERT INTO $USERS (name, imageUrl, description, dob, fcm_token, locationGIS) VALUES (?, ?, ?, ?, ?, ?);",
            user.asList())
      }
      Log.d(TAG, "createUserProfile: Success")
      onSuccess()
      connector.uploadData(db)
    } catch (e: Exception) {
      Log.d(TAG, "createUserProfile: fail to create user profile: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun upsertUserProfile(
      userDto: UserDto,
      onSuccess: (UserDto) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val currUser: String? = supabase.auth.currentUserOrNull()?.id

      db.writeTransaction { tx ->
        tx.execute(
            """
                        INSERT INTO $USERS (name, imageUrl, description, dob, fcm_token, locationGIS)
                        VALUES (?, ?, ?, ?, ?)
                        ON CONFLICT (user_id)
                        DO UPDATE SET name = ?, imageUrl = ?, description = ?, dob = ?, fcm_token = ?, locationGIS = ?;
                    """,
            listOf(
                currUser,
                userDto.name,
                userDto.imageUrl,
                userDto.description,
                userDto.dob,
                userDto.fcm_token,
                Json.encodeToString(userDto.locationGIS),
                userDto.name,
                userDto.imageUrl,
                userDto.description,
                userDto.dob,
                userDto.fcm_token,
                Json.encodeToString(userDto.locationGIS)))
      }
      Log.d(TAG, "upsertUserProfile: Success")
      connector.uploadData(db)
      onSuccess(userDto)
    } catch (e: Exception) {
      Log.d(TAG, "upsertUserProfile: fail to create user profile: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun deleteUserProfile(
      idUser: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val currUser =
          supabase.auth.currentUserOrNull()?.id
              ?: throw Exception("Supabase does not have a user logged in")

      db.writeTransaction { tx ->
        tx.execute("DELETE FROM $USERS WHERE user_id = ?", listOf(currUser))
      }
      Log.d(TAG, "deleteUserProfile: Success")
      connector.uploadData(db)
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
      withContext(Dispatchers.Main) {
        val file = supabase.storage.from("avatars").downloadAuthenticated("$filePath.jpg")
        Log.d(TAG, "downloadFile: Success")
        onSuccess(file)
      }
    } catch (e: Exception) {
      Log.d(TAG, "downloadFile: fail to download file: ${e.message}")
      onFailure(e)
    }
  }
}
