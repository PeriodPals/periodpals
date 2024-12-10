package com.android.periodpals.services

import com.android.periodpals.BuildConfig
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

// Replace this with your Stream secret key (should be kept secure)
private const val STREAM_SECRET = BuildConfig.STREAM_SDK_SECRET

class JwtTokenService {

  companion object {
    fun generateStreamToken(uid: String): String {
      val algorithm = Algorithm.HMAC256(STREAM_SECRET)

      return JWT.create()
          .withClaim("user_id", uid)
          .withExpiresAt(Date(System.currentTimeMillis() + 3600 * 1000)) // 1 hour expiration
          .sign(algorithm)
    }
  }
}
