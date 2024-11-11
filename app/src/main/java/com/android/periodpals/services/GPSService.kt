package com.android.periodpals.services

/**
 * Interface to interact with the GPS in the device.
 *
 * Defines functions to ask for location access and to fetch the geographical
 * coordinates of the device.
 *
 */
interface GPSService {

  /**
   * Verifies whether access to the location of the device has already been granted. If granted, it
   * starts the fetching the device location. Otherwise, it first launches the system dialog asking
   * the user to grant location access and then starts the updates.
   */
  fun askPermissionAndStartUpdates()

  /**
   * Switches from precise to approximate location updates.
   *
   * - **Precise** updates demand more power.
   * - **Approximate** updates demand less power.
   *
   * Before switching, it verifies that permission is granted, the app is already fetching the device
   * location and that the current mode up is precise.
   */
  fun switchFromPreciseToApproximate()

  /**
   * Switches from approximate to precise location updates.
   *
   * - **Precise** updates demand more power.
   * - **Approximate** updates demand less power.
   *
   * Before switching, it verifies that permission is granted, the app is already fetching the device
   * location and that the current mode up is approximate.
   */
  fun switchFromApproximateToPrecise()


  /**
   * Stops fetching the user location and cleans up the resources.
   */
  fun cleanup()
}
