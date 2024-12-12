package com.android.periodpals.model.user

import com.android.periodpals.model.location.LocationGIS
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Data Transfer Object (DTO) for user data.
 *
 * @property name The display name of the user.
 * @property imageUrl The URL of the user's profile image.
 * @property description A brief description of the user.
 * @property dob The age of the user.
 * @property fcm_token The Firebase Cloud Messaging token for the user (optional).
 * @property locationGIS The geographic location of the user.
 */
@Serializable
data class UserDto(
    val name: String,
    val imageUrl: String,
    val description: String,
    val dob: String,
    val fcm_token: String? = null,
    val locationGIS: LocationGIS,
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
        fcmToken = this.fcm_token,
        locationGIS = this.locationGIS,
    )
  }

  inline fun asList(): List<Any?> =
      listOf(name, imageUrl, description, dob, fcm_token, Json.encodeToString(locationGIS))
}
