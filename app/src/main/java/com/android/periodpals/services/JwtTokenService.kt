package com.android.periodpals.services

import com.android.periodpals.BuildConfig
import com.android.periodpals.model.timer.HOUR
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

// Replace this with your Stream secret key (should be kept secure)
class JwtTokenService {

  companion object {
    fun generateStreamToken(
        uid: String?,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
      if (uid.isNullOrBlank()) {
        onFailure("user_id cannot be null or blank")
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
