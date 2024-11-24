package com.android.periodpals.services

/**
 * Interface for the push notifications service. Provides a method to ask for notification
 * permissions.
 */
interface PushNotificationsService {

  /** Asks the user for permission to send push notifications. */
  fun askPermission()
}
