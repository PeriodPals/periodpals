package com.android.periodpals.model.alert

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.periodpals.model.location.Location
import kotlinx.coroutines.launch

private const val TAG = "AlertViewModel"

/**
 * ViewModel for managing alert data.
 *
 * @property alertModelSupabase The repository used for loading and saving alerts.
 * @property userId the id linked to the current user.
 * @property _alerts Mutable state holding the list of all alerts.
 * @property alerts Public state exposing the list of all alerts.
 * @property _myAlerts Mutable state holding the list of current users alerts.
 * @property myAlerts Public state exposing the list of current users alerts.
 * @property _alertsWithinRadius Mutable state holding the ordered list of all alerts within a
 *   specified radius.
 * @property alertsWithinRadius Public state exposing the ordered list of all alerts within a
 *   specified radius.
 *     @property alertFilter Mutable state holding a filter for `filterAlerts`.
 *     @property _filterAlerts Mutable state holding the list of alerts filtered by `alertFilter`.
 *     @property filterAlerts Public state exposing the list of alerts filtered y `alertFilter`.
 * @property _palAlerts Mutable state holding the list of other users alerts within selected radius.
 * @property palAlerts Public state exposing the list of other users alerts within selected radius.
 * @property _selectedAlert Mutable state holding the selected alert.
 * @property selectedAlert Public state exposing the selected alert.
 */
class AlertViewModel(private val alertModelSupabase: AlertModelSupabase) : ViewModel() {
  private var userId = mutableStateOf<String?>(null)

  private var _alerts = mutableStateOf<List<Alert>>(listOf())
  val alerts: State<List<Alert>> = _alerts

  private var _myAlerts =
      derivedStateOf<List<Alert>> { _alerts.value.filter { it.uid == userId.value } }
  val myAlerts: State<List<Alert>> = _myAlerts

  private var _alertsWithinRadius = mutableStateOf<List<Alert>>(listOf())
  val alertsWithinRadius: State<List<Alert>> = _alertsWithinRadius
  private var alertFilter = mutableStateOf<(Alert) -> Boolean>({ true })
  private var _filterAlerts = derivedStateOf {
    _alertsWithinRadius.value.filter { alertFilter.value(it) }
  }
  val filterAlerts: State<List<Alert>> = _filterAlerts

  private var _palAlerts =
      derivedStateOf<List<Alert>> { _filterAlerts.value.filter { it.uid != userId.value } }
  val palAlerts: State<List<Alert>> = _palAlerts

  private var _selectedAlert = mutableStateOf<Alert?>(null)
  val selectedAlert: State<Alert?> = _selectedAlert


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
          })
    }
  }

  /**
   * Retrieves an alert by its ID.
   *
   * @param idAlert The ID of the alert to be retrieved.
   * @param onSuccess Callback function called upon successful run of function
   * @param onFailure Callback function called upon a failed run of function
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
   *
   * @param onSuccess Callback function to be called on success, use the states to get results
   * @param onFailure Callback function to be called on failure
   */
  fun fetchAlerts(onSuccess: () -> Unit = {}, onFailure: (Exception) -> Unit = {}) {
    viewModelScope.launch {
      alertModelSupabase.getAllAlerts(
          onSuccess = { alerts ->
            Log.d(TAG, "getAllAlerts: Success")
            _alerts.value = alerts
              if(_alertsWithinRadius.value.isEmpty()) { // initialize list of alerts within radius
                _alertsWithinRadius.value = alerts
              }
            onSuccess()
          },
          onFailure = { e ->
            Log.e(TAG, "getAllAlerts: fail to get alerts: ${e.message}")
            onFailure(e)
          })
    }
  }

  /**
   * Updates an existing alert.
   *
   * @param alert The alert with updated parameters.
   * @param onSuccess Callback function to be called on success.
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
          })
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
          })
    }
  }

  /**
   * Retrieves alerts within a specified radius from a given location.
   *
   * @param location The location from which to search for alerts.
   * @param radius The radius within which to search for alerts, in kilometers.
   * @param onSuccess Callback function to be called on successful retrieval.
   * @param onFailure Callback function to be called on failure.
   */
  fun fetchAlertsWithinRadius(
      location: Location,
      radius: Double,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    viewModelScope.launch {
      alertModelSupabase.getAlertsWithinRadius(
          latitude = location.latitude,
          longitude = location.longitude,
          radius = radius,
          onSuccess = {
            _alertsWithinRadius.value = it
            Log.d(TAG, "getAlertsWithinRadius: Success, $alertsWithinRadius")
            onSuccess()
          },
          onFailure = { e ->
            Log.e(TAG, "getAlertsWithinRadius: fail to get alerts: ${e.message}")
            onFailure(e)
          })
    }
  }

  /** Resets the `alertsWithinRadius` list to the `alerts` list. */
  fun removeFilters() {
    viewModelScope.launch {
      _alertsWithinRadius.value = _alerts.value
      alertFilter.value = { true }
    }
  }

  /**
   * Sets the filter for the `filterAlerts` list. Some filter examples could be:
   * ```
   * alertViewModel.setFilter(filter = { it.urgency == Urgency.HIGH } )
   * // or
   * alertViewModel.setFilter(filter = { it.product == Product.TAMPON )
   * ```
   *
   * You could also make more complex filters like:
   * ```
   * alertViewModel.setFilter(
   *    filter = { it.urgency == Urgency.HIGH && it.product == Product.TAMPON}
   * )
   * ```
   *
   * @param filter Filter to be set on the alert list
   */
  fun setFilter(filter: (Alert) -> Boolean) {
    viewModelScope.launch { alertFilter.value = filter }
  }

  /**
   * Sets the `userID` state of View Model
   *
   * @param uid user id to be stored in a private state
   */
  fun setUserID(uid: String) {
    viewModelScope.launch { userId.value = uid }
  }

  /**
   * Selects an alert.
   *
   * @param alert The alert to be selected.
   */
  fun selectAlert(alert: Alert) {
    viewModelScope.launch { _selectedAlert.value = alert }
  }
}
