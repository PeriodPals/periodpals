package com.android.periodpals.model.alert

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "UserRepositorySupabase"
private const val ALERTS = "alerts"

class AlertModelSupabase(
    private val supabase: SupabaseClient,
) : AlertModel {

  override suspend fun addAlert(
      alert: Alert,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.IO) { supabase.postgrest[ALERTS].insert(alert) }
      Log.d(TAG, "addAlert: Success")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "addAlert: fail to create user profile: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun getAlert(
      idAlert: String,
      onSuccess: (Alert) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val result =
          supabase.postgrest[ALERTS].select { filter { eq("id", idAlert) } }.decodeSingle<Alert>()
      onSuccess(result)
    } catch (e: Exception) {
      Log.d(TAG, "getAlert: fail to retrieve alert: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun getAllAlerts(
      onSuccess: (List<Alert>) -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      val result = supabase.postgrest[ALERTS].select().decodeList<Alert>()
      onSuccess(result)
    } catch (e: Exception) {
      Log.d(TAG, "getAllAlerts: fail to retrieve alerts: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun updateAlert(
      alert: Alert,
      idAlert: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      withContext(Dispatchers.IO) {
        supabase.postgrest[ALERTS].update(alert) { filter { eq("id", idAlert) } }
      }
      Log.d(TAG, "updateAlert: Success")
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "updateAlert: fail to update alert: ${e.message}")
      onFailure(e)
    }
  }

  override suspend fun deleteAlertById(
      idAlert: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  ) {
    try {
      supabase.postgrest[ALERTS].delete { filter { eq("id", idAlert) } }
      onSuccess()
    } catch (e: Exception) {
      Log.d(TAG, "deleteAlertById: fail to delete alert: ${e.message}")
      onFailure(e)
    }
  }
}
