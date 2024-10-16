package com.android.periodpals.model.profile

import androidx.lifecycle.LiveData

interface ProfileRepository {

    suspend fun getProfiles(): List<ProfileDto>?

    suspend fun loadUserProfile(id : String?) : ProfileDto

    suspend fun createUserProfile(profile: Profile) : Boolean

    suspend fun updateUserProfile(id : String?, name : String, email : String, avatarUrl : String, description : String)
}