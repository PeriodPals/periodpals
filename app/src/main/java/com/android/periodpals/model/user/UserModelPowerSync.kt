package com.android.periodpals.model.user

import android.util.Log
import com.powersync.PowerSyncDatabase
import com.powersync.connector.supabase.SupabaseConnector

private const val TAG = "UserModelPowerSync"
private const val USERS = "users"

class UserModelPowerSync(
    private val db: PowerSyncDatabase
) : UserRepository{

    override suspend fun loadUserProfile(
        onSuccess: (UserDto) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
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
            db.writeTransaction { tx ->
                tx.execute(
                    "UPSERT INTO $USERS (name, imageUrl, description, dob) VALUES (?, ?, ?, ?);",
                    userDto.asList()
                )
            }
            Log.d(TAG, "upsertUserProfile: Success")
            onSuccess(userDto)
        } catch (e: Exception) {
            Log.d(TAG, "upsertUserProfile: fail to create user profile: ${e.message}")
            onFailure(e)
        }
        TODO("Not yet implemented")
    }

    override suspend fun deleteUserProfile(
        idUser: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }

}