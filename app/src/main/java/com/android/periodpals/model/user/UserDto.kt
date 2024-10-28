package com.android.periodpals.model.user

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for user data.
 *
 * @property displayName The display name of the user.
 * @property email The email address of the user.
 * @property imageUrl The URL of the user's profile image.
 * @property description A brief description of the user.
 * @property age The age of the user.
 */
@Serializable
data class UserDto(
    val displayName: String,
    val email: String,
    val imageUrl: String,
    val description: String,
    val age: String
)
