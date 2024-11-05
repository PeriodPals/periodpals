package com.android.periodpals.model.alert

import java.time.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Alert(
    @SerialName("idAlert") val idAlert: String,
    @SerialName("idProfile") val idProfile: String,
    @SerialName("name") val name: String,
    @SerialName("product") val product: Product,
    @SerialName("urgency") val urgency: Urgency,
    @SerialName("createdAt") @Contextual val createdAt: LocalDateTime,
    @SerialName("location") val location: String, // TODO: Create data class Location
    @SerialName("message") val message: String,
    @SerialName("status") val status: Status
)

enum class Product {
  TAMPON,
  PAD
}

enum class Urgency {
  LOW,
  MEDIUM,
  HIGH,
}

enum class Status {
  CREATED, // The alert has been created and is waiting for someone to respond
  PENDING, // Someone has acknowledged the alert and is helping
  SOLVED, // The alert has been resolved, help was provided
}
