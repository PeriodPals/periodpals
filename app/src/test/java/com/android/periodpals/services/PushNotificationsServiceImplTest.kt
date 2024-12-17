package com.android.periodpals.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.User
import com.android.periodpals.model.user.UserViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
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
import org.mockito.kotlin.eq
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
  private lateinit var mockAuthenticationViewModel: AuthenticationViewModel
  private lateinit var mockUserViewModel: UserViewModel
  private lateinit var mockTokenTask: Task<String>

  private lateinit var mockActivityCompat: MockedStatic<ActivityCompat>
  private lateinit var mockBuildVersion: MockedStatic<Build.VERSION>
  private lateinit var mockNotificationManagerCompatStatic: MockedStatic<NotificationManagerCompat>
  private lateinit var mockFirebaseMessagingStatic: MockedStatic<FirebaseMessaging>

  private val permissionCallbackCaptor: ArgumentCaptor<ActivityResultCallback<Boolean>> =
      ArgumentCaptor.forClass(ActivityResultCallback::class.java)
          as ArgumentCaptor<ActivityResultCallback<Boolean>>
  private val channelCaptor = ArgumentCaptor.forClass(NotificationChannel::class.java)
  private val onCompleteListenerCaptor =
      ArgumentCaptor.forClass(OnCompleteListener::class.java)
          as ArgumentCaptor<OnCompleteListener<String>>

  private lateinit var pushNotificationsService: PushNotificationsServiceImpl

  companion object {
    private const val NOTIFICATION_TITLE = "test title"
    private const val NOTIFICATION_MESSAGE = "test message"
    private const val DEVICE_TOKEN = "test token"
    private const val NOTIFICATION_CHANNEL_ID = "period_pals_channel_id"
    private const val NOTIFICATION_CHANNEL_NAME = "Period Pals Channel"
    private const val NOTIFICATION_CHANNEL_DESCRIPTION = "Channel for Period Pals notifications"
    private const val USER_NAME = "test name"
    private const val USER_IMAGE_URL = "test image url"
    private const val USER_DESCRIPTION = "test description"
    private const val USER_DOB = "test dob"
    private const val USER_PREF_DISTANCE = 500
  }

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
    mockAuthenticationViewModel = mock(AuthenticationViewModel::class.java)
    mockUserViewModel = mock(UserViewModel::class.java)
    mockTokenTask = mock(Task::class.java) as Task<String>

    mockBuildVersion = mockStatic(Build.VERSION::class.java)
    mockActivityCompat = mockStatic(ActivityCompat::class.java)
    mockNotificationManagerCompatStatic = mockStatic(NotificationManagerCompat::class.java)
    mockFirebaseMessagingStatic = mockStatic(FirebaseMessaging::class.java)

    doReturn(mockLauncher)
        .`when`(mockActivity)
        .registerForActivityResult(
            any<ActivityResultContracts.RequestPermission>(),
            capture(permissionCallbackCaptor),
        )
    doReturn(mockNotificationManager)
        .`when`(mockActivity)
        .getSystemService(NotificationManager::class.java)
    mockNotificationManagerCompatStatic
        .`when`<NotificationManagerCompat> { NotificationManagerCompat.from(mockActivity) }
        .thenReturn(mockNotificationManagerCompat)
    mockFirebaseMessagingStatic
        .`when`<FirebaseMessaging> { FirebaseMessaging.getInstance() }
        .thenReturn(mockFirebaseMessaging)
    `when`(mockFirebaseMessaging.token).thenReturn(mockTokenTask)
    `when`(mockTokenTask.addOnCompleteListener(capture(onCompleteListenerCaptor))).thenAnswer {
      onCompleteListenerCaptor.value.onComplete(mockTokenTask)
      mockTokenTask
    }
    `when`(mockTokenTask.result).thenReturn(DEVICE_TOKEN)

    pushNotificationsService =
        PushNotificationsServiceImpl(mockActivity, mockAuthenticationViewModel, mockUserViewModel)
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
            any<ActivityResultContracts.RequestPermission>(),
            capture(permissionCallbackCaptor),
        )

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
            any<ActivityResultContracts.RequestPermission>(),
            capture(permissionCallbackCaptor),
        )

    // permission granted by the user
    permissionCallbackCaptor.value.onActivityResult(true)

    assert(pushNotificationsService.pushPermissionsGranted.value)
  }

  @Test
  @Config(sdk = [Build.VERSION_CODES.TIRAMISU])
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
            any<ActivityResultContracts.RequestPermission>(),
            capture(permissionCallbackCaptor),
        )

    // permission granted by the user
    permissionCallbackCaptor.value.onActivityResult(true)

    // capture the channel to verify its properties
    verify(mockNotificationManager).createNotificationChannel(capture(channelCaptor))

    // verify the properties of the created channel
    val channel = channelCaptor.value
    assert(channel.id == NOTIFICATION_CHANNEL_ID)
    assert(channel.name == NOTIFICATION_CHANNEL_NAME)
    assert(channel.importance == NotificationManager.IMPORTANCE_DEFAULT)
    assert(channel.description == NOTIFICATION_CHANNEL_DESCRIPTION)
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
            any<ActivityResultContracts.RequestPermission>(),
            capture(permissionCallbackCaptor),
        )

    // permission granted by the user
    permissionCallbackCaptor.value.onActivityResult(true)

    val data = mapOf("title" to NOTIFICATION_TITLE, "message" to NOTIFICATION_MESSAGE)
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
            any<ActivityResultContracts.RequestPermission>(),
            capture(permissionCallbackCaptor),
        )

    // permission granted by the user
    permissionCallbackCaptor.value.onActivityResult(true)

    val data = mapOf("title" to null, "message" to NOTIFICATION_MESSAGE)
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
            any<ActivityResultContracts.RequestPermission>(),
            capture(permissionCallbackCaptor),
        )

    // permission granted by the user
    permissionCallbackCaptor.value.onActivityResult(true)

    val data = mapOf("title" to NOTIFICATION_TITLE, "message" to null)
    doReturn(data).`when`(mockRemoteMessage).data

    pushNotificationsService.onMessageReceived(mockRemoteMessage)

    verify(mockNotificationManagerCompat, never()).notify(any(), any())
  }

  @Test
  fun `createDeviceToken gets token from Firebase`() {
    `when`(mockFirebaseMessaging.token).thenReturn(Tasks.forResult(DEVICE_TOKEN))

    pushNotificationsService.createDeviceToken()

    verify(mockFirebaseMessaging).token
  }

  @Test
  fun `onNewToken loadUser success calls userVM saveUser`() {
    `when`(mockUserViewModel.loadUser(any(), any())).thenAnswer {
      val onSuccess = it.arguments[0] as () -> Unit
      onSuccess()
    }
    `when`(mockUserViewModel.user)
        .thenReturn(
            mutableStateOf(
                User(USER_NAME, USER_IMAGE_URL, USER_DESCRIPTION, USER_DOB, USER_PREF_DISTANCE)))

    pushNotificationsService.onNewToken(DEVICE_TOKEN)

    verify(mockUserViewModel)
        .saveUser(
            eq(
                User(
                    USER_NAME,
                    USER_IMAGE_URL,
                    USER_DESCRIPTION,
                    USER_DOB,
                    USER_PREF_DISTANCE,
                    DEVICE_TOKEN,
                )),
            any(),
            any(),
        )
  }

  @Test
  fun `onNewToken loadUser failure does not call userVM saveUser`() {
    `when`(mockUserViewModel.loadUser(any(), any())).thenAnswer {
      val onFailure = it.arguments[1] as (Exception) -> Unit
      onFailure(Exception("test exception"))
    }

    pushNotificationsService.onNewToken(DEVICE_TOKEN)

    verify(mockUserViewModel, never()).saveUser(any(), any(), any())
  }

  @SuppressLint("CheckResult")
  @Test
  fun `createDeviceToken token task success loadUser success calls userVM saveUser with correct attributes`() {
    `when`(mockUserViewModel.loadUser(any(), any(), any())).thenAnswer {
      val onSuccess = it.arguments[1] as () -> Unit
      onSuccess()
    }
    `when`(mockUserViewModel.user)
        .thenReturn(
            mutableStateOf(
                User(USER_NAME, USER_IMAGE_URL, USER_DESCRIPTION, USER_DOB, USER_PREF_DISTANCE)))
    `when`(mockAuthenticationViewModel.authUserData.value!!.uid).thenReturn("test uid")

    `when`(mockTokenTask.isSuccessful).thenReturn(true)

    pushNotificationsService.createDeviceToken()

    verify(mockUserViewModel)
        .saveUser(
            eq(
                User(
                    USER_NAME,
                    USER_IMAGE_URL,
                    USER_DESCRIPTION,
                    USER_DOB,
                    USER_PREF_DISTANCE,
                    DEVICE_TOKEN)),
            any(),
            any(),
        )
  }

  @Test
  fun `createDeviceToken token task success loadUser failure does not call userVM saveUser`() {
    `when`(mockUserViewModel.loadUser(any(), any(), any())).thenAnswer {
      val onFailure = it.arguments[2] as (Exception) -> Unit
      onFailure(Exception("test exception"))
    }
    `when`(mockTokenTask.isSuccessful).thenReturn(true)

    pushNotificationsService.createDeviceToken()

    verify(mockUserViewModel, never()).saveUser(any(), any(), any())
  }

  @Test
  fun `createDeviceToken token task failure does not call userVm saveUser`() {
    `when`(mockTokenTask.isSuccessful).thenReturn(false)

    pushNotificationsService.createDeviceToken()

    verify(mockUserViewModel, never()).saveUser(any(), any(), any())
  }
}
