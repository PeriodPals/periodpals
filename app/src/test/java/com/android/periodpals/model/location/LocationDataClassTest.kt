package com.android.periodpals.model.location

import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LocationDataClassTest {

  @Test
  fun `toString returns correct string`() {
    val location = Location(19.4326, -99.1331, "Mexico City")
    assert("19.4326,-99.1331,Mexico City" == location.toString())
  }

  @Test
  fun `toString matches expected comma-separated format`() {
    val locations =
        listOf(
            Location(46.9484, 7.4521, "Bern"),
            Location(0.0, 0.0, "Null Island"),
            Location(-33.8688, 151.2093, "Sydney"),
            Location(40.7128, -74.0060, "New York City, NY"))

    /* Regex to verify the format
    ^     start of string
    -?    optional negative sign (coordinate may be negative)
    \d+   one or more digits
    \.\d+ decimal point followed by one or more digits
    ,     a literal comma
    \S+   one or more non-whitespace characters
    .*    allows for more characters in case the name has multiple words
    $     end of string
    */
    val expectedFormat = """^-?\d+\.\d+,-?\d+\.\d+,\S+.*$""".toRegex()

    for (loc in locations) {
      assert(loc.toString().matches(expectedFormat))
    }
  }

  @Test
  fun `fromString returns correct Location object`() {
    val serializedLoc = "19.4326,-99.1331,Mexico City"
    val location = Location.fromString(serializedLoc)

    assert(location.latitude == 19.4326)
    assert(location.longitude == -99.1331)
    assert(location.name == "Mexico City")
  }

  @Test
  fun `fromString throws IllegalArgumentException when badly formatted input string`() {
    val badSerializedLocation = "19.4326,-99.1331,Mexico City, Mexico"

    assertThrows(IllegalArgumentException::class.java) {
      Location.fromString(badSerializedLocation)
    }
  }

  @Test
  fun `fromString throws IllegalArgumentException when input string doesn't contain coordinates`() {
    val noCoordinateLocation = ",,Non Existent Place"

    assertThrows(IllegalArgumentException::class.java) { Location.fromString(noCoordinateLocation) }
  }

  @Test
  fun `fromString throws IllegalArgumentException when input string has blank name`() {
    val noNameLocation = "0.0,0.0,"

    assertThrows(IllegalArgumentException::class.java) { Location.fromString(noNameLocation) }
  }

  @Test
  fun `toString serializes and fromString successfully deserializes with one word names`() {
    val locations =
        listOf(
            Location(46.9484, 7.4521, "Bern"),
            Location(-33.8688, 151.2093, "Sydney"),
            Location(47.60383, -122.3300, "Seattle"))
    assert(isSerializationCorrect(locations))
  }

  @Test
  fun `toString serializes and fromString successfully deserializes with multiple word names`() {
    val locations =
        listOf(
            Location(37.7792, -122.4193, "San Francisco"),
            Location(-33.4377, -70.6504, "Santiago de Chile"),
            Location(50.1106, 8.6820, "Frankfurt am Main"))
    assert(isSerializationCorrect(locations))
  }

  @Test
  fun `toString serializes and fromString successfully deserializes with comma in name`() {
    val locations =
        listOf(
            Location(40.7127, -74.0060, "New York, NY"),
            Location(46.5218, 6.6327, "Lausanne, VD"),
            Location(19.0815, 72.8866, "Mumbai, Maharashtra"))
    assert(isSerializationCorrect(locations))
  }

  /**
   * Verifies that the serialization and deserialization of the [Location] objects in the
   * [locations] parameter is correct.
   *
   * @param locations
   * @return True if the serialization-deserialization was successful and false otherwise.
   */
  private fun isSerializationCorrect(locations: List<Location>): Boolean {
    return locations.all { loc ->
      val serialized = loc.toString()
      loc == Location.fromString(serialized)
    }
  }
}
