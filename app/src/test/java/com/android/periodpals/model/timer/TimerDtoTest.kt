package com.android.periodpals.model.timer

import junit.framework.Assert.assertEquals
import org.junit.Test

class TimerDtoTest {
  companion object {
    private const val TIME_1 = 3L
    private const val INSTRUCTION_1 = "Timer 1"
    val timer1 = Timer(time = TIME_1, instructionText = INSTRUCTION_1)
    val timerDto1 = TimerDto(id = timer1.id, time = TIME_1, instructionText = INSTRUCTION_1)
    val id1 = "someId1"

    private const val TIME_2 = 6L
    private const val INSTRUCTION_2 = "Timer 2"
    val timer2 = Timer(time = TIME_2, instructionText = INSTRUCTION_2)
    val timerDto2 = TimerDto(id = timer2.id, time = TIME_2, instructionText = INSTRUCTION_2)
    val id2 = "someId2"
  }

  @Test
  fun constructorIsCorrect() {
    assertEquals(timerDto1, TimerDto(timer1))
    assertEquals(timerDto2, TimerDto(timer2))
  }

  @Test
  fun toTimerIsCorrect() {
    assertEquals(timer1.copy(id = id1), timerDto1.copy(id = id1).toTimer())
    assertEquals(timer2.copy(id = id2), timerDto2.copy(id = id2).toTimer())
  }
}
