package com.android.periodpals.model.user

import com.android.periodpals.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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

  @Mock private lateinit var userRepoModel: UserRepositorySupabase

  @Incubating private lateinit var userViewModel: UserViewModel

  @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    userViewModel = UserViewModel(userRepoModel)
  }

  @Test
  fun `loadUserProfile updates user state`() = runTest {
    val user = UserDto("test", "test", "test", "test")
    val expected = user.asDomainModel()

    doAnswer { it.getArgument<(UserDto) -> Unit>(0)(user) }
        .`when`(userRepoModel)
        .loadUserProfile(any<(UserDto) -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.loadUserProfile()

    assertEquals(expected, userViewModel.user.value)
    /*
    val userDto = UserDto("test", "test", "test", "test")
    val user = userDto.asDomainModel()
    whenever(userRepository.loadUserProfile()).thenReturn(userDto)

    userViewModel.loadUserProfile()

    assertEquals(user, userViewModel.user.value)

     */
  }

  @Test
  fun `saveUser calls repository`() = runTest {
    val user = UserDto("test", "test", "test", "test").asDomainModel()
    var result = false

    doAnswer { result = true }
        .`when`(userRepoModel)
        .createUserProfile(any<User>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    userViewModel.saveUser(user)
    assert(result)
  }

  private fun UserDto.asDomainModel(): User {
    return User(
        displayName = this.displayName,
        imageUrl = this.imageUrl,
        description = this.description,
        age = this.age)
  }
}
