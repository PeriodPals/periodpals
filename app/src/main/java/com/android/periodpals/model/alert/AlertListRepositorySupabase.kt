package com.android.periodpals.model.alert

import io.github.jan.supabase.SupabaseClient

class AlertListRepositorySupabase(private val supabase: SupabaseClient): AlertListRepository {
    override fun getNewUid(): String {
        TODO("Not yet implemented")
    }

    override fun init(onSuccess: () -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getAlerts(onSuccess: (List<Alert>) -> Unit, onFailure: (Exception) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun addAlert(alert: Alert, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateAlert(alert: Alert, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteAlertById(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        TODO("Not yet implemented")
    }
}
