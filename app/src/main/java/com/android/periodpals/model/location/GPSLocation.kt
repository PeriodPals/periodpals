package com.android.periodpals.model.location

import org.osmdroid.util.GeoPoint

data class GPSLocation(val lat: Double, val long: Double) {

    /**
     * Transform this location into a [GeoPoint].
     *
     * @return The respective [GeoPoint].
     */
    fun toGeoPoint() = GeoPoint(lat, long)

    companion object {
        val DEFAULT_LOCATION = GPSLocation(46.5191, 6.5668)
    }
}