package com.android.periodpals.model.user

import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.LocationGIS
import com.android.periodpals.model.location.parseLocationGIS

/**
 * Data class representing a user.
 *
 * @property name The display name of the user.
 * @property imageUrl The URL of the user's profile image.
 * @property description A brief description of the user.
 * @property dob The date of birth of the user.
 * @property fcmToken The Firebase Cloud Messaging token for the user (optional).
 * @property locationGIS The geographic location of the user. Default is the default location.
 */
data class User(
    val name: String,
    val imageUrl: String,
    val description: String,
    val dob: String,
    val fcmToken: String? = null,
    val locationGIS: LocationGIS = parseLocationGIS(Location.DEFAULT_LOCATION),
) {
  /**
   * Converts the User object to a UserDto object.
   *
   * @return A UserDto object containing the user's data.
   */
  fun asUserDto(): UserDto {
    return UserDto(
        name = this.name,
        imageUrl = this.imageUrl,
        description = this.description,
        dob = this.dob,
        fcm_token = this.fcmToken,
        locationGIS = this.locationGIS,
    )
  }
}
