package com.android.periodpals.model.alert

import com.android.periodpals.R
import kotlinx.serialization.Serializable

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
    val product: PeriodPalsIcon,
    val urgency: PeriodPalsIcon,
    val createdAt: String?,
    val location: String, // TODO: Create data class Location
    val message: String,
    val status: Status
)

/** Data class representing the icon of an alert. */
@Serializable data class PeriodPalsIcon(val id: Int, val textId: String)

/** Object containing the products that can be requested in an alert. */
object Products {
  val TAMPON = PeriodPalsIcon(id = R.drawable.tampon, textId = "Tampon")
  val PAD = PeriodPalsIcon(id = R.drawable.pad, textId = "Pad")
  val NO_PREF = PeriodPalsIcon(id = R.drawable.tampon_and_pad, textId = "No Preference")
}

/** List of products that can be requested in an alert. */
val LIST_OF_PRODUCTS = listOf(Products.TAMPON, Products.PAD, Products.NO_PREF)

/** Object containing the urgency levels that can be set in an alert. */
object Urgencies {
  val LOW = PeriodPalsIcon(id = R.drawable.urgency_1, textId = "Low")
  val MEDIUM = PeriodPalsIcon(id = R.drawable.urgency_2, textId = "Medium")
  val HIGH = PeriodPalsIcon(id = R.drawable.urgency_3, textId = "High")
}

val LIST_OF_URGENCIES = listOf(Urgencies.LOW, Urgencies.MEDIUM, Urgencies.HIGH)

val ORGANIC = PeriodPalsIcon(id = R.drawable.cotton, textId = "Organic")

/** Enum class representing the current status of the alert. */
enum class Status {
  CREATED, // The alert has been created and is waiting for someone to respond
  PENDING, // Someone has acknowledged the alert and is helping
  SOLVED, // The alert has been resolved, help was provided
}
