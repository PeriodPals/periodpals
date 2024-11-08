package com.android.periodpals.model.user

import org.junit.Assert.assertEquals
import org.junit.Test

class UserDtoTest {

  val input = UserDto("test_name", "test_url", "test_desc", "test_dob")

  val output = User("test_name", "test_url", "test_desc", "test_dob")

  @Test
  fun asUserIsCorrect() {
    assertEquals(output, input.asUser())
  }
}
