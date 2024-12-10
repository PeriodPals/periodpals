package com.android.periodpals.model.user

import android.util.Log
import com.powersync.PowerSyncDatabase
import com.powersync.connector.supabase.SupabaseConnector
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserInfo

private const val TAG = "UserModelPowerSync"
private const val USERS = "users"

class UserModelPowerSync(
    private val db: PowerSyncDatabase,
    private val supabase: SupabaseClient
) : UserRepository{

    override suspend fun loadUserProfile(
        onSuccess: (UserDto) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val currUser: String? = supabase.auth.currentUserOrNull()?.id
            var user: UserDto? = null

            if (currUser == null) {
                throw Exception("Supabase does not have a user logged in")
            }

            db.writeTransaction { tx ->
                user = tx.getOptional (
                    "SELECT name, imageUrl, description, dob from $USERS where user_id = ?",
                    listOf(currUser)
                ) {
                    UserDto(
                        name = it.getString(0)!!,
                        imageUrl = it.getString(1)!!,
                        description = it.getString(2)!!,
                        dob = it.getString(3)!!
                    )
                }
            }
            if (user == null) {
                throw Exception("PowerSync failure did not fetch correctly")
            }

            Log.d(TAG, "loadUserProfile: Success")
            onSuccess(user!!)
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
                    user.asList()
                )
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
                        INSERT INTO $USERS (name, imageUrl, description, dob)
                        VALUES (?, ?, ?, ?)
                        ON CONFLICT (id)
                    """,
                    // "UPSERT INTO $USERS (name, imageUrl, description, dob) VALUES (?, ?, ?, ?);",
                    userDto.asList()
                )
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
        // TODO: NEEDS REFACTORING
        try {
            db.writeTransaction { tx ->
                tx.execute(
                    "DELETE FROM $USERS WHERE id=?",
                    listOf(idUser)
                )
            }
            Log.d(TAG, "deleteUserProfile: Success")
            onSuccess()
        } catch (e: Exception) {
            Log.d(TAG, "deleteUserProfile: fail to delete user profile: ${e.message}")
            onFailure(e)
        }
    }

}