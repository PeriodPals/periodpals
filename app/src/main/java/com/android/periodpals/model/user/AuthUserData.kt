package com.android.periodpals.model.user

import io.github.jan.supabase.auth.user.UserInfo

/**
 * Data class used to transfer authenticated user data from authentication view model to profile
 * view model.
 *
 * @param uid user's id within `auth` table in supabase
 * @param email user's linked email
 */
data class AuthUserData(
  val uid: String,
  val email: String?
) {
  fun fromUserData(user: UserInfo): AuthUserData {
    return AuthUserData(
      uid = user.id,
      email = user.email
    )
  }
}
