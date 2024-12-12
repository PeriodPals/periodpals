package com.android.periodpals.model.timer

import junit.framework.Assert.assertEquals
import org.junit.Test

class TimerDtoTest {
  companion object {
    private const val INSTRUCTION_1 = "Timer 1"
    val timer1 = Timer(time = null, instructionText = INSTRUCTION_1)
    val timerDto1 = TimerDto(id = timer1.id, time = null, instructionText = INSTRUCTION_1)
    private const val ID1 = "someId1"

    private const val TIME_2 = 6L
    val timer2 = Timer(time = TIME_2, instructionText = null)
    val timerDto2 = TimerDto(id = timer2.id, time = TIME_2, instructionText = null)
    private const val ID2 = "someId2"
  }

  @Test
  fun constructorIsCorrect() {
    assertEquals(timerDto1, TimerDto(timer1))
    assertEquals(timerDto2, TimerDto(timer2))
  }

  @Test
  fun toTimerIsCorrect() {
    assertEquals(timer1.copy(id = ID1), timerDto1.copy(id = ID1).toTimer())
    assertEquals(timer2.copy(id = ID2), timerDto2.copy(id = ID2).toTimer())
  }
}
