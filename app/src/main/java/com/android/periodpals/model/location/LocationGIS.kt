package com.android.periodpals.model.location

import kotlinx.serialization.Serializable

/**
 * Data class representing a geographic location in GeoJSON format (needed to send to the database).
 *
 * @property type The type of the GeoJSON object, typically "Point".
 * @property coordinates A list containing the longitude and latitude of the location.
 */
@Serializable
data class LocationGIS(
    val type: String,
    val coordinates: List<Double>, // Handles JSON structure returned by the database
)

/**
 * Parses a location string (parameter location from an Alert) in "latitude,longitude,name" format
 * into a PostGIS-compatible POINT.
 *
 * @param location The location string to be parsed.
 * @return A PostGIS-compatible POINT string (e.g., "POINT(longitude latitude)").
 */
fun parseLocationGIS(location: String): LocationGIS {
  val locationValue = Location.fromString(location)
  return LocationGIS("Point", listOf(locationValue.longitude, locationValue.latitude))
}

/**
 * Parses a location object (parameter location from an Alert) into a PostGIS-compatible POINT.
 *
 * @param location The location object to be parsed.
 * @return A PostGIS-compatible POINT string (e.g., "POINT(longitude latitude)").
 */
fun parseLocationGIS(location: Location): LocationGIS {
    return LocationGIS("Point", listOf(location.longitude, location.latitude))
}
