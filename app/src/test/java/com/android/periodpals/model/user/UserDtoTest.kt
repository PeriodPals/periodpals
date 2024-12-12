package com.android.periodpals.model.user

import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.parseLocationGIS
import org.junit.Assert.assertEquals
import org.junit.Test

class UserDtoTest {

  val input =
      UserDto(
          "test_name",
          "test_url",
          "test_desc",
          "test_dob",
          1,
          "test_fcm_token",
          parseLocationGIS(Location.DEFAULT_LOCATION),
      )

  val output =
      User(
          "test_name",
          "test_url",
          "test_desc",
          "test_dob",
          1,
          "test_fcm_token",
          parseLocationGIS(Location.DEFAULT_LOCATION),
      )

  @Test
  fun asUserIsCorrect() {
    assertEquals(output, input.asUser())
  }
}
