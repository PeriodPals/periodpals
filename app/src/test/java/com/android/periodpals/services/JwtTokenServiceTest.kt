package com.android.periodpals.services

import com.android.periodpals.BuildConfig
import com.android.periodpals.model.timer.HOUR
import com.android.periodpals.model.timer.SECOND
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.junit.Assert
import org.junit.Test
import kotlin.math.abs

class JwtTokenServiceTest {

  companion object {
    private const val USER_ID = "test_user_id"
    private const val MARGIIN_OF_ERROR = 2L * SECOND
  }

  @Test
  fun generateStreamTokenSuccessful() {
    var token: String? = null
    JwtTokenService.generateStreamToken(
        USER_ID,
        onSuccess = { token = it },
        onFailure = { Assert.fail("Expected success but got failure: $it") })

    Assert.assertNotNull(token)

    val decodedJWT =
        JWT.require(Algorithm.HMAC256(BuildConfig.STREAM_SDK_SECRET)).build().verify(token)

    Assert.assertEquals(USER_ID, decodedJWT.getClaim("user_id").asString())

    // Check if the expiration time is within the expected range
    val currentTime = System.currentTimeMillis()
    val expirationTime = decodedJWT.expiresAt.time
    val expectedExpirationTime = currentTime + HOUR

    // Allow a small margin of error for the time difference
    Assert.assertTrue(abs((expirationTime - expectedExpirationTime).toDouble()) <= MARGIIN_OF_ERROR)
  }

  @Test
  fun generateStreamTokenEmptyUid() {
    var failureMessage: String? = null
    val emptyUserId = ""
    JwtTokenService.generateStreamToken(
        emptyUserId,
        onSuccess = { Assert.fail("Expected failure but got success: $it") },
        onFailure = { failureMessage = it })

    Assert.assertEquals("user_id cannot be null or blank", failureMessage)
  }

  @Test
  fun generateStreamTokenNullUid() {
    var failureMessage: String? = null
    JwtTokenService.generateStreamToken(
        null,
        onSuccess = { Assert.fail("Expected failure but got success: $it") },
        onFailure = { failureMessage = it })

    Assert.assertEquals("user_id cannot be null or blank", failureMessage)
  }
}
