package com.android.periodpals.services

/** A service that provides the user location. */
interface GPSService {

  /** Requests the user for permission to access their location. */
  fun requestUserPermissionForLocation()

  /** Updates the location of the user */
  fun refreshCurrentLocation()
}
