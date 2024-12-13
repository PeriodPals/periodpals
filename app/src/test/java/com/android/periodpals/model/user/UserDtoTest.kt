package com.android.periodpals.model.user

import com.android.periodpals.model.location.Location
import com.android.periodpals.model.location.parseLocationGIS
import org.junit.Assert.assertEquals
import org.junit.Test

class UserDtoTest {

  companion object {
    val name = "test_name"
    val imageUrl = "test_image"
    val description = "test_description"
    val dob = "test_dob"
    val id = "test_id"
    val preferredDistance = 500
    val fcmToken = "test_fcm_token"
    val locationGIS = parseLocationGIS(Location.DEFAULT_LOCATION)
  }

  val input =
      UserDto(
          name,
          imageUrl,
          description,
          dob,
          preferredDistance,
          fcmToken,
          locationGIS,
      )

  val output =
      User(
          name,
          imageUrl,
          description,
          dob,
          preferredDistance,
          fcmToken,
          locationGIS,
      )

  @Test
  fun asUserIsCorrect() {
    assertEquals(output, input.asUser())
  }
}
