package com.android.periodpals.model.authentication

import com.android.periodpals.MainCoroutineRule
import com.android.periodpals.model.user.AuthenticationUserData
import com.android.periodpals.model.user.UserAuthenticationState
import com.dsc.form_builder.TextFieldState
import com.dsc.form_builder.Validators
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    private val googleIdToken = "test_token"
    private val rawNonce = "test_nonce"
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

  @Test
  fun `signInWithGoogle success`() = runBlocking {
    doAnswer { inv -> inv.getArgument<() -> Unit>(2)() }
        .`when`(authModel)
        .loginGoogle(any<String>(), any<String>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    authenticationViewModel.loginWithGoogle(googleIdToken, rawNonce)

    val result =
        when (authenticationViewModel.userAuthenticationState.value) {
          is UserAuthenticationState.Success -> true
          else -> false
        }
    assert(result)
  }

  @Test
  fun `signInWithGoogle failure`() = runBlocking {
    doAnswer { inv ->
          val onFailure = inv.getArgument<(Exception) -> Unit>(3)
          onFailure(Exception("sign in failure"))
        }
        .`when`(authModel)
        .loginGoogle(any<String>(), any<String>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    authenticationViewModel.loginWithGoogle(googleIdToken, rawNonce)

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
  fun testGenerateHashCodeFormat() {
    val rawNonce = "testNonce"
    val hashCode = authenticationViewModel.generateHashCode(rawNonce)

    // Assert that the hash code is a hexadecimal string
    val hexPattern = Regex("^[a-fA-F0-9]+$")
    assertTrue("Hash code is not in hexadecimal format", hexPattern.matches(hashCode))

    // Assert that the hash code has the expected length (64 characters for SHA-256)
    val expectedLength = 64
    assertTrue(
        "Hash code length is not $expectedLength characters",
        hashCode.length == expectedLength,
    )
  }

  @Test
  fun formStateContainsCorrectFields() {
    val formState = authenticationViewModel.formState
    assertEquals(4, formState.fields.size)
    assertTrue(formState.fields.any { it.name == AuthenticationViewModel.EMAIL_STATE_NAME })
    assertTrue(
        formState.fields.any { it.name == AuthenticationViewModel.PASSWORD_SIGNUP_STATE_NAME })
    assertTrue(
        formState.fields.any { it.name == AuthenticationViewModel.CONFIRM_PASSWORD_STATE_NAME })
    assertTrue(
        formState.fields.any { it.name == AuthenticationViewModel.PASSWORD_LOGIN_STATE_NAME })
  }

  @Test
  fun emailFieldHasCorrectValidators() {
    val emailField =
        authenticationViewModel.formState.getState<TextFieldState>(
            AuthenticationViewModel.EMAIL_STATE_NAME)
    assertEquals(3, emailField.validators.size)
    assertTrue(emailField.validators.any { it is Validators.Email })
    assertTrue(emailField.validators.any { it is Validators.Required })
    assertTrue(emailField.validators.any { it is Validators.Max })
  }

  @Test
  fun passwordSignupFieldHasCorrectValidators() {
    val passwordSignupField =
        authenticationViewModel.formState.getState<TextFieldState>(
            AuthenticationViewModel.PASSWORD_SIGNUP_STATE_NAME)
    assertEquals(7, passwordSignupField.validators.size)
    assertTrue(passwordSignupField.validators.any { it is Validators.Min })
    assertTrue(passwordSignupField.validators.any { it is Validators.Max })
    assertTrue(passwordSignupField.validators.any { it is Validators.Required })
    assertTrue(passwordSignupField.validators.any { it is Validators.Custom })
  }

  @Test
  fun confirmPasswordFieldHasCorrectValidators() {
    val confirmPasswordField =
        authenticationViewModel.formState.getState<TextFieldState>(
            AuthenticationViewModel.CONFIRM_PASSWORD_STATE_NAME)
    assertEquals(7, confirmPasswordField.validators.size)
    assertTrue(confirmPasswordField.validators.any { it is Validators.Min })
    assertTrue(confirmPasswordField.validators.any { it is Validators.Max })
    assertTrue(confirmPasswordField.validators.any { it is Validators.Required })
    assertTrue(confirmPasswordField.validators.any { it is Validators.Custom })
  }

  @Test
  fun passwordLoginFieldHasCorrectValidators() {
    val passwordLoginField =
        authenticationViewModel.formState.getState<TextFieldState>(
            AuthenticationViewModel.PASSWORD_LOGIN_STATE_NAME)
    assertEquals(2, passwordLoginField.validators.size)
    assertTrue(passwordLoginField.validators.any { it is Validators.Required })
    assertTrue(passwordLoginField.validators.any { it is Validators.Max })
  }
}
