package com.android.periodpals.model.alert

interface AlertModel {
    fun getNewUid(): String

    fun init(onSuccess: () -> Unit)

    fun getAlerts(onSuccess: (List<Alert>) -> Unit, onFailure: (Exception) -> Unit)

    fun addAlert(alert: Alert, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    fun updateAlert(alert: Alert, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    fun deleteAlertById(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}