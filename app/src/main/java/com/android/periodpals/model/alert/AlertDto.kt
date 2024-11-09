package com.android.periodpals.model.alert

import java.time.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for alert data.
 *
 * @property id The unique identifier of the alert.
 * @property uid The user ID associated with the alert.
 * @property name The name of the alert.
 * @property product The product associated with the alert.
 * @property urgency The urgency level of the alert.
 * @property createdAt The date and time when the alert was created.
 * @property location The location of the alert.
 * @property message The message associated with the alert.
 * @property status The current status of the alert.
 */
@Serializable
data class AlertDto(
    @SerialName("id") val id: String? = " ", // given when created in supabase
    @SerialName("uid") val uid: String,
    @SerialName("name") val name: String,
    @SerialName("product") val product: Product,
    @SerialName("urgency") val urgency: Urgency,
    @SerialName("createdAt") @Contextual val createdAt: LocalDateTime,
    @SerialName("location") val location: String, // TODO: Create data class Location
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
        message = message,
        status = status)
  }
}
