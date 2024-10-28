package com.android.periodpals.services

import kotlinx.coroutines.flow.StateFlow

/**
 * A service that provides the user location.
 */
interface LocationService {

  /**
   * Requests the user for permission to access their location.
   */
  fun requestUserPermissionForLocation()
}
