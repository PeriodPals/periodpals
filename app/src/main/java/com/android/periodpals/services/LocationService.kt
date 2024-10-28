package com.android.periodpals.services

import kotlinx.coroutines.flow.StateFlow

/**
 * A service that provides the user location.
 */
interface LocationService {

  fun requestUserPermissionForLocation()
}
