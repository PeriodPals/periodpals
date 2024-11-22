package com.android.periodpals.model.alert

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
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
class AlertViewModel(
    private val alertModelSupabase: AlertModelSupabase,
    private val userId: String
) : ViewModel() {
  // remove this?
  private var _alerts = mutableStateOf<List<Alert>>(listOf())
  val alerts: State<List<Alert>> = _alerts

  private var _myAlerts = derivedStateOf<List<Alert>> { _alerts.value.filter { it.uid == userId } }
  val myAlerts: State<List<Alert>> = _myAlerts

  private var _palAlerts = derivedStateOf<List<Alert>> { _alerts.value.filter { it.uid != userId } }
  val palAlerts: State<List<Alert>> = _palAlerts

  private var alertFilter = mutableStateOf<(Alert) -> Boolean>({false})
  private var _filterAlerts = derivedStateOf { _alerts.value.filter { alertFilter.value(it) } }
  private var filterAlerts: State<List<Alert>> = _filterAlerts

  /**
   * Creates a new alert.
   *
   * @param alert The alert to be created.
   * @param onSuccess Callback function to be called on success
   * @param onFailure Callback function to be called on failure
   */
  fun createAlert(alert: Alert, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    viewModelScope.launch {
      alertModelSupabase.addAlert(
          alert = alert,
          onSuccess = {
            Log.d(TAG, "createAlert: Success")
            fetchAlerts(onSuccess, onFailure) // refresh the alerts list
          },
          onFailure = { e ->
              Log.e(TAG, "createAlert: fail to create alert: ${e.message}")
              onFailure(e)
          }
      )
    }
  }

  /**
   * Retrieves an alert by its ID.
   *
   * @param idAlert The ID of the alert to be retrieved.
   * @param onSuccess Callback function called upon successful run of function
   * @param onFailure Callback function called upon a failed run of function
   * @return The alert if found, null otherwise.
   */
  fun getAlert(idAlert: String, onSuccess: (Alert) -> Unit, onFailure: (Exception) -> Unit) {
    viewModelScope.launch {
      alertModelSupabase.getAlert(
          idAlert = idAlert,
          onSuccess = {
            Log.d(TAG, "getAlert: Success")
            onSuccess(it)
          },
          onFailure = { e ->
            Log.e(TAG, "getAlert: fail to get alert: ${e.message}")
            onFailure(e)
          })
    }
  }

  /**
   * Retrieves all alerts.
   * @param onSuccess Callback function to be called on success, use the states to get results
   * @param onFailure Callback function to be called on failure
   * @return The list of all alerts.
   */
  fun fetchAlerts(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    viewModelScope.launch {
      alertModelSupabase.getAllAlerts(
          onSuccess = { alerts ->
            Log.d(TAG, "getAllAlerts: Success")
            _alerts.value = alerts
            onSuccess()
          },
          onFailure = { e ->
            Log.e(TAG, "getAllAlerts: fail to get alerts: ${e.message}")
            onFailure(e)
          })
    }
  }

  /**
   * Retrieves alerts for a specific user by their UID.
   *
   * @param uid The UID of a user.
   * @param onSuccess Callback function called upon successful run of function
   * @param onFailure Callback function called upon a failed run of function
   * @return The list of alerts for the user.
   */
  fun getAlertsByUser(
      uid: String,
      onSuccess: (List<Alert>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    viewModelScope.launch {
      alertModelSupabase.getAlertsFilteredBy(
          // ideally the uid would not be passed as argument and instead we could get uid by
          cond = { eq("uid", uid) },
          onSuccess = { alerts ->
            Log.d(TAG, "getAlertsByUser: Success")
            onSuccess(alerts)
          },
          onFailure = {
            e -> Log.e(TAG, "getAlertsByUser: fail to get alerts: ${e.message}")
            onFailure(e)
          }
      )
    }
  }

  /**
   * Updates an existing alert.
   *
   * @param alert The alert with updated parameters.
   * @param onFailure Callback function to be called on failure.
   */
  fun updateAlert(alert: Alert, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    viewModelScope.launch {
      alertModelSupabase.updateAlert(
          alert = alert,
          onSuccess = {
            Log.d(TAG, "updateAlert: Success")
            fetchAlerts(onSuccess, onFailure)
          },
          onFailure = { e ->
            Log.e(TAG, "updateAlert: fail to update alert: ${e.message}")
            onFailure(e)
          }
      )
    }
  }

  /**
   * Deletes an alert.
   *
   * @param idAlert The ID of the alert to be retrieved.
   * @param onSuccess Callback function to be called on success
   * @param onFailure Callback function to be called on failure
   */
  fun deleteAlert(idAlert: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    viewModelScope.launch {
        alertModelSupabase.deleteAlertById(
            idAlert = idAlert,
            onSuccess = {
              Log.d(TAG, "deleteAlert: Success")
              fetchAlerts(onSuccess, onFailure)
            },
            onFailure = { e ->
              Log.e(TAG, "deleteAlert: fail to delete alert: ${e.message}")
              onFailure(e)
            }
        )
    }
  }

  /**
   * Sets the filter for the filter alert list.
   *
   * @param filter Filter to be set on the alert list
   */
  fun setFilter(filter: (Alert) -> Boolean){
      viewModelScope.launch {
          alertFilter.value = filter
      }
  }
}
