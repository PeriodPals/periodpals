package com.android.periodpals.model.location

import org.osmdroid.util.GeoPoint

private const val PARTS_ERROR_MESSAGE = "Invalid format. Expected 'lat,long'."
private const val PARSE_ERROR_MESSAGE = "Invalid numeric values for latitude and longitude"
private const val STRING_DELIMITER = ","

/**
 * Represents a geographic location defined by latitude and longitude coordinates
 *
 * @property lat the latitude of the location
 * @property long the longitude of the location
 *
 * The class also contains a method to convert the location into a [GeoPoint] used by `OSMDroid`.
 */
data class GPSLocation(val lat: Double, val long: Double) {

  /**
   * Transform this location into a [GeoPoint].
   *
   * @return The respective [GeoPoint].
   */
  fun toGeoPoint() = GeoPoint(lat, long)

  /**
   * Transform this location into a [Location]
   *
   * @return The respective [Location]
   */
  fun toLocation() = Location(lat, long, CURRENT_LOCATION_NAME)

  /**
   * Converts the GPSLocation object to a String in "lat,long" format.
   *
   * @return A String representation of the GPSLocation object.
   */
  override fun toString(): String = "$lat,$long"

  /**
   * Converts a String in "lat,long" format back to a GPSLocation object.
   *
   * @param value The [String] representation of the [GPSLocation] object.
   * @return The [GPSLocation] parsed from the string.
   * @throws IllegalArgumentException if the string format or the numeric values for the coordinates
   *   are invalid
   */
  fun fromString(value: String): GPSLocation {
    val parts = value.split(STRING_DELIMITER)
    if (parts.size != 2) {
      throw IllegalArgumentException(PARTS_ERROR_MESSAGE)
    }
    val lat = parts[0].toDoubleOrNull()
    val long = parts[1].toDoubleOrNull()

    if (lat == null || long == null) {
      throw IllegalArgumentException(PARSE_ERROR_MESSAGE)
    }

    return GPSLocation(lat, long)
  }

  /**
   * Default location to be used when the user has not granted permission to access their location.
   */
  companion object {
    val DEFAULT_LOCATION = GPSLocation(46.9484, 7.4521) // Bern
    const val CURRENT_LOCATION_NAME = "Current location"
  }
}
