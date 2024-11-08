package com.android.periodpals.services

/** A service that asks for location access and updates the user's location. */
interface GPSService {

  /** Start updating the location of the user . The location is accessible through
   * [GPSServiceImpl.location]. */
  fun startUserLocation()

  /** Stop updating the location of the user. */
  fun stopUserLocation()

  /** Launches system dialog asking the user for access to their location (if not already done so) */
  fun askUserForLocationPermission()
}