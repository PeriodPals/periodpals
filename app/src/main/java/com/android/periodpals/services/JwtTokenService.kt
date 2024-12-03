package com.android.periodpals.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

class JwtTokenService {

  fun generateToken(userId: String, apiSecret: String): String {
    val algorithm = Algorithm.HMAC256(apiSecret)

    return JWT.create()
        .withIssuer("stream")
        .withClaim("user_id", userId)
        .withClaim("role", "user")
        .withExpiresAt(Date(System.currentTimeMillis() + 3600 * 1000)) // Token expiration (1 hour)
        .sign(algorithm)
  }
}
