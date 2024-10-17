package com.android.periodpals.ui.map

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.periodpals.ui.theme.PeriodPalsAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMapScreenWithPermissionGranted() {
        composeTestRule.setContent {
            PeriodPalsAppTheme {
                MapScreen(locationPermissionGranted = true)
            }
        }

        composeTestRule.onNodeWithTag("MapView").assertExists()
    }

    @Test
    fun testMapScreenWithoutPermission() {
        composeTestRule.setContent {
            PeriodPalsAppTheme {
                MapScreen(locationPermissionGranted = false)
            }
        }

        composeTestRule.onNodeWithTag("MapView").assertExists()
    }
}