package com.android.periodpals.model.location

import org.osmdroid.util.GeoPoint

private const val PARTS_ERROR_MESSAGE = "Invalid format. Expected 'lat,long'."
private const val PARSE_ERROR_MESSAGE = "Invalid numeric values for latitude and longitude"
private const val STRING_DELIMITER = ","
private const val COMMA_URL_ENCODING = "%2C"

/**
 * Represents a geographic location associated to a name.
 *
 * @property latitude the latitude of the location
 * @property longitude the longitude of the location
 */
data class Location(val latitude: Double, val longitude: Double, val name: String) {

  /**
   * Converts the GPSLocation object to a String in "latitude,longitude, name" format. Escapes
   * commas in the [name].
   *
   * @return A String representation of the GPSLocation object.
   */
  override fun toString(): String {
    // Replace comma with its URL encoding counterpart
    val escapedName = name.replace(",", COMMA_URL_ENCODING)
    return "$latitude,$longitude,$escapedName"
  }

  /**
   * Transform this location into a [GeoPoint].
   *
   * @return The respective [GeoPoint].
   */
  fun toGeoPoint() = GeoPoint(latitude, longitude)

  companion object {
    val DEFAULT_LOCATION = Location(46.9484, 7.4521, "Bern")
    const val CURRENT_LOCATION_NAME = "Current location"

    /**
     * Converts a String in "latitude,longitude,name" format back to a GPSLocation object.
     *
     * @param value The [String] representation of the [Location] object.
     * @return The [Location] parsed from the string.
     * @throws IllegalArgumentException if the string format or the numeric values for the
     *   coordinates are invalid
     */
    fun fromString(value: String): Location {
      val parts = value.split(STRING_DELIMITER)
      if (parts.size != 3) {
        throw IllegalArgumentException(PARTS_ERROR_MESSAGE)
      }
      val lat = parts[0].toDoubleOrNull()
      val long = parts[1].toDoubleOrNull()
      // Replace URL-encoded comma with the ASCII comma
      val nameString = parts[2].replace(COMMA_URL_ENCODING, ",")

      if (lat == null || long == null || nameString.isBlank()) {
        throw IllegalArgumentException(PARSE_ERROR_MESSAGE)
      }

      return Location(lat, long, nameString)
    }
  }
}
