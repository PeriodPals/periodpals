package com.android.periodpals.model.alert

import com.android.periodpals.R
import java.time.LocalDateTime
import java.util.UUID

/**
 * Data class representing an alert.
 *
 * @property id The unique identifier of the alert, generated when alert is created.
 * @property uid The user ID associated with the alert
 * @property name The name of the user who created the Alert.
 * @property product The product associated with the alert.
 * @property urgency The urgency level of the alert.
 * @property createdAt The date and time when the alert was created, generated when alert is
 *   created.
 * @property location The location of the alert.
 * @property locationGIS The location of the alert in PostGIS-compatible POINT format, directly
 *   computed when alert is created.
 * @property message The message associated with the alert.
 * @property status The current status of the alert.
 */
data class Alert(
    val id: String = UUID.randomUUID().toString(),
    val uid: String,
    val name: String,
    val product: Product,
    val urgency: Urgency,
    val createdAt: String = LocalDateTime.now().toString(),
    val location: String,
    val locationGIS: String = parseLocationGIS(location),
    val message: String,
    val status: Status
) {
  companion object {
    /**
     * Parses a location string in "latitude,longitude,name" format into a PostGIS-compatible POINT.
     *
     * @param location The location string to be parsed.
     * @return A PostGIS-compatible POINT string (e.g., "POINT(longitude latitude)").
     */
    fun parseLocationGIS(location: String): String {
      val parts = location.split(",")
      if (parts.size < 2) {
        throw IllegalArgumentException(
            "Invalid location format. Expected 'latitude,longitude,name'.")
      }

      val latitude =
          parts[0].toDoubleOrNull() ?: throw IllegalArgumentException("Invalid latitude value.")
      val longitude =
          parts[1].toDoubleOrNull() ?: throw IllegalArgumentException("Invalid longitude value.")

      // Return the location in PostGIS-compatible POINT format
      return "POINT($longitude $latitude)"
    }
  }
}

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
        PeriodPalsIcon(R.drawable.urgency_3, "High"),
        PeriodPalsIcon(R.drawable.urgency_2, "Medium"),
        PeriodPalsIcon(R.drawable.urgency_1, "Low"),
    )

/**
 * Converts a text representation of a product to a `Product` enum.
 *
 * @param productText The text representation of the product.
 * @return The corresponding `Product` enum, or `null` if the text does not match any product.
 */
fun stringToProduct(productText: String): Product? {
  return when (productText) {
    LIST_OF_PRODUCTS[0].textId -> Product.TAMPON
    LIST_OF_PRODUCTS[1].textId -> Product.PAD
    LIST_OF_PRODUCTS[2].textId -> Product.NO_PREFERENCE
    else -> null
  }
}

/**
 * Converts a text representation of urgency to an `Urgency` enum.
 *
 * @param urgencyText The text representation of the urgency.
 * @return The corresponding `Urgency` enum, or `null` if the text does not match any urgency level.
 */
fun stringToUrgency(urgencyText: String): Urgency? {
  return when (urgencyText) {
    LIST_OF_URGENCIES[0].textId -> Urgency.HIGH
    LIST_OF_URGENCIES[1].textId -> Urgency.MEDIUM
    LIST_OF_URGENCIES[2].textId -> Urgency.LOW
    else -> null
  }
}

/**
 * Extracts the PeriodPalsIcon from the Alert product enum.
 *
 * @param product The product associated with the alert.
 * @return The corresponding product of type PeriodPalsIcon.
 */
fun productToPeriodPalsIcon(product: Product): PeriodPalsIcon =
    when (product) {
      Product.TAMPON -> LIST_OF_PRODUCTS[0]
      Product.PAD -> LIST_OF_PRODUCTS[1]
      Product.NO_PREFERENCE -> LIST_OF_PRODUCTS[2]
    }

/**
 * Extracts the PeriodPalsIcon from the Alert urgency enum.
 *
 * @param urgency The urgency associated with the alert.
 * @return The corresponding urgency of type PeriodPalsIcon.
 */
fun urgencyToPeriodPalsIcon(urgency: Urgency): PeriodPalsIcon =
    when (urgency) {
      Urgency.HIGH -> LIST_OF_URGENCIES[0]
      Urgency.MEDIUM -> LIST_OF_URGENCIES[1]
      Urgency.LOW -> LIST_OF_URGENCIES[2]
    }
