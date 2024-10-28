package com.android.periodpals.model.user

/**
 * Data class representing a user.
 *
 * @property displayName The display name of the user.
 * @property email The email address of the user.
 * @property imageUrl The URL of the user's profile image.
 * @property description A brief description of the user.
 * @property age The age of the user.
 */
data class User(
    val displayName: String,
    val email: String,
    val imageUrl: String,
    val description: String,
    val age: String
)
