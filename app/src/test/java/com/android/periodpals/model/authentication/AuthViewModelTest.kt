package com.android.periodpals.model.authentication

import com.android.periodpals.MainCoroutineRule
import com.android.periodpals.model.user.UserAuthState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

  @Mock private lateinit var authModel: AuthModelSupabase
  private lateinit var authViewModel: AuthViewModel

  @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  companion object {
    private val email = "test@example.com"
    private val password = "password"
  }

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    authViewModel = AuthViewModel(authModel)
  }

  @Test
  fun `signUpWithEmail success`() = runBlocking {
    doAnswer { inv -> (inv.getArgument<() -> Unit>(2))() }
        .`when`(authModel)
        .register(any<String>(), any<String>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    authViewModel.signUpWithEmail(userEmail = email, userPassword = password)

    val result =
        when (authViewModel.userAuthState.value) {
          is UserAuthState.Success -> true
          else -> false
        }
    assert(result)
  }

  @Test
  fun `signUpWithEmail failure`() = runBlocking {
    doAnswer { inv -> (inv.getArgument<(Exception) -> Unit>(3))(Exception("signup failure")) }
        .`when`(authModel)
        .register(any<String>(), any<String>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    authViewModel.signUpWithEmail(userEmail = email, userPassword = password)

    val result =
        when (authViewModel.userAuthState.value) {
          is UserAuthState.Error -> true
          else -> false
        }
    assert(result)
  }

  @Test
  fun `signInWithEmail success`() = runBlocking {
    doAnswer { inv -> inv.getArgument<() -> Unit>(2)() }
        .`when`(authModel)
        .login(any<String>(), any<String>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    authViewModel.logInWithEmail(userEmail = email, userPassword = password)

    val result =
        when (authViewModel.userAuthState.value) {
          is UserAuthState.Success -> true
          else -> false
        }
    assert(result)
  }

  @Test
  fun `signInWithEmail failure`() = runBlocking {
    doAnswer { inv ->
          val onFailure = inv.getArgument<(Exception) -> Unit>(3)
          onFailure(Exception("sign in failure"))
        }
        .`when`(authModel)
        .login(any<String>(), any<String>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    authViewModel.logInWithEmail(userEmail = email, userPassword = password)

    val result =
        when (authViewModel.userAuthState.value) {
          is UserAuthState.Success -> false
          is UserAuthState.Error -> true
          is UserAuthState.Loading -> false
          else -> false
        }
    assert(result)
  }

  @Test
  fun `logOut success`() = runBlocking {
    doAnswer { inv -> inv.getArgument<() -> Unit>(0)() }
        .`when`(authModel)
        .logout(any<() -> Unit>(), any<(Exception) -> Unit>())

    authViewModel.logOut()

    val result =
        when (authViewModel.userAuthState.value) {
          is UserAuthState.Success -> true
          else -> false
        }
    assert(result)
  }

  @Test
  fun `logOut failure`() = runBlocking {
    doAnswer { inv -> inv.getArgument<(Exception) -> Unit>(1)(Exception("logout failure")) }
        .`when`(authModel)
        .logout(any<() -> Unit>(), any<(Exception) -> Unit>())

    authViewModel.logOut()

    val result =
        when (authViewModel.userAuthState.value) {
          is UserAuthState.Error -> true
          else -> false
        }
    assert(result)
  }

  @Test
  fun `isUserLoggedIn success`() = runBlocking {
    doAnswer { inv -> inv.getArgument<() -> Unit>(1)() }
        .`when`(authModel)
        .isUserLoggedIn(any<() -> Unit>(), any<(Exception) -> Unit>())

    authViewModel.isUserLoggedIn()

    val result =
        when (authViewModel.userAuthState.value) {
          is UserAuthState.Success -> true
          else -> false
        }
    assert(result)
  }

  @Test
  fun `isUserLoggedIn failure`() = runBlocking {
    doAnswer { inv -> inv.getArgument<(Exception) -> Unit>(2)(Exception("user not logged in")) }
        .`when`(authModel)
        .isUserLoggedIn(any<() -> Unit>(), any<(Exception) -> Unit>())

    authViewModel.isUserLoggedIn()

    val result =
        when (authViewModel.userAuthState.value) {
          is UserAuthState.Error -> true
          else -> false
        }
    assert(result)
  }
}
