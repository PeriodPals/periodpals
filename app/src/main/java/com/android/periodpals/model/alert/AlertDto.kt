package com.android.periodpals.model.alert

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
    @SerialName("locationGIS")
    val locationGIS: LocationGIS? = null, // Handle JSON object for locationGIS
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
      locationGIS = alert.locationGIS?.let { parseLocationGIS(it) },
      message = alert.message,
      status = alert.status)

  /**
   * Converts this `AlertDto` to an `Alert` object.
   *
   * @return The `Alert` object created from this `AlertDto`.
   */
  fun toAlert(): Alert {
    val gisString =
        locationGIS?.let {
          "POINT(${it.coordinates[0]} ${it.coordinates[1]})" // Convert JSON to PostGIS-compatible
                                                             // string
        }
    return Alert(
        id = id,
        uid = uid,
        name = name,
        product = product,
        urgency = urgency,
        createdAt = createdAt,
        location = location,
        locationGIS = gisString, // Use PostGIS-compatible string
        message = message,
        status = status)
  }

  companion object {
    fun parseLocationGIS(gisString: String): LocationGIS {
      val regex = """POINT\((\S+)\s+(\S+)\)""".toRegex()
      val matchResult =
          regex.matchEntire(gisString)
              ?: throw IllegalArgumentException("Invalid POINT format: $gisString")
      val (longitude, latitude) = matchResult.destructured
      return LocationGIS("Point", listOf(longitude.toDouble(), latitude.toDouble()))
    }
  }
}

/**
 * Data class representing the GIS location.
 *
 * @property type The type of the GIS object, typically "Point".
 * @property coordinates The coordinates of the GIS object, where the first element is longitude and
 *   the second is latitude.
 */
@Serializable
data class LocationGIS(
    val type: String,
    val coordinates: List<Double> // Handles JSON structure returned by the database
)
