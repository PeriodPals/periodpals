package com.android.periodpals.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val displayName: String,
    val email: String,
    val imageUrl: String,
    val description: String,
    val age: String
)
