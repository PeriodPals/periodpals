package com.android.periodpals.model.alert

import com.android.periodpals.model.location.LocationGIS
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for alert data.
 *
 * @property id The unique identifier of the alert, generated when alert is created in [Alert].
 * @property uid The user ID associated with the alert.
 * @property name The name of the user who created the alert.
 * @property product The product associated with the alert.
 * @property urgency The urgency level of the alert.
 * @property createdAt The date and time when the alert was created, generated when alert is created
 *   in [Alert].
 * @property location The location of the alert.
 * @property locationGIS The location of the alert in PostGIS-compatible POINT format.
 * @property message The message associated with the alert.
 * @property status The current status of the alert.
 */
@Serializable
data class AlertDto(
    @SerialName("id") val id: String,
    @SerialName("uid") val uid: String,
    @SerialName("name") val name: String,
    @SerialName("product") val product: Product,
    @SerialName("urgency") val urgency: Urgency,
    @SerialName("createdAt") val createdAt: String,
    @SerialName("location") val location: String,
    @SerialName("locationGIS") val locationGIS: LocationGIS,
    @SerialName("message") val message: String,
    @SerialName("status") val status: Status
) {
  /**
   * Constructs an `AlertDto` from an `Alert` object.
   *
   * @param alert The `Alert` object to be converted into an `AlertDto`.
   */
  constructor(
      alert: Alert
  ) : this(
      id = alert.id,
      uid = alert.uid,
      name = alert.name,
      product = alert.product,
      urgency = alert.urgency,
      createdAt = alert.createdAt,
      location = alert.location,
      locationGIS = alert.locationGIS,
      message = alert.message,
      status = alert.status)

  /**
   * Converts this `AlertDto` to an `Alert` object.
   *
   * @return The `Alert` object created from this `AlertDto`.
   */
  fun toAlert(): Alert {
    return Alert(
        id = id,
        uid = uid,
        name = name,
        product = product,
        urgency = urgency,
        createdAt = createdAt,
        location = location,
        locationGIS = locationGIS,
        message = message,
        status = status)
  }
}
