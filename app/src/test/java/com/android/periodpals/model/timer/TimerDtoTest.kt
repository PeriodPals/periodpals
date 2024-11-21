package com.android.periodpals.model.timer

import junit.framework.Assert.assertEquals
import org.junit.Test

class TimerDtoTest {
  companion object {
    val input1 =
        TimerDto(
            startTime = "1",
            elapsedTime = 3,
            status = TimerStatus.RUNNING,
            lastTimers = listOf(4, 5, 6))

    val output1 =
        Timer(
            startTime = "1",
            elapsedTime = 3,
            status = TimerStatus.RUNNING,
            lastTimers = listOf(4, 5, 6))

    val input2 =
        TimerDto(
            startTime = "2",
            elapsedTime = 7,
            status = TimerStatus.STOPPED,
            lastTimers = listOf(8, 9, 10))

    val output2 =
        Timer(
            startTime = "2",
            elapsedTime = 7,
            status = TimerStatus.STOPPED,
            lastTimers = listOf(8, 9, 10))
  }

  @Test
  fun asTimerIsCorrect() {
    assertEquals(output1, input1.asTimer())
    assertEquals(output2, input2.asTimer())
  }

  @Test
  fun constructorIsCorrect() {
    val timerDto =
        TimerDto(
            startTime = "3",
            elapsedTime = 10,
            status = TimerStatus.STOPPED,
            lastTimers = listOf(11, 12, 13))
    assertEquals("3", timerDto.startTime)
    assertEquals(10, timerDto.elapsedTime)
    assertEquals(TimerStatus.STOPPED, timerDto.status)
    assertEquals(listOf(11, 12, 13), timerDto.lastTimers)
  }

  @Test
  fun asTimerHandlesNullStartTime() {
    val timerDto =
        TimerDto(
            startTime = null,
            elapsedTime = 0,
            status = TimerStatus.STOPPED,
            lastTimers = emptyList())
    val timer = timerDto.asTimer()
    assertEquals(null, timer.startTime)
    assertEquals(0, timer.elapsedTime)
    assertEquals(TimerStatus.STOPPED, timer.status)
    assertEquals(emptyList<Int>(), timer.lastTimers)
  }
}
