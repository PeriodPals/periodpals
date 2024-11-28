package com.android.periodpals.model.timer

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.test.runTest
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
  private lateinit var context: Context
  private lateinit var timerManager: TimerManager
  private var dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault())

  @Before
  fun setUp() {
    sharedPreferences = mock(SharedPreferences::class.java)
    editor = mock(SharedPreferences.Editor::class.java)
    `when`(sharedPreferences.edit()).thenReturn(editor)
    `when`(editor.putString(anyString(), anyString())).thenReturn(editor)
    `when`(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor)

    context = mock(Context::class.java)
    `when`(context.getSharedPreferences(eq(TimerManager.PREFERENCES), eq(Context.MODE_PRIVATE)))
        .thenReturn(sharedPreferences)

    timerManager = TimerManager(context)
  }

  @Test
  fun sharedPreferencesInitialisesCorrectly() = runTest {
    val startTime = "01/01/2024 00:00:00"
    `when`(sharedPreferences.getString(TimerManager.START_TIME_KEY, null)).thenReturn(startTime)
    `when`(sharedPreferences.getBoolean(TimerManager.COUNTING_KEY, false)).thenReturn(true)

    timerManager = TimerManager(context)

    assertNotNull(timerManager.startTime())
    assertEquals(
        dateFormat.parse(startTime)?.toString() ?: "", timerManager.startTime()?.toString())
    assertTrue(timerManager.timerCounting())
  }

  @Test
  fun startTimerActionSuccess() = runTest {
    timerManager.startTimerAction(onSuccess = {}, onFailure = { _ -> })

    assertNotNull(timerManager.startTime())
    assertNotNull(timerManager.stopTime())
    assertTrue(timerManager.timerCounting())
    verify(editor).putString(eq(TimerManager.START_TIME_KEY), anyString())
    verify(editor).putBoolean(eq(TimerManager.COUNTING_KEY), eq(true))
  }

  @Test
  fun startTimerActionFailure() = runTest {
    `when`(sharedPreferences.edit()).thenThrow(RuntimeException("Test start action failure"))

    var failureException: Exception? = null
    timerManager.startTimerAction(onSuccess = {}, onFailure = { e -> failureException = e })

    assertNotNull(failureException)
    assertEquals("Test start action failure", failureException?.message)
  }

  @Test
  fun startTimerActionWithNullValues() = runTest {
    `when`(sharedPreferences.edit()).thenReturn(null)

    var failureException: Exception? = null
    timerManager.startTimerAction(onSuccess = {}, onFailure = { e -> failureException = e })

    assertNotNull(failureException)
    assertTrue(failureException?.message?.contains("Cannot invoke") == true)
  }

  @Test
  fun resetTimerActionSuccess() = runTest {
    val startTime = Date(System.currentTimeMillis() - 3_600_000) // 1 hour ago
    timerManager.setStartTime(startTime)
    timerManager.resetTimerAction(onSuccess = {}, onFailure = { _ -> })

    assertNull(timerManager.startTime())
    assertNull(timerManager.stopTime())
    assertFalse(timerManager.timerCounting())
    verify(editor).putString(eq(TimerManager.START_TIME_KEY), isNull())
    verify(editor).putBoolean(eq(TimerManager.COUNTING_KEY), eq(false))
  }

  @Test
  fun resetTimerActionFailure() = runTest {
    val startTime = Date(System.currentTimeMillis() - 3_600_000) // 1 hour ago
    timerManager.setStartTime(startTime)
    `when`(sharedPreferences.edit()).thenThrow(RuntimeException("Test reset action failure"))

    var failureException: Exception? = null
    timerManager.resetTimerAction(onSuccess = {}, onFailure = { e -> failureException = e })

    assertNotNull(failureException)
    assertEquals("Test reset action failure", failureException?.message)
  }

  @Test
  fun resetTimerActionWithNullValues() = runTest {
    val startTime = Date(System.currentTimeMillis() - 3_600_000) // 1 hour ago
    timerManager.setStartTime(startTime)
    `when`(sharedPreferences.edit()).thenThrow(RuntimeException("Test reset action failure"))

    var failureException: Exception? = null
    timerManager.resetTimerAction(onSuccess = {}, onFailure = { e -> failureException = e })

    assertNotNull(failureException)
    assertTrue(failureException?.message?.contains("Test reset action failure") == true)
  }

  @Test
  fun stopTimerActionSuccess() = runTest {
    val startTime = Date(System.currentTimeMillis() - 3_600_000) // 1 hour ago
    timerManager.setStartTime(startTime)
    var elapsedTime = 0L
    timerManager.stopTimerAction(onSuccess = { elapsedTime = it }, onFailure = { _ -> })

    assertTrue(elapsedTime > 0)
    assertNull(timerManager.startTime())
    assertNull(timerManager.stopTime())
    assertFalse(timerManager.timerCounting())
    verify(editor).putString(eq(TimerManager.START_TIME_KEY), isNull())
    verify(editor).putBoolean(eq(TimerManager.COUNTING_KEY), eq(false))
  }

  @Test
  fun stopTimerActionFailure() = runTest {
    val startTime = Date(System.currentTimeMillis() - 3_600_000) // 1 hour ago
    timerManager.setStartTime(startTime)
    `when`(sharedPreferences.edit()).thenThrow(RuntimeException("Test stop action failure"))

    var failureException: Exception? = null
    timerManager.stopTimerAction(onSuccess = {}, onFailure = { e -> failureException = e })

    assertNotNull(failureException)
    assertEquals("Test stop action failure", failureException?.message)
  }

  @Test
  fun stopTimerActionWithNullValues() = runTest {
    timerManager.setStartTime(null)
    var failureException: Exception? = null
    timerManager.stopTimerAction(onSuccess = {}, onFailure = { e -> failureException = e })

    assertNotNull(failureException)
    assertTrue(failureException?.message?.contains("Start time is null") == true)
  }

  @Test
  fun getRemainingTimeIsCorrect() = runTest {
    val startTime = Date()
    timerManager.setStartTime(startTime)
    timerManager.startTimerAction()

    val remainingTime = timerManager.getRemainingTime()
    assertTrue(remainingTime > 0)
  }
}
