package com.android.periodpals.model.alert

import java.time.LocalDateTime
import kotlinx.serialization.Contextual

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
data class Alert(
    val id: String?, // given when created in supabase
    val uid: String?,
    val name: String,
    val product: Product,
    val urgency: Urgency,
    @Contextual val createdAt: LocalDateTime,
    val location: String, // TODO: Create data class Location
    val message: String,
    val status: Status
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
