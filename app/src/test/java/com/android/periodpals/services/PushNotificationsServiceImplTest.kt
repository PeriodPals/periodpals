package com.android.periodpals.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.tasks.Tasks
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
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
  private lateinit var mockNotificationManager: NotificationManager
  private lateinit var mockNotificationManagerCompat: NotificationManagerCompat
  private lateinit var mockRemoteMessage: RemoteMessage
  private lateinit var mockFirebaseMessaging: FirebaseMessaging

  private lateinit var mockActivityCompat: MockedStatic<ActivityCompat>
  private lateinit var mockBuildVersion: MockedStatic<Build.VERSION>
  private lateinit var mockNotificationManagerCompatStatic: MockedStatic<NotificationManagerCompat>
  private lateinit var mockFirebaseMessagingStatic: MockedStatic<FirebaseMessaging>

  private val permissionCallbackCaptor: ArgumentCaptor<ActivityResultCallback<Boolean>> =
      ArgumentCaptor.forClass(ActivityResultCallback::class.java)
          as ArgumentCaptor<ActivityResultCallback<Boolean>>
  private val channelCaptor = ArgumentCaptor.forClass(NotificationChannel::class.java)

  private lateinit var pushNotificationsService: PushNotificationsServiceImpl

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)

    val realActivity = Robolectric.buildActivity(ComponentActivity::class.java).get()
    mockActivity = Mockito.spy(realActivity)
    mockLauncher = mock(ActivityResultLauncher::class.java) as ActivityResultLauncher<String>
    mockNotificationManager = mock(NotificationManager::class.java)
    mockNotificationManagerCompat = mock(NotificationManagerCompat::class.java)
    mockRemoteMessage = mock(RemoteMessage::class.java)
    mockFirebaseMessaging = mock(FirebaseMessaging::class.java)

    mockBuildVersion = mockStatic(Build.VERSION::class.java)
    mockActivityCompat = mockStatic(ActivityCompat::class.java)
    mockNotificationManagerCompatStatic = mockStatic(NotificationManagerCompat::class.java)
    mockFirebaseMessagingStatic = mockStatic(FirebaseMessaging::class.java)

    doReturn(mockLauncher)
        .`when`(mockActivity)
        .registerForActivityResult(
            any<ActivityResultContracts.RequestPermission>(), capture(permissionCallbackCaptor))
    doReturn(mockNotificationManager)
        .`when`(mockActivity)
        .getSystemService(NotificationManager::class.java)
    mockNotificationManagerCompatStatic
        .`when`<NotificationManagerCompat> { NotificationManagerCompat.from(mockActivity) }
        .thenReturn(mockNotificationManagerCompat)
    mockFirebaseMessagingStatic
        .`when`<FirebaseMessaging> { FirebaseMessaging.getInstance() }
        .thenReturn(mockFirebaseMessaging)

    pushNotificationsService = PushNotificationsServiceImpl(mockActivity)
  }

  @After
  fun tearDown() {
    mockActivityCompat.close()
    mockBuildVersion.close()
    mockNotificationManagerCompatStatic.close()
    mockFirebaseMessagingStatic.close()
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

  @Test
  fun `askPermission new API permission granted creates notification channel`() {
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

    // capture the channel to verify its properties
    verify(mockNotificationManager).createNotificationChannel(capture(channelCaptor))

    // verify the properties of the created channel
    val channel = channelCaptor.value
    assert(channel.id == "period_pals_channel_id")
    assert(channel.name == "Period Pals Channel")
    assert(channel.importance == NotificationManager.IMPORTANCE_DEFAULT)
    assert(channel.description == "Channel for Period Pals notifications")
  }

  @Test
  fun `onMessageReceived valid message shows notification`() {
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

    val title = "test title"
    val message = "test message"
    val data = mapOf("title" to title, "message" to message)
    doReturn(data).`when`(mockRemoteMessage).data

    pushNotificationsService.onMessageReceived(mockRemoteMessage)

    verify(mockNotificationManagerCompat).notify(any(), any())
  }

  @Test
  fun `onMessageReceived null title message does nothing`() {
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

    val title = null
    val message = "test message"
    val data = mapOf("title" to title, "message" to message)
    doReturn(data).`when`(mockRemoteMessage).data

    pushNotificationsService.onMessageReceived(mockRemoteMessage)

    verify(mockNotificationManagerCompat, never()).notify(any(), any())
  }

  @Test
  fun `onMessageReceived null message message does nothing`() {
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

    val title = "test title"
    val message = null
    val data = mapOf("title" to title, "message" to message)
    doReturn(data).`when`(mockRemoteMessage).data

    pushNotificationsService.onMessageReceived(mockRemoteMessage)

    verify(mockNotificationManagerCompat, never()).notify(any(), any())
  }

  @Test
  fun `createDeviceToken gets token from Firebase`() {
    `when`(mockFirebaseMessaging.token).thenReturn(Tasks.forResult("test token"))

    pushNotificationsService.createDeviceToken()

    verify(mockFirebaseMessaging).token
  }
}
