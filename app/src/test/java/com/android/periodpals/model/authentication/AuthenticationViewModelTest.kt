package com.android.periodpals.model.authentication

import com.android.periodpals.MainCoroutineRule
import com.android.periodpals.model.user.AuthenticationUserData
import com.android.periodpals.model.user.UserAuthenticationState
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer

@OptIn(ExperimentalCoroutinesApi::class)
class AuthenticationViewModelTest {

  @Mock private lateinit var authModel: AuthenticationModelSupabase
  private lateinit var authenticationViewModel: AuthenticationViewModel

  @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  companion object {
    private val email = "test@example.com"
    private val password = "password"
    private val aud = "test_aud"
    private val id = "test_id"
  }

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    authenticationViewModel = AuthenticationViewModel(authModel)
  }

  @Test
  fun `signUpWithEmail success`() = runBlocking {
    doAnswer { inv -> (inv.getArgument<() -> Unit>(2))() }
        .`when`(authModel)
        .register(any<String>(), any<String>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    authenticationViewModel.signUpWithEmail(userEmail = email, userPassword = password)

    val result =
        when (authenticationViewModel.userAuthenticationState.value) {
          is UserAuthenticationState.Success -> true
          else -> false
        }
    assert(result)
  }

  @Test
  fun `signUpWithEmail failure`() = runBlocking {
    doAnswer { inv -> (inv.getArgument<(Exception) -> Unit>(3))(Exception("signup failure")) }
        .`when`(authModel)
        .register(any<String>(), any<String>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    authenticationViewModel.signUpWithEmail(userEmail = email, userPassword = password)

    val result =
        when (authenticationViewModel.userAuthenticationState.value) {
          is UserAuthenticationState.Error -> true
          else -> false
        }
    assert(result)
  }

  @Test
  fun `signInWithEmail success`() = runBlocking {
    doAnswer { inv -> inv.getArgument<() -> Unit>(2)() }
        .`when`(authModel)
        .login(any<String>(), any<String>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    authenticationViewModel.logInWithEmail(userEmail = email, userPassword = password)

    val result =
        when (authenticationViewModel.userAuthenticationState.value) {
          is UserAuthenticationState.Success -> true
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

    authenticationViewModel.logInWithEmail(userEmail = email, userPassword = password)

    val result =
        when (authenticationViewModel.userAuthenticationState.value) {
          is UserAuthenticationState.Success -> false
          is UserAuthenticationState.Error -> true
          is UserAuthenticationState.Loading -> false
          else -> false
        }
    assert(result)
  }

  @Test
  fun `logOut success`() = runBlocking {
    doAnswer { inv -> inv.getArgument<() -> Unit>(0)() }
        .`when`(authModel)
        .logout(any<() -> Unit>(), any<(Exception) -> Unit>())

    authenticationViewModel.logOut()

    val result =
        when (authenticationViewModel.userAuthenticationState.value) {
          is UserAuthenticationState.Success -> true
          else -> false
        }
    assert(result)
  }

  @Test
  fun `logOut failure`() = runBlocking {
    doAnswer { inv -> inv.getArgument<(Exception) -> Unit>(1)(Exception("logout failure")) }
        .`when`(authModel)
        .logout(any<() -> Unit>(), any<(Exception) -> Unit>())

    authenticationViewModel.logOut()

    val result =
        when (authenticationViewModel.userAuthenticationState.value) {
          is UserAuthenticationState.Error -> true
          else -> false
        }
    assert(result)
  }

  @Test
  fun `isUserLoggedIn success`() = runBlocking {
    doAnswer { inv -> inv.getArgument<() -> Unit>(0)() }
        .`when`(authModel)
        .isUserLoggedIn(any<() -> Unit>(), any<(Exception) -> Unit>())

    authenticationViewModel.isUserLoggedIn()

    val result =
        when (authenticationViewModel.userAuthenticationState.value) {
          is UserAuthenticationState.Success -> true
          else -> false
        }
    assert(result)
  }

  @Test
  fun `isUserLoggedIn failure`() = runBlocking {
    doAnswer { inv -> inv.getArgument<(Exception) -> Unit>(1)(Exception("user not logged in")) }
        .`when`(authModel)
        .isUserLoggedIn(any<() -> Unit>(), any<(Exception) -> Unit>())

    authenticationViewModel.isUserLoggedIn()

    val result =
        when (authenticationViewModel.userAuthenticationState.value) {
          is UserAuthenticationState.Error -> true
          else -> false
        }
    assert(result)
  }

  @Test
  fun `loadAuthUserData success`() = runBlocking {
    val userInfo: UserInfo = UserInfo(aud = aud, id = id, email = email)
    val expected: AuthenticationUserData = AuthenticationUserData(uid = id, email = email)

    doAnswer { inv -> inv.getArgument<(UserInfo) -> Unit>(0)(userInfo) }
        .`when`(authModel)
        .currentAuthenticationUser(any<(UserInfo) -> Unit>(), any<(Exception) -> Unit>())

    authenticationViewModel.loadAuthenticationUserData()

    assertEquals(expected, authenticationViewModel.authUserData.value)
  }

  @Test
  fun `loadAuthUserData failure`() = runBlocking {
    doAnswer { inv -> inv.getArgument<(Exception) -> Unit>(1)(Exception("Model Failed")) }
        .`when`(authModel)
        .currentAuthenticationUser(any<(UserInfo) -> Unit>(), any<(Exception) -> Unit>())

    authenticationViewModel.loadAuthenticationUserData()

    assertNull(authenticationViewModel.authUserData.value)
  }
}
