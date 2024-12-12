package com.android.periodpals.services

import com.android.periodpals.BuildConfig
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlin.math.abs
import org.junit.Assert
import org.junit.Test

class JwtTokenServiceTest {

  @Test
  fun testGenerateStreamToken_validToken() {
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
    val expectedExpirationTime = currentTime + 3600 * 1000

    // Allow a small margin of error for the time difference
    val marginOfError: Long = 2000 // 2 seconds
    Assert.assertTrue(abs((expirationTime - expectedExpirationTime).toDouble()) <= marginOfError)
  }

  @Test
  fun testGenerateStreamToken_differentUserId() {
    var token: String? = null
    val differentUserId = "another_user_id"
    JwtTokenService.generateStreamToken(
        differentUserId,
        onSuccess = { token = it },
        onFailure = { Assert.fail("Expected success but got failure: $it") })

    Assert.assertNotNull(token)

    val decodedJWT =
        JWT.require(Algorithm.HMAC256(BuildConfig.STREAM_SDK_SECRET)).build().verify(token)

    Assert.assertEquals(differentUserId, decodedJWT.getClaim("user_id").asString())

    // Check if the expiration time is within the expected range
    val currentTime = System.currentTimeMillis()
    val expirationTime = decodedJWT.expiresAt.time
    val expectedExpirationTime = currentTime + 3600 * 1000

    // Allow a small margin of error for the time difference
    val marginOfError: Long = 2000 // 2 seconds
    Assert.assertTrue(abs((expirationTime - expectedExpirationTime).toDouble()) <= marginOfError)
  }

  @Test
  fun testGenerateStreamToken_emptyUserId() {
    var failureMessage: String? = null
    val emptyUserId = ""
    JwtTokenService.generateStreamToken(
        emptyUserId,
        onSuccess = { Assert.fail("Expected failure but got success: $it") },
        onFailure = { failureMessage = it })

    Assert.assertEquals("user_id cannot be null or blank", failureMessage)
  }

  @Test
  fun testGenerateStreamToken_nullUserId() {
    var failureMessage: String? = null
    JwtTokenService.generateStreamToken(
        null,
        onSuccess = { Assert.fail("Expected failure but got success: $it") },
        onFailure = { failureMessage = it })

    Assert.assertEquals("user_id cannot be null or blank", failureMessage)
  }

  companion object {
    private const val USER_ID = "test_user_id"
  }
}
