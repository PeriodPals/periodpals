package com.android.periodpals.model.alert

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "AlertRepositorySupabase"
private const val ALERTS = "alerts"

/**
 * Implementation of the AlertModel interface using Supabase.
 *
 * @property supabase The Supabase client used for database operations.
 */
class AlertModelSupabase(
    private val supabase: SupabaseClient,
) : AlertModel {

  /**
   * Adds a new alert to the database.
   *
   * @param alert The alert to be added.
   * @param onSuccess Callback function to be called on successful addition.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  override suspend fun addAlert(
      alert: Alert,
      onSuccess: (String) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val insertedAlertDto =
          withContext(Dispatchers.IO) {
              val alertDto = AlertDto(
                  id = alert.id,
                  uid = alert.uid,
                  name = alert.name,
                  product = alert.product,
                  urgency = alert.urgency,
                  createdAt = alert.createdAt,
                  location = alert.location,
                  message = alert.message,
                  status = alert.status
              )
            supabase.postgrest[ALERTS].insert(alertDto).decodeSingle<AlertDto>()
          }
      val insertedAlert = insertedAlertDto.toAlert()
      if (insertedAlert.id != null) {
        Log.d(TAG, "addAlert: Success")
        onSuccess(insertedAlert.id)
      } else {
        Log.e(TAG, "addAlert: fail to create alert: ID is null")
        onFailure(Exception("ID is null"))
      }
      Log.d(TAG, "addAlert: Success")
    } catch (e: Exception) {
      Log.e(TAG, "addAlert: fail to create alert: ${e.message}")
      onFailure(e)
    }
  }

  /**
   * Retrieves an alert by its ID from the database.
   *
   * @param idAlert The ID of the alert to be retrieved.
   * @param onSuccess Callback function to be called on successful retrieval, with the alert as a
   *   parameter.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  override suspend fun getAlert(
      idAlert: String,
      onSuccess: (Alert) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val result =
          supabase.postgrest[ALERTS].select { filter { eq("id", idAlert) } }.decodeSingle<AlertDto>()
      Log.d(TAG, "getAlert: Success")
      onSuccess(result.toAlert())
    } catch (e: Exception) {
      Log.e(TAG, "getAlert: fail to retrieve alert: ${e.message}")
      onFailure(e)
    }
  }

  /**
   * Retrieves all alerts from the database.
   *
   * @param onSuccess Callback function to be called on successful retrieval, with the list of
   *   alerts as a parameter.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  override suspend fun getAllAlerts(
      onSuccess: (List<Alert>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val result = supabase.postgrest[ALERTS].select().decodeList<AlertDto>()
      Log.d(TAG, "getAllAlerts: Success")
      onSuccess(result.map { it.toAlert() })
    } catch (e: Exception) {
      Log.e(TAG, "getAllAlerts: fail to retrieve alerts: ${e.message}")
      onFailure(e)
    }
  }

  /**
   * Retrieves alerts for a specific user by their UID.
   *
   * @param uid The UID of the user whose alerts are to be retrieved.
   * @param onSuccess Callback function to be called on successful retrieval, with the list of
   *   alerts as a parameter.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  override suspend fun getMyAlerts(
      uid: String,
      onSuccess: (List<Alert>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val result =
          supabase.postgrest[ALERTS].select { filter { eq("uid", uid) } }.decodeList<AlertDto>()
      Log.d(TAG, "getMyAlerts: Success")
      onSuccess(result.map { it.toAlert() })
    } catch (e: Exception) {
      Log.e(TAG, "getMyAlerts: fail to retrieve alerts: ${e.message}")
      onFailure(e)
    }
  }

  /**
   * Updates an existing alert in the database.
   *
   * @param idAlert The ID of the alert to be updated.
   * @param alert Updated parameters for the Alert.
   * @param onSuccess Callback function to be called on successful update.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  override suspend fun updateAlert(
      alert: Alert,
      idAlert: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.IO) {
          val alertDto = AlertDto(
              id = alert.id,
              uid = alert.uid,
              name = alert.name,
              product = alert.product,
              urgency = alert.urgency,
              createdAt = alert.createdAt,
              location = alert.location,
              message = alert.message,
              status = alert.status
          )
        supabase.postgrest[ALERTS].update(alertDto) { filter { eq("id", idAlert) } }
      }
      Log.d(TAG, "updateAlert: Success")
      onSuccess()
    } catch (e: Exception) {
      Log.e(TAG, "updateAlert: fail to update alert: ${e.message}")
      onFailure(e)
    }
  }

  /**
   * Deletes an alert by its ID from the database.
   *
   * @param idAlert The ID of the alert to be deleted.
   * @param onSuccess Callback function to be called on successful deletion.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  override suspend fun deleteAlertById(
      idAlert: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      supabase.postgrest[ALERTS].delete { filter { eq("id", idAlert) } }
      Log.d(TAG, "deleteAlert: Success")
      onSuccess()
    } catch (e: Exception) {
      Log.e(TAG, "deleteAlertById: fail to delete alert: ${e.message}")
      onFailure(e)
    }
  }
}
