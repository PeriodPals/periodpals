package com.android.periodpals.model.user

/**
 * Data class representing a user.
 *
 * @property name The display name of the user.
 * @property imageUrl The URL of the user's profile image.
 * @property description A brief description of the user.
 * @property dob The date of birth of the user.
 * @property fcmToken The Firebase Cloud Messaging token for the user (optional).
 */
data class User(
    val name: String,
    val imageUrl: String,
    val description: String,
    val dob: String,
    val fcmToken: String? = null,
) {
  inline fun asUserDto(): UserDto {
    return UserDto(
        name = this.name,
        imageUrl = this.imageUrl,
        description = this.description,
        dob = this.dob,
        fcm_token = this.fcmToken,
    )
  }
}
