package com.android.periodpals.model.user

import android.util.Log
import com.powersync.PowerSyncDatabase
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth

/**
 * Implementation of UserRepository using PowerSync with Supabase.
 *
 * @property db PowerSync's database used to locally cache everything that calls syncs to online servers.
 * @property supabase The Supabase client used for making API calls.
 */
private const val TAG = "UserModelPowerSync"
private const val USERS = "users"

class UserModelPowerSync(private val db: PowerSyncDatabase, private val supabase: SupabaseClient) :
    UserRepository {

  override suspend fun loadUserProfile(
      onSuccess: (UserDto) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val currUser =
          supabase.auth.currentUserOrNull()?.id
              ?: throw Exception("Supabase does not have a user logged in")

        val user: UserDto? =
            db.writeTransaction { tx ->
                tx.getOptional(
                    "SELECT name, imageUrl, description, dob FROM $USERS WHERE user_id = ?",
                    listOf(currUser)) {
                      UserDto(
                          name = it.getString(0)!!,
                          imageUrl = it.getString(1)!!,
                          description = it.getString(2)!!,
                          dob = it.getString(3)!!)
                    }
            }
      if (user == null) {
        throw Exception("PowerSync failure did not fetch correctly")
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
            "INSERT INTO $USERS (name, imageUrl, description, dob) VALUES (?, ?, ?, ?);",
            user.asList())
      }
      Log.d(TAG, "createUserProfile: Success")
      onSuccess()
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
                        INSERT INTO $USERS (user_id, name, imageUrl, description, dob)
                        VALUES (?, ?, ?, ?, ?)
                        ON CONFLICT (user_id)
                        DO UPDATE SET name = ?, imageUrl = ?, description = ?, dob = ?;
                    """,
            listOf(
                currUser,
                userDto.name,
                userDto.imageUrl,
                userDto.description,
                userDto.dob,
                userDto.name,
                userDto.imageUrl,
                userDto.description,
                userDto.dob))
      }
      Log.d(TAG, "upsertUserProfile: Success")
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
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "deleteUserProfile: fail to delete user profile: ${e.message}")
      onFailure(e)
    }
  }
}
