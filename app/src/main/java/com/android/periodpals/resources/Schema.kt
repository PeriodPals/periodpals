package com.android.periodpals.resources

import com.powersync.db.schema.Column
import com.powersync.db.schema.Schema
import com.powersync.db.schema.Table

const val USERS = "users"

val users = Table(
    USERS,
    listOf(
        Column.text("user_id"),
        Column.text("name"),
        Column.text("email"),
        Column.text("imageUrl"),
        Column.text("description"),
        Column.text("dob"),
        Column.text("id"),
        Column.text("fcm_token"),
        Column.integer("erred_distance"),
        Column.text("locationGIS")
    )
)

val localSchema: Schema = Schema(
    listOf(
        users
    )
)