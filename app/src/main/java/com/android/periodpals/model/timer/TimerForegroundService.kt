package com.android.periodpals.model.timer

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat

/**
 * Service that runs a timer in the foreground and displays a notification with the elapsed time.
 * Timer still runs even if the app is closed.
 */
class TimerService : Service() {

  private var startTime: Long = 0L
  private var isRunning: Boolean = false
  private val handler = Handler(Looper.getMainLooper())
  private lateinit var notificationManager: NotificationManager

  /** Creates the notification channel for the timer service. */
  override fun onCreate() {
    super.onCreate()
    notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    createNotificationChannel()
  }

  /**
   * Starts or stops the timer based on the intent action.
   *
   * @param intent The intent used to start or stop the timer.
   * @param flags Additional data about this start request.
   */
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (intent?.action == "START_TIMER") {
      startTime = intent.getLongExtra("start_time", System.currentTimeMillis())
      startTimer()
    } else if (intent?.action == "STOP_TIMER") {
      stopTimer()
    }
    return START_STICKY
  }

  /** Starts the timer and updates the notification with the elapsed time. */
  private fun startTimer() {
    isRunning = true
    handler.post(
        object : Runnable {
          override fun run() {
            if (isRunning) {
              val elapsedTime = System.currentTimeMillis() - startTime
              updateNotification(elapsedTime / 1000)
              handler.postDelayed(this, 1000)
            }
          }
        })
  }

  /** Stops the timer and removes the notification. */
  private fun stopTimer() {
    isRunning = false
    stopForeground(true)
    stopSelf()
  }

  /**
   * Updates the notification with the elapsed time.
   *
   * @param secondsElapsed The number of seconds that have elapsed since the timer started.
   *
   * TODO: implement icon.
   * TODO: check when implementing notification this is well handled.
   */
  @SuppressLint("NotificationPermission")
  private fun updateNotification(secondsElapsed: Long) {
    val notification =
        NotificationCompat.Builder(this, "TIMER_CHANNEL")
            .setContentTitle("Timer Running")
            .setContentText("Elapsed Time: $secondsElapsed seconds")
            .setOngoing(true)
            .build()
    notificationManager.notify(1, notification)
  }

  /** Creates the notification channel for the timer service. */
  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel =
          NotificationChannel(
              "TIMER_CHANNEL", "Timer Notifications", NotificationManager.IMPORTANCE_LOW)
      notificationManager.createNotificationChannel(channel)
    }
  }

  /** Binds the service to the given intent. */
  override fun onBind(intent: Intent?): IBinder? = null
}
