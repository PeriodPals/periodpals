package com.android.periodpals.model.timer

import junit.framework.Assert.assertEquals
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class TimerDtoTest {
  companion object {
    val timer1 = Timer(lastTimers = listOf(4, 5, 6))
    val timerDto1 = TimerDto(lastTimers = Json.encodeToString(listOf(4, 5, 6)))

    val timer2 = Timer(lastTimers = listOf(5, 3, 89))
    val timerDto2 = TimerDto(lastTimers = Json.encodeToString(listOf(5, 3, 89)))
  }

  @Test
  fun asTimerIsCorrect() {
    assertEquals(timer1, timerDto1.asTimer())
    assertEquals(timer2, timerDto2.asTimer())
  }

  @Test
  fun asTimerDtoIsCorrect() {
    assertEquals(timerDto1, timer1.asTimerDto())
    assertEquals(timerDto2, timer2.asTimerDto())
  }

  @Test
  fun asTimerHandlesEmptyList() {
    val timerDto = TimerDto(lastTimers = Json.encodeToString(emptyList<Long>()))
    val timer = timerDto.asTimer()
    assertEquals(emptyList<Int>(), timer.lastTimers)
  }
}
