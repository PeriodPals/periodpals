package com.android.periodpals.model.alert

import com.android.periodpals.R

/**
 * Data class representing an alert.
 *
 * @property id The unique identifier of the alert, given when created in Supabase.
 * @property uid The user ID associated with the alert
 * @property name The name of the user who created the Alert.
 * @property product The product associated with the alert.
 * @property urgency The urgency level of the alert.
 * @property createdAt The date and time when the alert was created, normally initialized in
 *   Supabase, otherwise declare it as : LocalDateTime(2022, 1, 1, 0, 0).toString()
 * @property location The location of the alert.
 * @property message The message associated with the alert.
 * @property status The current status of the alert.
 */
data class Alert(
    val id: String?, // given when created in supabase
    val uid: String,
    val name: String,
    val product: Product,
    val urgency: Urgency,
    val createdAt: String?,
    val location: String, // TODO: Create data class Location
    val message: String,
    val status: Status
)

/** Enum class representing the product requested with the alert. */
enum class Product {
  TAMPON,
  PAD,
  NO_PREFERENCE,
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

/** Data class representing an [icon] and [textId] for the product and urgency level */
data class PeriodPalsIcon(val icon: Int, val textId: String)

val LIST_OF_PRODUCTS =
    listOf(
        PeriodPalsIcon(R.drawable.tampon, "Tampon"),
        PeriodPalsIcon(R.drawable.pad, "Pad"),
        PeriodPalsIcon(R.drawable.tampon_and_pad, "No Preference"),
    )
val LIST_OF_URGENCIES =
    listOf(
        PeriodPalsIcon(R.drawable.urgency_1, "Low"),
        PeriodPalsIcon(R.drawable.urgency_2, "Medium"),
        PeriodPalsIcon(R.drawable.urgency_3, "High"),
    )
