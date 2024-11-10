package com.android.periodpals.services

/** A service that asks for location access and updates the user's location. */
interface GPSService {

  fun cleanup()

  fun switchToApproximate()

  fun switchToPrecise()

  fun askPermissionAndStartUpdates()
}
