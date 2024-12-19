package com.android.periodpals.model.location

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class UserLocationDtoTest {

  private val json = Json { ignoreUnknownKeys = true }

  @Test
  fun serializeUserLocationDto() {
    val location = LocationGIS("Point", listOf(12.34, 56.78))
    val userLocationDto = UserLocationDto("user123", location)
    val jsonString = json.encodeToString(userLocationDto)
    val expectedJson =
        """{"uid":"user123","locationGIS":{"type":"Point","coordinates":[12.34,56.78]}}"""
    assertEquals(expectedJson, jsonString)
  }

  @Test
  fun deserializeUserLocationDto() {
    val jsonString =
        """{"uid":"user123","locationGIS":{"type":"Point","coordinates":[12.34,56.78]}}"""
    val userLocationDto = json.decodeFromString<UserLocationDto>(jsonString)
    val expectedLocation = LocationGIS("Point", listOf(12.34, 56.78))
    val expectedUserLocationDto = UserLocationDto("user123", expectedLocation)
    assertEquals(expectedUserLocationDto, userLocationDto)
  }
}
