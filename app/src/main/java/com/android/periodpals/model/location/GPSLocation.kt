package com.android.periodpals.model.location

import org.osmdroid.util.GeoPoint

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
   * Default location to be used when the user has not granted permission to access their location.
   */
  companion object {
    val DEFAULT_LOCATION = GPSLocation(46.9484, 7.4521) // Bern
  }
}
