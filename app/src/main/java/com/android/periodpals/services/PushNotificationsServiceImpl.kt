package com.android.periodpals.services

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.flow.MutableStateFlow

private const val TAG = "PushNotificationsServiceImpl"

/**
 * Implementation of the PushNotificationsService interface. This class handles the push
 * notification permissions and integrates with Firebase Messaging Service.
 *
 * @property activity The activity context used for requesting permissions.
 */
class PushNotificationsServiceImpl(private val activity: ComponentActivity) :
    FirebaseMessagingService(), PushNotificationsService {

  private var _pushPermissionsGranted = MutableStateFlow(false)
  val pushPermissionsGranted = _pushPermissionsGranted

  constructor() : this(ComponentActivity())

  override fun onNewToken(token: String) {
    // TODO: send the new token to the server
    Log.d(TAG, "Refreshed token: $token")
  }

  // needs to be a `(ComponentActivity) -> ActivityResultLauncher<String>` so that it won't
  // initialise if it's not being called (lazy)
  private val requestPermissionLauncher = { activity: ComponentActivity ->
    Log.d(TAG, "Creating permission launcher")
    activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
      if (isGranted) {
        Log.d(TAG, "Notification permission granted")
        Toast.makeText(activity, "Notification permission granted", Toast.LENGTH_SHORT).show()
        _pushPermissionsGranted.value = true
      } else {
        Log.d(TAG, "Notification permission denied")
        Toast.makeText(activity, "Notification permission denied", Toast.LENGTH_SHORT).show()
        _pushPermissionsGranted.value = false
      }
    }
  }

  /** Asks the user for permission to send push notifications. */
  override fun askPermission() {
    // no need to ask for permission on API level < 33 (TIRAMISU)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
      Log.d(TAG, "Notification permission not needed on API level < 33")
      _pushPermissionsGranted.value = true
      return
    }
    Log.d(TAG, "Checking notification permission")

    _pushPermissionsGranted.value =
        ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    if (_pushPermissionsGranted.value) {
      Log.d(TAG, "Notification permission already granted")
      return
    }

    // TODO: if the user had previously denied the permission, show a dialog explaining why the
    //       permission is needed using shouldShowRequestPermissionRationale(activity,
    //       Manifest.permission.POST_NOTIFICATIONS)

    Log.d(TAG, "Requesting notification permission")
    requestPermissionLauncher(activity).launch(Manifest.permission.POST_NOTIFICATIONS)
  }
}
