package com.android.periodpals.model.timer

import junit.framework.Assert.assertEquals
import org.junit.Test

class TimerDtoTest {
  companion object {
    val timer1 = Timer(timerID = "1", userID = "2", time = 3)
    val timerDto1 = TimerDto(timerID = "1", userID = "2", time = 3)

    val timer2 = Timer(timerID = "4", userID = "5", time = 6)
    val timerDto2 = TimerDto(timerID = "4", userID = "5", time = 6)
  }

  @Test
  fun constructorIsCorrect() {
    assertEquals(timerDto1, TimerDto(timer1))
    assertEquals(timerDto2, TimerDto(timer2))
  }

  @Test
  fun toTimerIsCorrect() {
    assertEquals(timer1, timerDto1.toTimer())
    assertEquals(timer2, timerDto2.toTimer())
  }
}
