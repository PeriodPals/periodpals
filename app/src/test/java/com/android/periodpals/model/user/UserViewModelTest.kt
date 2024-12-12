package com.android.periodpals.model.user

import com.android.periodpals.MainCoroutineRule
import com.dsc.form_builder.TextFieldState
import com.dsc.form_builder.Validators
import java.text.DateFormat
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Incubating
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

  @Mock private lateinit var userModel: UserRepositorySupabase

  @Incubating private lateinit var userViewModel: UserViewModel

  @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  private lateinit var mockDateFormatStatic: MockedStatic<DateFormat>
  private lateinit var mockDateFormat: DateFormat
  private var dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE)

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    userViewModel = UserViewModel(userModel)

    mockDateFormatStatic = mockStatic(DateFormat::class.java)
    mockDateFormat = mock(DateFormat::class.java)

    mockDateFormatStatic
        .`when`<DateFormat> { DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE) }
        .thenReturn(mockDateFormat)
    `when`(mockDateFormat.setLenient(false)).thenAnswer { dateFormat.setLenient(it.getArgument(0)) }
  }

  @After
  fun tearDown() {
    mockDateFormatStatic.close()
  }

  @Test
  fun initHasSucceeded() = runTest {
    val user = UserDto("test", "test", "test", "test", 1)
    val expected = user.asUser()

    doAnswer { it.getArgument<(UserDto) -> Unit>(0)(user) }
        .`when`(userModel)
        .loadUserProfile(any<(UserDto) -> Unit>(), any<(Exception) -> Unit>())

    doAnswer { it.getArgument<(ByteArray) -> Unit>(1)(byteArrayOf(1)) }
        .`when`(userModel)
        .downloadFile(any(), any<(ByteArray) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.init()

    assertEquals(expected, userViewModel.user.value)
  }

  @Test
  fun initLoadHasFailed() = runTest {
    doAnswer { it.getArgument<(Exception) -> Unit>(1)(Exception("failed")) }
        .`when`(userModel)
        .loadUserProfile(any<(UserDto) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.init()

    assertNull(userViewModel.user.value)
  }

  @Test
  fun initDownLoadHasFailed() = runTest {
    val user = UserDto("test", "test", "test", "test", 1)

    doAnswer { it.getArgument<(UserDto) -> Unit>(0)(user) }
        .`when`(userModel)
        .loadUserProfile(any<(UserDto) -> Unit>(), any<(Exception) -> Unit>())

    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("failed")) }
        .`when`(userModel)
        .downloadFile(any(), any<(ByteArray) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.downloadFile("test")

    assertNull(userViewModel.user.value)
  }

  @Test
  fun loadUserIsSuccessful() = runTest {
    val user = UserDto("test", "test", "test", "test", 1, "fcmToken")
    val expected = user.asUser()

    doAnswer { it.getArgument<(UserDto) -> Unit>(0)(user) }
        .`when`(userModel)
        .loadUserProfile(any<(UserDto) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.loadUser()

    assertEquals(expected, userViewModel.user.value)
  }

  @Test
  fun loadUserHasFailed() = runTest {
    doAnswer { it.getArgument<(Exception) -> Unit>(1)(Exception("failed")) }
        .`when`(userModel)
        .loadUserProfile(any<(UserDto) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.loadUser()

    assertNull(userViewModel.user.value)
  }

  @Test
  fun saveUserIsSuccessful() = runTest {
    val expected = UserDto("test", "test", "test", "test", 1, "fcmToken").asUser()

    doAnswer { it.getArgument<(UserDto) -> Unit>(1)(expected.asUserDto()) }
        .`when`(userModel)
        .upsertUserProfile(any<UserDto>(), any<(UserDto) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.saveUser(expected)

    assertEquals(expected, userViewModel.user.value)
  }

  @Test
  fun saveUserHasFailed() = runTest {
    val test = UserDto("test", "test", "test", "test", 1, "fcmToken").asUser()

    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("failed")) }
        .`when`(userModel)
        .upsertUserProfile(any<UserDto>(), any<(UserDto) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.saveUser(test)

    assertNull(userViewModel.user.value)
  }

  @Test
  fun deleteUserIsSuccessful() = runTest {
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(userModel)
        .deleteUserProfile(any(), any<() -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.deleteUser("test_id")

    assertNull(userViewModel.user.value)
  }

  @Test
  fun deleteUserHasFailed() = runTest {
    val expected = userViewModel.user.value
    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("failed")) }
        .`when`(userModel)
        .deleteUserProfile(any(), any<() -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.deleteUser("test_id")

    assertEquals(expected, userViewModel.user.value)
  }

  @Test
  fun setPreferredDistanceIsSuccessful() = runTest {
    val user = UserDto("test", "test", "test", "test", 1, "fcmToken")
    val expected = user.copy(preferred_distance = 10).asUser()

    doAnswer { it.getArgument<(UserDto) -> Unit>(1)(expected.asUserDto()) }
        .`when`(userModel)
        .upsertUserProfile(any<UserDto>(), any<(UserDto) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.saveUser(expected)
    userViewModel.setPreferredDistance(10)

    assertEquals(expected, userViewModel.user.value)
  }

  @Test
  fun setPreferredDistanceHasFailed() = runTest {
    val user = UserDto("test", "test", "test", "test", 1, "fcmToken").asUser()

    doAnswer { it.getArgument<(UserDto) -> Unit>(1)(user.asUserDto()) }
        .`when`(userModel)
        .upsertUserProfile(any<UserDto>(), any<(UserDto) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.saveUser(user)

    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("failed")) }
        .`when`(userModel)
        .upsertUserProfile(any<UserDto>(), any<(UserDto) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.setPreferredDistance(10)

    assertEquals(user, userViewModel.user.value)
  }

  @Test
  fun uploadFileIsSuccessful() = runTest {
    var test = false
    doAnswer { it.getArgument<() -> Unit>(2)() }
        .`when`(userModel)
        .uploadFile(any(), any(), any<() -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.uploadFile("test", byteArrayOf(0), onSuccess = { test = true })

    assert(test)
  }

  @Test
  fun uploadFileHasFailed() = runTest {
    var test = false
    doAnswer { it.getArgument<(Exception) -> Unit>(3)(Exception("failed")) }
        .`when`(userModel)
        .uploadFile(any(), any(), any<() -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.uploadFile("test", byteArrayOf(0), onFailure = { test = true })

    assert(test)
  }

  @Test
  fun downloadFileIsSuccessful() = runTest {
    val expected = byteArrayOf(1)
    doAnswer { it.getArgument<(ByteArray) -> Unit>(1)(expected) }
        .`when`(userModel)
        .downloadFile(any(), any<(ByteArray) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.downloadFile("test")

    assertEquals(expected, userViewModel.avatar.value)
  }

  @Test
  fun downloadFileHasFailed() = runTest {
    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("failed")) }
        .`when`(userModel)
        .downloadFile(any(), any<(ByteArray) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.downloadFile("test")

    assertNull(userViewModel.avatar.value)
  }

  @Test
  fun formStateContainsCorrectFields() {
    val formState = userViewModel.formState
    assertEquals(4, formState.fields.size)
    assert(formState.fields.any { it.name == UserViewModel.NAME_STATE_NAME })
    assert(formState.fields.any { it.name == UserViewModel.DESCRIPTION_STATE_NAME })
    assert(formState.fields.any { it.name == UserViewModel.DOB_STATE_NAME })
    assert(formState.fields.any { it.name == UserViewModel.PROFILE_IMAGE_STATE_NAME })
  }

  @Test
  fun nameFieldHasCorrectValidators() {
    val nameField = userViewModel.formState.getState<TextFieldState>(UserViewModel.NAME_STATE_NAME)
    assertEquals(2, nameField.validators.size)
    assert(nameField.validators.any { it is Validators.Required })
    assert(nameField.validators.any { it is Validators.Max })
  }

  @Test
  fun descriptionFieldHasCorrectValidators() {
    val descriptionField =
        userViewModel.formState.getState<TextFieldState>(UserViewModel.DESCRIPTION_STATE_NAME)
    assertEquals(2, descriptionField.validators.size)
    assert(descriptionField.validators.any { it is Validators.Required })
    assert(descriptionField.validators.any { it is Validators.Max })
  }

  @Test
  fun dobFieldHasCorrectValidators() {
    val dobField = userViewModel.formState.getState<TextFieldState>(UserViewModel.DOB_STATE_NAME)
    assertEquals(2, dobField.validators.size)
    assert(dobField.validators.any { it is Validators.Required })
    assert(dobField.validators.any { it is Validators.Custom })
  }

  @Test
  fun profileImageFieldHasNoValidators() {
    // TODO: delete this test when the profile image field is implemented
    val profileImageField =
        userViewModel.formState.getState<TextFieldState>(UserViewModel.PROFILE_IMAGE_STATE_NAME)
    assert(profileImageField.validators.isEmpty())
  }

  @Test
  fun validateDateReturnsTrueForValidDate() {
    `when`(mockDateFormat.parse(any<String>())).thenReturn(null)
    assert(validateDate("01/01/2000"))
  }

  @Test
  fun validateDateReturnsFalseForInvalidDate() {
    `when`(mockDateFormat.parse(any<String>())).thenAnswer { dateFormat.parse(it.getArgument(0)) }
    // empty date
    assert(!validateDate(""))
    // completely off date
    assert(!validateDate("aa/bb/cccc"))
    // incomplete date
    assert(!validateDate("01/01"))
    // invalid year
    assert(!validateDate("01/01/abcd"))
    // invalid month
    assert(!validateDate("01/13/2000"))
    // invalid day
    assert(!validateDate("32/01/2000"))
  }
}
