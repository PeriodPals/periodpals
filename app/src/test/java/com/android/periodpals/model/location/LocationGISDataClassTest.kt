package com.android.periodpals.model.location

import org.junit.Test

class LocationGISDataClassTest {
  @Test
  fun testLocationGISInitialization() {
    val location = LocationGIS("Point", listOf(-122.4194, 37.7749))
    assert(location.type == "Point")
    assert(location.coordinates[0] == -122.4194)
    assert(location.coordinates[1] == 37.7749)
  }

  @Test
  fun testParseLocationGIS() {
    val location = parseLocationGIS("-122.4194,37.7749,San Francisco")
    assert(location.type == "Point")
    assert(location.coordinates[0] == 37.7749)
    assert(location.coordinates[1] == -122.4194)
  }

  @Test(expected = IllegalArgumentException::class)
  fun testParseLocationGISInvalidFormat() {
    parseLocationGIS("invalid_format")
  }
}
