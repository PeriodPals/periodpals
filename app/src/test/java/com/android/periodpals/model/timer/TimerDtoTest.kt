package com.android.periodpals.model.timer

import junit.framework.Assert.assertEquals
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class TimerDtoTest {
  companion object {
    val timer1 =
        Timer(
            startTime = LocalDateTime(2022, 1, 1, 0, 0, 0).toString(),
            stopTime = LocalDateTime(2022, 1, 1, 0, 0, 3).toString(),
            remainingTime = 3,
            status = TimerStatus.RUNNING,
            lastTimers = listOf(4, 5, 6),
        )

    val timerDto1 =
        TimerDto(
            startTime = LocalDateTime(2022, 1, 1, 0, 0, 0).toString(),
            stopTime = LocalDateTime(2022, 1, 1, 0, 0, 3).toString(),
            remainingTime = 3,
            status = TimerStatus.RUNNING,
            lastTimers = Json.encodeToString(listOf(4, 5, 6)),
        )

    val timer2 =
        Timer(
            startTime = LocalDateTime(2022, 12, 31, 23, 59, 59).toString(),
            stopTime = LocalDateTime(2023, 1, 1, 0, 0, 1).toString(),
            remainingTime = 3,
            status = TimerStatus.RUNNING,
            lastTimers = listOf(4, 5, 6),
        )

    val timerDto2 =
        TimerDto(
            startTime = LocalDateTime(2022, 12, 31, 23, 59, 59).toString(),
            stopTime = LocalDateTime(2023, 1, 1, 0, 0, 1).toString(),
            remainingTime = 3,
            status = TimerStatus.RUNNING,
            lastTimers = Json.encodeToString(listOf(4, 5, 6)),
        )
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
  fun asTimerHandlesNullStartTime() {
    val timerDto =
        TimerDto(
            startTime = null,
            stopTime = null,
            remainingTime = 0,
            status = TimerStatus.RUNNING,
            lastTimers = Json.encodeToString(emptyList<Long>()))
    val timer = timerDto.asTimer()
    assertEquals(null, timer.startTime)
    assertEquals(null, timer.stopTime)
    assertEquals(0, timer.remainingTime)
    assertEquals(TimerStatus.RUNNING, timer.status)
    assertEquals(emptyList<Int>(), timer.lastTimers)
  }
}
