package com.android.periodpals.model.location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) representing a user's location.
 *
 * @property uid The unique identifier of the user.
 * @property location The geographical location of the user.
 */
@Serializable
data class UserLocationDto(
    @SerialName("uid") val uid: String,
    @SerialName("locationGIS") val location: LocationGIS,
)
