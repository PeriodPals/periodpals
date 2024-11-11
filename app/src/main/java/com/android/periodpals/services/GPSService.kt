package com.android.periodpals.services

interface GPSService {

  fun cleanup()

  fun switchToApproximate()

  fun switchToPrecise()

  fun askPermissionAndStartUpdates()
}
