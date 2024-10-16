package com.android.periodpals.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto (

    @SerialName("id")
    val id: String?,

    @SerialName("name")
    val name: String,

    @SerialName("email")
    val email: String,

    @SerialName("avatar_url")
    val avatarUrl: String,

    @SerialName("description")
    val description: String
)