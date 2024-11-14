package com.android.periodpals.model.user

import com.android.periodpals.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Incubating
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

  @Mock private lateinit var userModel: UserRepositorySupabase

  @Incubating private lateinit var userViewModel: UserViewModel

  @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    userViewModel = UserViewModel(userModel)
  }

  @Test
  fun loadUserIsSuccessful() = runTest {
    val user = UserDto("test", "test", "test", "test")
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
    val expected = UserDto("test", "test", "test", "test").asUser()

    doAnswer { it.getArgument<(UserDto) -> Unit>(1)(expected.asUserDto()) }
        .`when`(userModel)
        .upsertUserProfile(any<UserDto>(), any<(UserDto) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.saveUser(expected)

    assertEquals(expected, userViewModel.user.value)
  }

  @Test
  fun saveUserHasFailed() = runTest {
    val test = UserDto("test", "test", "test", "test").asUser()

    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("failed")) }
        .`when`(userModel)
        .upsertUserProfile(any<UserDto>(), any<(UserDto) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.saveUser(test)

    assertNull(userViewModel.user.value)
  }
}
