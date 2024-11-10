package com.android.periodpals.model.alert

import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder

/** Interface representing the Alert model. */
interface AlertModel {

  /**
   * Adds a new alert.
   *
   * @param alert The alert to be added.
   * @param onSuccess Callback function to be called on successful addition, with the ID of the
   *   created alert as a parameter.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  suspend fun addAlert(alert: Alert, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Retrieves an alert by its ID.
   *
   * @param idAlert The ID of the alert to be retrieved.
   * @param onSuccess Callback function to be called on successful retrieval, with the alert as a
   *   parameter.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  suspend fun getAlert(idAlert: String, onSuccess: (Alert) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Retrieves all alerts in data base.
   *
   * @param onSuccess Callback function to be called on successful retrieval, with the list of
   *   alerts as a parameter.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  suspend fun getAllAlerts(onSuccess: (List<Alert>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Retrieves alerts for a specific user by their UID.
   *
   * @param cond The condition to filter the alerts by, eg. `uid == user.uid`.
   * @param onSuccess Callback function to be called on successful retrieval, with the list of
   *   alerts as a parameter.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  suspend fun getAlertsFilteredBy(
      cond: PostgrestFilterBuilder.() -> Unit,
      onSuccess: (List<Alert>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Updates an existing alert (edited).
   *
   * @param alert Alert with updated parameters. `id` must not be null to know which alert to update
   * @param onSuccess Callback function to be called on successful update.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  suspend fun updateAlert(alert: Alert, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Deletes an alert by its ID.
   *
   * @param idAlert The ID of the alert to be deleted.
   * @param onSuccess Callback function to be called on successful deletion.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  suspend fun deleteAlertById(
      idAlert: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )
}
