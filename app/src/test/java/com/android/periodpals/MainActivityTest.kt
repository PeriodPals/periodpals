package com.android.periodpals

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

  @get:Rule
  val permissionRule: GrantPermissionRule =
      GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

  @Test
  fun testLocationPermissionGranted() {
    val scenario = ActivityScenario.launch(MainActivity::class.java)
    scenario.onActivity { activity ->
      val permissionStatus =
          ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
      assertTrue(permissionStatus == PackageManager.PERMISSION_GRANTED)
    }
  }
  /*
  @Test
  fun testLocationPermissionDenied() {
      val scenario = ActivityScenario.launch(MainActivity::class.java)
      scenario.onActivity { activity ->
          activity.onRequestPermissionsResult(1, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), intArrayOf(PackageManager.PERMISSION_DENIED))
          assertTrue(!activity.locationPermissionGranted)
      }
  }*/
}
