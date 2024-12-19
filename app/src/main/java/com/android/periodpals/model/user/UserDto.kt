package com.android.periodpals.model.user

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for user data.
 *
 * @property name The display name of the user.
 * @property imageUrl The URL of the user's profile image.
 * @property description A brief description of the user.
 * @property dob The age of the user.
 * @property preferred_distance The preferred radius distance for receiving alerts.
 * @property fcm_token The Firebase Cloud Messaging token for the user (optional).
 */
@Serializable
data class UserDto(
  val name: String,
  val imageUrl: String,
  val description: String,
  val dob: String,
  val preferred_distance: Int,
  val fcm_token: String? = null,
) {
  /**
   * Converts this UserDto to a User object.
   *
   * @return A User object with the same properties as this UserDto.
   */
  fun asUser(): User {
    return User(
      name = this.name,
      imageUrl = this.imageUrl,
      description = this.description,
      dob = this.dob,
      preferredDistance = this.preferred_distance,
      fcmToken = this.fcm_token,
    )
  }
}
