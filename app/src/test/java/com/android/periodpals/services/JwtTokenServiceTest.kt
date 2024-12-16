package com.android.periodpals.services

import com.android.periodpals.BuildConfig
import com.android.periodpals.model.timer.HOUR
import com.android.periodpals.model.timer.SECOND
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlin.math.abs
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Test

class JwtTokenServiceTest {

  companion object {
    private const val FAILURE_MESSAGE = "java.lang.Exception: User ID is null or blank."

    private const val USER_ID = "test_user_id"
    private const val MARGIN_OF_ERROR = 2L * SECOND
  }

  @Test
  fun generateStreamTokenSuccessful() {
    var token: String? = null
    JwtTokenService.generateStreamToken(
        USER_ID,
        onSuccess = { token = it },
        onFailure = { fail("Expected success but got failure: $it") })

    assertNotNull(token)

    val decodedJWT =
        JWT.require(Algorithm.HMAC256(BuildConfig.STREAM_SDK_SECRET)).build().verify(token)

    assertEquals(USER_ID, decodedJWT.getClaim("user_id").asString())

    // Check if the expiration time is within the expected range
    val currentTime = System.currentTimeMillis()
    val expirationTime = decodedJWT.expiresAt.time
    val expectedExpirationTime = currentTime + HOUR

    // Allow a small margin of error for the time difference
    Assert.assertTrue(abs((expirationTime - expectedExpirationTime).toDouble()) <= MARGIN_OF_ERROR)
  }

  @Test
  fun generateStreamTokenEmptyUid() {
    var failureMessage: String? = null
    val emptyUserId = ""
    JwtTokenService.generateStreamToken(
        emptyUserId,
        onSuccess = { fail("Should not call `onSuccess`") },
        onFailure = { failureMessage = it.toString() })

    assertNotNull(failureMessage)
    assertEquals(FAILURE_MESSAGE, failureMessage)
  }

  @Test
  fun generateStreamTokenNullUid() {
    var failureMessage: String? = null
    JwtTokenService.generateStreamToken(
        null,
        onSuccess = { fail("Should not call `onSuccess`") },
        onFailure = { failureMessage = it.toString() })

    assertNotNull(failureMessage)
    assertEquals(FAILURE_MESSAGE, failureMessage)
  }
}
