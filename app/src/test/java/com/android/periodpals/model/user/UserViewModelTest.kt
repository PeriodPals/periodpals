package com.android.periodpals.model.user

import com.android.periodpals.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

  @Mock private lateinit var userRepository: UserRepository

  private lateinit var userViewModel: UserViewModel

  @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    userViewModel = UserViewModel(userRepository)
  }

  @Test
  fun `loadUserProfile updates user state`() = runTest {
    val userDto = UserDto(1, "test", "test", "test", "test", "test")
    val user = userDto.asDomainModel()
    whenever(userRepository.loadUserProfile(any())).thenReturn(userDto)

    userViewModel.loadUserProfile()

    assertEquals(user, userViewModel.user.value)
  }

  @Test
  fun `saveUser calls repository`() = runTest {
    val user = User(1, "test", "test", "test", "test", "test")
    doAnswer {}.`when`(userRepository).createUserProfile(any())

    userViewModel.saveUser(user)

    // Verify that the repository's createUserProfile method was called
    org.mockito.kotlin.verify(userRepository).createUserProfile(user)
  }

  private fun UserDto.asDomainModel(): User {
    return User(
        id = this.id,
        displayName = this.displayName,
        email = this.email,
        imageUrl = this.imageUrl,
        description = this.description,
        age = this.age)
  }
}
