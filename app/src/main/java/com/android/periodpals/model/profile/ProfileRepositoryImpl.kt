package com.android.periodpals.model.profile

import com.russhwolf.settings.BuildConfig
import io.github.jan.supabase.auth.status.SessionSource
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject


class ProfileRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: SessionSource.Storage,
) : ProfileRepository {

    override suspend fun getProfiles(): List<ProfileDto>? {
        return withContext(Dispatchers.IO) {
            val result = postgrest.from("profiles")
                .select().decodeList<ProfileDto>()
            result
        }
    }

    override suspend fun loadUserProfile(id: String): ProfileDto {
        return withContext(Dispatchers.IO) {
            postgrest.from("profiles").select {
                filter {
                    eq("id", id)
                }
            }.decodeSingle<ProfileDto>()
        }
    }

    override suspend fun createUserProfile(profile: Profile): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val profiletDto = ProfileDto(
                    id = profile.id,
                    name = profile.name,
                    email = profile.email,
                    avatarUrl = profile.avatarUrl,
                    description = profile.description
                )
                postgrest.from("profiles").insert(profiletDto)
                true
            }
            true
        } catch (e: java.lang.Exception) {
            throw e
        }
    }

    override suspend fun updateUserProfile(
        id: String,
        name: String,
        email: String,
        avatarUrl: String,
        description: String
    ) {
        withContext(Dispatchers.IO) {
            if (avatarUrl.isNotEmpty()) {
                postgrest.from("products").update({
                    set("name", name)
                    set("description", description)
                }) {
                    filter {
                        eq("id", id)
                    }
                }
            }
        }
    }
}