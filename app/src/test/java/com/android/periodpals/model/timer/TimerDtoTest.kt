package com.android.periodpals.model.timer

import junit.framework.Assert.assertEquals
import org.junit.Test

class TimerDtoTest {
  companion object {
    val input1 =
        TimerDto(
            uid = "1",
            status = TimerStatus.RUNNING,
            averageTime = 3L,
            timerCount = 4,
            startedAt = "5",
            updatedAt = "6",
        )

    val output1 =
        Timer(
            uid = "1",
            status = TimerStatus.RUNNING,
            averageTime = 3L,
            timerCount = 4,
            startedAt = "5",
            updatedAt = "6",
        )

    val input2 =
        TimerDto(
            uid = "2",
            status = TimerStatus.STOPPED,
            averageTime = 7L,
            timerCount = 8,
            startedAt = "9",
            updatedAt = "10",
        )

    val output2 =
        Timer(
            uid = "2",
            status = TimerStatus.STOPPED,
            averageTime = 7L,
            timerCount = 8,
            startedAt = "9",
            updatedAt = "10",
        )
  }

  @Test
  fun asTimerIsCorrect() {
    assertEquals(output1, input1.toTimer())
    assertEquals(output2, input2.toTimer())
  }
}
