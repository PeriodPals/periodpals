package com.android.periodpals.model.location

import org.osmdroid.util.GeoPoint

/**
 * Represents a geographic location associated to a name.
 *
 * @property latitude the latitude of the location
 * @property longitude the longitude of the location
 */
data class Location(val latitude: Double, val longitude: Double, val name: String) {
  companion object {
    val DEFAULT_LOCATION = Location(46.9484, 7.4521, "Bern")
    const val CURRENT_LOCATION_NAME = "Current location"
  }

  /**
   * Transform this location into a [GeoPoint].
   *
   * @return The respective [GeoPoint].
   */
  fun toGeoPoint() = GeoPoint(latitude, longitude)
}
