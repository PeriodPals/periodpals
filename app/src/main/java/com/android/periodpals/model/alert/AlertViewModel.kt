package com.android.periodpals.model.alert

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

private const val TAG = "AlertViewModel"

/**
 * ViewModel for managing alert data.
 *
 * @property alertModelSupabase The repository used for loading and saving alerts.
 * @property _alerts Mutable state holding the list of alerts.
 * @property alerts Public state exposing the list of alerts.
 */
class AlertViewModel(private val alertModelSupabase: AlertModelSupabase) : ViewModel() {
  // remove this?
  private var _alerts = mutableStateOf<List<Alert>?>(listOf())
  val alerts: State<List<Alert>?> = _alerts

  /**
   * Creates a new alert.
   *
   * @param alert The alert to be created.
   */
  fun createAlert(alert: Alert) {
    viewModelScope.launch {
      alertModelSupabase.addAlert(
          alert = alert,
          onSuccess = {
            Log.d(TAG, "createAlert: Success")
            getAllAlerts() // refresh the alerts list
          },
          onFailure = { e -> Log.e(TAG, "createAlert: fail to create alert: ${e.message}") })
    }
  }

  /**
   * Retrieves an alert by its ID.
   *
   * @param idAlert The ID of the alert to be retrieved.
   * @return The alert if found, null otherwise.
   */
  fun getAlert(idAlert: String): Alert? {
    var alert: Alert? = null
    viewModelScope.launch {
      alertModelSupabase.getAlert(
          idAlert = idAlert,
          onSuccess = { fetched ->
            Log.d(TAG, "getAlert: Success")
            alert = fetched
          },
          onFailure = { e -> Log.e(TAG, "getAlert: fail to get alert: ${e.message}") })
    }
    return alert
  }

  /**
   * Retrieves all alerts.
   *
   * @return The list of all alerts.
   */
  fun getAllAlerts(): List<Alert>? {
    var alertsList: List<Alert>? = null
    viewModelScope.launch {
      alertModelSupabase.getAllAlerts(
          onSuccess = { alerts ->
            Log.d(TAG, "getAllAlerts: Success")
            _alerts.value = alerts
            alertsList = alerts
          },
          onFailure = { e -> Log.e(TAG, "getAllAlerts: fail to get alerts: ${e.message}") })
    }
    return alertsList
  }

  /**
   * Retrieves alerts for a specific user by their UID.
   *
   * @param uid The UID of the user.
   * @return The list of alerts for the user.
   */
  fun getAlertsByUser(uid: String): List<Alert>? {
    var alertsList: List<Alert>? = null
    viewModelScope.launch {
      alertModelSupabase.getAlertsFilteredBy(
          // ideally the uid would not be passed as argument and instead we could get uid by
          // UserViewModel.currentUser?.uid
          cond = { eq("uid", uid) },
          onSuccess = { alerts ->
            Log.d(TAG, "getMyAlerts: Success")
            alertsList = alerts
          },
          onFailure = { e -> Log.e(TAG, "getMyAlerts: fail to get alerts: ${e.message}") })
    }
    return alertsList
  }

  /**
   * Updates an existing alert.
   *
   * @param alert The alert with updated parameters.
   */
  fun updateAlert(alert: Alert) {
    viewModelScope.launch {
      alertModelSupabase.updateAlert(
          alert = alert,
          onSuccess = {
            Log.d(TAG, "updateAlert: Success")
            getAllAlerts()
          },
          onFailure = { e -> Log.e(TAG, "updateAlert: fail to update alert: ${e.message}") })
    }
  }

  /**
   * Deletes an alert.
   *
   * @param alert The alert to be deleted.
   */
  fun deleteAlert(alert: Alert) {
    viewModelScope.launch {
      alert.id?.let {
        alertModelSupabase.deleteAlertById(
            idAlert = it,
            onSuccess = {
              Log.d(TAG, "deleteAlert: Success")
              getAllAlerts()
            },
            onFailure = { e -> Log.e(TAG, "deleteAlert: fail to delete alert: ${e.message}") })
      } ?: run { Log.e(TAG, "deleteAlert: fail to delete alert: id of the Alert is null") }
    }
  }
}
