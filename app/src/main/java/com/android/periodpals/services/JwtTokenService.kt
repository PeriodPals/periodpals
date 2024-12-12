package com.android.periodpals.services

import com.android.periodpals.BuildConfig
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

// Replace this with your Stream secret key (should be kept secure)
class JwtTokenService {

  companion object {
    fun generateStreamToken(uid: String): String {
      val algorithm = Algorithm.HMAC256(BuildConfig.STREAM_SDK_SECRET)

      val expirationTime = Date(System.currentTimeMillis() + 3600 * 1000)
      return JWT.create().withClaim("user_id", uid).withExpiresAt(expirationTime).sign(algorithm)
    }
  }
}
