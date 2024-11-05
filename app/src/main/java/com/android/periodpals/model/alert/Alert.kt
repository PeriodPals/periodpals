package com.android.periodpals.model.alert

import java.time.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data class representing an alert.
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
data class Alert(
    @SerialName("id") val id: String,
    @SerialName("uid") val uid: String,
    @SerialName("name") val name: String,
    @SerialName("product") val product: Product,
    @SerialName("urgency") val urgency: Urgency,
    @SerialName("createdAt") @Contextual val createdAt: LocalDateTime,
    @SerialName("location") val location: String, // TODO: Create data class Location
    @SerialName("message") val message: String,
    @SerialName("status") val status: Status
)

/** Enum class representing the product requested with the alert. */
enum class Product {
  TAMPON,
  PAD
}

/** Enum class representing the urgency level of the alert. */
enum class Urgency {
  LOW,
  MEDIUM,
  HIGH,
}

/** Enum class representing the current status of the alert. */
enum class Status {
  CREATED, // The alert has been created and is waiting for someone to respond
  PENDING, // Someone has acknowledged the alert and is helping
  SOLVED, // The alert has been resolved, help was provided
}
