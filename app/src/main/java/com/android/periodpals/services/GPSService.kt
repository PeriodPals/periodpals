package com.android.periodpals.services

/**
 * Interface to interact with the GPS in the device.
 *
 * Defines functions to ask for location access and to fetch the geographical coordinates of the
 * device.
 */
interface GPSService {

  /**
   * Verifies whether access to the location of the device has already been granted.
   * - If granted, it starts the fetching the device location.
   * - Otherwise, it first launches the system permission dialog then starts the updates if
   *   permission is granted. Moreover, it also verifies that the location is not being already
   *   tracked.
   */
  fun askPermissionAndStartUpdates()

  /**
   * Switches from precise to approximate location updates.
   * - **Precise** updates demand more power.
   * - **Approximate** updates demand less power.
   *
   * Calling this function will have no effect if permissions are not granted or if no location
   * updates are being received.
   */
  fun switchFromPreciseToApproximate()

  /**
   * Switches from approximate to precise location updates.
   * - **Precise** updates demand more power.
   * - **Approximate** updates demand less power.
   *
   * Calling this function will have no effect if permissions are not granted or if no location
   * updates are being received.
   */
  fun switchFromApproximateToPrecise()

  /** Stops fetching the user location and cleans up the resources. */
  fun cleanup()
}
