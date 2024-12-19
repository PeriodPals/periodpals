package com.android.periodpals.services

import android.util.Log
import com.android.periodpals.BuildConfig
import com.android.periodpals.model.timer.HOUR
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

private const val TAG = "JwtTokenService"

/** Service for generating JWT tokens. */
class JwtTokenService {

  companion object {
    /**
     * Generates a Stream Chat token for the given user ID.
     *
     * @param uid The user ID.
     * @param onSuccess The callback to be invoked when the token is generated successfully.
     * @param onFailure The callback to be invoked when the token generation fails.
     */
    fun generateStreamToken(
        uid: String?,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
      if (uid.isNullOrBlank()) {
        Log.d(TAG, "User ID is null or blank.")
        onFailure(Exception("User ID is null or blank."))
        return
      }
      val algorithm = Algorithm.HMAC256(BuildConfig.STREAM_SDK_SECRET)

      val expirationTime = Date(System.currentTimeMillis() + HOUR)
      val token =
          JWT.create().withClaim("user_id", uid).withExpiresAt(expirationTime).sign(algorithm)
      onSuccess(token)
    }
  }
}
