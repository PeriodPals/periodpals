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
  fun testParseLocationGISString() {
    val location = parseLocationGIS("-122.4194,37.7749,San Francisco")
    assert(location.type == "Point")
    assert(location.coordinates[0] == 37.7749)
    assert(location.coordinates[1] == -122.4194)
  }

  @Test(expected = IllegalArgumentException::class)
  fun testParseLocationGISStringInvalidFormat() {
    parseLocationGIS("invalid_format")
  }

    @Test
    fun testParseLocationGISLocation() {
        val location = Location(-122.4194, 37.7749, "San Francisco")
        val locationGIS = parseLocationGIS(location)
        assert(locationGIS.type == "Point")
        assert(locationGIS.coordinates[0] == 37.7749)
        assert(locationGIS.coordinates[1] == -122.4194)
    }
}
