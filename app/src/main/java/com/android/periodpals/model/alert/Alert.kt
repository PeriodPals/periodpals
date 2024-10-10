package com.android.periodpals.model.alert

data class Alert(
    val uid: String,            // id of the alert
    val owner: String,          // id (mail) of the user owner of the alert
    val name: String,           // display name of the user
    val product: Product,
    val urgency: Urgency,
//    val time: ,           //TODO: Timestamp supabase??
    val location: String,       //TODO: Create data class Location
    val message: String,
    val status: Status
)

enum class Product {
    TAMPON,
    PAD
}

enum class Urgency {
    URGENT,
    NORMAL,
    NOT_URGENT,
}

enum class Status {
    CREATED,        // The alert has just been created
    PENDING,        // Someone has acknowledged the alert and is helping
    SOLVED,         // The alert has been resolved, help was provided
    CANCELED        // The alert was canceled by the user
}
