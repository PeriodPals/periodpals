package com.android.periodpals.model.timer

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.anyBoolean
import org.mockito.Mockito.anyString
import org.mockito.Mockito.eq
import org.mockito.Mockito.isNull
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class TimerManagerTest {
  private lateinit var sharedPreferences: SharedPreferences
  private lateinit var editor: SharedPreferences.Editor
  private lateinit var activity: Activity
  private lateinit var timerManager: TimerManager
  private var dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())

  @Before
  fun setUp() {
    sharedPreferences = mock(SharedPreferences::class.java)
    editor = mock(SharedPreferences.Editor::class.java)
    `when`(sharedPreferences.edit()).thenReturn(editor)
    `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
    `when`(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor)

    activity = mock(Activity::class.java)
    `when`(activity.getPreferences(eq(Context.MODE_PRIVATE))).thenReturn(sharedPreferences)

    timerManager = TimerManager(activity)
  }

  @Test
  fun sharedPreferencesInitialisesCorrectly() {
    val startTime = "01/01/2024 00:00:00"
    `when`(sharedPreferences.getString(TimerManager.START_TIME_KEY, null)).thenReturn(startTime)
    `when`(sharedPreferences.getBoolean(TimerManager.COUNTING_KEY, false)).thenReturn(true)

    timerManager = TimerManager(activity)

    assertNotNull(timerManager.startTime())
    assertEquals(
        dateFormat.parse(startTime)?.toString() ?: "", timerManager.startTime()?.toString())
    assertTrue(timerManager.timerCounting())
  }

  @Test
  fun startTimerActionIsCorrect() {
    timerManager.startTimerAction()

    assertNotNull(timerManager.startTime())
    assertNotNull(timerManager.stopTime())
    assertTrue(timerManager.timerCounting())
    verify(editor).putString(eq(TimerManager.START_TIME_KEY), anyString())
    verify(editor).putBoolean(eq(TimerManager.COUNTING_KEY), eq(true))
  }

  @Test
  fun resetTimerActionIsCorrect() {
    timerManager.resetTimerAction()

    assertNull(timerManager.startTime())
    assertNull(timerManager.stopTime())
    assertFalse(timerManager.timerCounting())
    verify(editor).putString(eq(TimerManager.START_TIME_KEY), isNull())
    verify(editor).putBoolean(eq(TimerManager.COUNTING_KEY), eq(false))
  }

  @Test
  fun stopTimerActionIsCorrect() {
    val startTime = Date(System.currentTimeMillis() - 3_600_000) // 1 hour ago
    timerManager.setStartTime(startTime)
    val elapsedTime = timerManager.stopTimerAction()

    assertTrue(elapsedTime > 0)
    assertNull(timerManager.startTime())
    assertNull(timerManager.stopTime())
    assertFalse(timerManager.timerCounting())
    verify(editor).putString(eq(TimerManager.START_TIME_KEY), isNull())
    verify(editor).putBoolean(eq(TimerManager.COUNTING_KEY), eq(false))
  }

  @Test
  fun getRemainingTimeIsCorrect() {
    val startTime = Date()
    timerManager.setStartTime(startTime)
    timerManager.startTimerAction()

    val remainingTime = timerManager.getRemainingTime()
    assertTrue(remainingTime > 0)
  }
}
