package com.android.periodpals.services

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// new API refers to APIs level 33 (TIRAMISU) or above
// old API refers to APIs level below 33
@Suppress("UNCHECKED_CAST")
@RunWith(RobolectricTestRunner::class)
class PushNotificationsServiceImplTest {

  private lateinit var mockActivity: ComponentActivity
  private lateinit var mockLauncher: ActivityResultLauncher<String>

  private lateinit var mockActivityCompat: MockedStatic<ActivityCompat>
  private lateinit var mockBuildVersion: MockedStatic<Build.VERSION>

  private val permissionCallbackCaptor: ArgumentCaptor<ActivityResultCallback<Boolean>> =
      ArgumentCaptor.forClass(ActivityResultCallback::class.java)
          as ArgumentCaptor<ActivityResultCallback<Boolean>>

  private lateinit var pushNotificationsService: PushNotificationsServiceImpl

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    val realActivity = Robolectric.buildActivity(ComponentActivity::class.java).get()
    mockActivity = Mockito.spy(realActivity)
    mockLauncher = mock(ActivityResultLauncher::class.java) as ActivityResultLauncher<String>

    mockBuildVersion = mockStatic(Build.VERSION::class.java)
    mockActivityCompat = mockStatic(ActivityCompat::class.java)

    doReturn(mockLauncher)
        .`when`(mockActivity)
        .registerForActivityResult(
            any<ActivityResultContracts.RequestPermission>(), capture(permissionCallbackCaptor))

    pushNotificationsService = PushNotificationsServiceImpl(mockActivity)
  }

  @After
  fun tearDown() {
    mockActivityCompat.close()
    mockBuildVersion.close()
  }

  @Test
  @Config(sdk = [Build.VERSION_CODES.TIRAMISU - 1])
  fun `askPermission old API does not launch request dialog`() {
    pushNotificationsService.askPermission()

    // does not launch the request permission dialog
    verify(mockLauncher, never()).launch(any<String>())
  }

  @Test
  @Config(sdk = [Build.VERSION_CODES.TIRAMISU - 1])
  fun `askPermission old API sets pushPermissionsGranted to true`() {
    pushNotificationsService.askPermission()

    assert(pushNotificationsService.pushPermissionsGranted.value)
  }

  @Test
  @Config(sdk = [Build.VERSION_CODES.TIRAMISU])
  fun `askPermission new API permission already granted does not launch request dialog`() {
    // permission already granted
    mockActivityCompat
        .`when`<Int> {
          ActivityCompat.checkSelfPermission(mockActivity, Manifest.permission.POST_NOTIFICATIONS)
        }
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    pushNotificationsService.askPermission()

    // does not launch the request permission dialog
    verify(mockLauncher, never()).launch(any<String>())
  }

  @Test
  @Config(sdk = [Build.VERSION_CODES.TIRAMISU])
  fun `askPermission new API permission already granted sets pushPermissionsGranted to true`() {
    // permission already granted
    mockActivityCompat
        .`when`<Int> {
          ActivityCompat.checkSelfPermission(mockActivity, Manifest.permission.POST_NOTIFICATIONS)
        }
        .thenReturn(PackageManager.PERMISSION_GRANTED)

    pushNotificationsService.askPermission()

    assert(pushNotificationsService.pushPermissionsGranted.value)
  }

  @Test
  @Config(sdk = [Build.VERSION_CODES.TIRAMISU])
  fun `askPermission new API permission not already granted launches request dialog`() {
    // permission not granted
    mockActivityCompat
        .`when`<Int> {
          ActivityCompat.checkSelfPermission(mockActivity, Manifest.permission.POST_NOTIFICATIONS)
        }
        .thenReturn(PackageManager.PERMISSION_DENIED)

    pushNotificationsService.askPermission()

    // launches the request permission dialog
    verify(mockLauncher).launch(Manifest.permission.POST_NOTIFICATIONS)
  }

  @Test
  @Config(sdk = [Build.VERSION_CODES.TIRAMISU])
  fun `askPermission new API permission denied by user sets pushPermissionsGranted to false`() {
    // permission not already granted so that the request dialog is launched
    mockActivityCompat
        .`when`<Int> {
          ActivityCompat.checkSelfPermission(mockActivity, Manifest.permission.POST_NOTIFICATIONS)
        }
        .thenReturn(PackageManager.PERMISSION_DENIED)

    pushNotificationsService.askPermission()

    // capture the callback to simulate the user denying the permission
    verify(mockActivity)
        .registerForActivityResult(
            any<ActivityResultContracts.RequestPermission>(), capture(permissionCallbackCaptor))

    // permission denied by the user
    permissionCallbackCaptor.value.onActivityResult(false)

    assert(!pushNotificationsService.pushPermissionsGranted.value)
  }

  @Test
  @Config(sdk = [Build.VERSION_CODES.TIRAMISU])
  fun `askPermission new API permission granted by user sets pushPermissionsGranted to true`() {
    // permission not already granted so that the request dialog is launched
    mockActivityCompat
        .`when`<Int> {
          ActivityCompat.checkSelfPermission(mockActivity, Manifest.permission.POST_NOTIFICATIONS)
        }
        .thenReturn(PackageManager.PERMISSION_DENIED)

    pushNotificationsService.askPermission()

    // capture the callback to simulate the user granting the permission
    verify(mockActivity)
        .registerForActivityResult(
            any<ActivityResultContracts.RequestPermission>(), capture(permissionCallbackCaptor))

    // permission granted by the user
    permissionCallbackCaptor.value.onActivityResult(true)

    assert(pushNotificationsService.pushPermissionsGranted.value)
  }
}
