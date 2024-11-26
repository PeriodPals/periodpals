package com.android.periodpals.model.location

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LocationDataClassTest {

  @Test
  fun `toString returns correct string`() {
    val location = Location(19.4326296, -99.1331785, "Mexico City")
    assert("19.4326296,-99.1331785,Mexico City" == location.toString())
  }

  @Test
  fun `toString matches expected comma-separated format`() {
    val locations = listOf(
      Location(46.9484, 7.4521, "Bern"),
      Location(0.0, 0.0, "Null Island"),
      Location(-33.8688, 151.2093, "Sydney"),
      Location(40.7128, -74.0060, "New York City, NY")
    )

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
}