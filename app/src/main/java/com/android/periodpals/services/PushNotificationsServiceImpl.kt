package com.android.periodpals.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.android.periodpals.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.flow.MutableStateFlow

private const val TAG = "PushNotificationsServiceImpl"
private const val CHANNEL_ID = "period_pals_channel_id"
private const val CHANNEL_NAME = "Period Pals Channel"
private const val CHANNEL_DESCRIPTION = "Channel for Period Pals notifications"

/**
 * Implementation of the PushNotificationsService interface. This class handles the push
 * notification permissions and integrates with Firebase Messaging Service.
 *
 * @property activity The activity context used for requesting permissions.
 */
class PushNotificationsServiceImpl(private val activity: ComponentActivity) :
    FirebaseMessagingService(), PushNotificationsService {

  private val firebase = FirebaseMessaging.getInstance()

  private var _pushPermissionsGranted = MutableStateFlow(false)
  val pushPermissionsGranted = _pushPermissionsGranted

  private val requestPermissionLauncher =
      activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        handlePermissionResult(it)
      }

  constructor() : this(ComponentActivity())

  init {
    createNotificationChannel()
  }

  /**
   * Called when a new token for the default Firebase project is generated. This is invoked after
   * app install when a token is first generated, and again if the token changes.
   *
   * @param token The new token.
   */
  override fun onNewToken(token: String) {
    super.onNewToken(token)
    // TODO: send the new token to the server
    Log.d(TAG, "Refreshed token: $token")
  }

  /**
   * Asks the user for permission to send push notifications. On API level < 33 (TIRAMISU), no
   * permission is needed.
   */
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
    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
  }

  /**
   * Called when a message is received.
   *
   * @param remoteMessage The message received from Firebase Cloud Messaging.
   */
  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)
    Log.d(TAG, "Message data: ${remoteMessage.data}")

    val title = remoteMessage.data["title"]
    val message = remoteMessage.data["message"]
    if (title == null || message == null) {
      Log.e(TAG, "Invalid push notification data")
      return
    }

    showNotification(title, message)
  }

  /**
   * Called when the FCM server deletes pending messages. This may be due to:
   * - Too many messages stored on the FCM server.
   * - The device hasn't connected in a long time and the app server has recently (within the last 4
   *   weeks) sent a message to the app on that device.
   */
  override fun onDeletedMessages() {
    super.onDeletedMessages()
    Log.d(TAG, "Device not registered for push notifications")
  }

  /**
   * Called when there is an error sending a message.
   *
   * @param msgId The message ID of the message that failed to send.
   * @param exception The exception that caused the error.
   */
  @Deprecated("Deprecated in Java")
  override fun onSendError(msgId: String, exception: Exception) {
    super.onSendError(msgId, exception)
    Log.e(TAG, "Error sending the notification: $msgId", exception)
  }

  /**
   * Creates a notification channel for push notifications. This is required for API level 26+
   * (Oreo) to display notifications.
   */
  private fun createNotificationChannel() {
    Log.d(TAG, "Creating notification channel")
    val channel =
        NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            .apply { description = CHANNEL_DESCRIPTION }

    val notificationManager: NotificationManager =
        activity.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)
  }

  /**
   * Handles the result of the notification permission request.
   *
   * @param isGranted True if the permission was granted, false otherwise.
   */
  private fun handlePermissionResult(isGranted: Boolean) {
    if (isGranted) {
      Log.d(TAG, "Notification permission granted")
      Toast.makeText(activity, "Notification permission granted", Toast.LENGTH_SHORT).show()
      _pushPermissionsGranted.value = true
      return
    }
    Log.d(TAG, "Notification permission denied")
    Toast.makeText(activity, "Notification permission denied", Toast.LENGTH_SHORT).show()
    _pushPermissionsGranted.value = false
  }

  /**
   * Displays a push notification with the given title and message.
   *
   * @param title The title of the notification.
   * @param message The message of the notification.
   */
  @SuppressLint("MissingPermission") // permission is checked in askPermission()
  private fun showNotification(title: String?, message: String?) {
    // TODO: notification layout RemoteViews

    val notificationBuilder =
        NotificationCompat.Builder(activity, CHANNEL_ID) // todo : channel id
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setContentTitle(title)
            .setContentText(message)
            // TODO: .setCustomContentView(notificationLayout)
            // .setCustomBigContentView(notificationLayoutBig) for expanded notification layout
            // .addAction() using PendingIntent if we want to add buttons to the notification
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    val notificationManager = NotificationManagerCompat.from(activity)
    if (_pushPermissionsGranted.value) {
      notificationManager.notify(1, notificationBuilder.build())
    }

    // TODO: display push notification
  }

  /**
   * Creates a new device notification token using Firebase Messaging.
   *
   * This function requests a new token from Firebase Messaging and logs the result. If the token is
   * successfully created, it is sent to the server for push notifications.
   *
   * The token is used to uniquely identify the device for sending push notifications.
   */
  fun createDeviceToken() {
    Log.d(TAG, "Creating device notification token")
    firebase.token.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val token = task.result
        Log.d(TAG, "Token created successfully: ${task.result}")
        // TODO: send token to server
      } else {
        Log.w(TAG, "Failed to get token")
      }
    }
  }
}
