package com.android.periodpals.model.user

data class User(
    val id: Int,
    val displayName: String,
    val email: String,
    val imageUrl: String,
    val description: String,
    val age: String
)
