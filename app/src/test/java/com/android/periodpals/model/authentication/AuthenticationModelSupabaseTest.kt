package com.android.periodpals.model.authentication

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.AuthConfig
import io.github.jan.supabase.auth.deepLinkOrNull
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull

class AuthenticationModelSupabaseTest {

  @Mock private lateinit var supabaseClient: SupabaseClient

  @Mock private lateinit var pluginManagerWrapper: PluginManagerWrapper

  @Mock private lateinit var auth: Auth

  @Mock private lateinit var authConfig: AuthConfig

  @Mock private lateinit var mockUserInfo: UserInfo

  private lateinit var authModel: AuthenticationModelSupabase

  companion object {
    private val email = "test@example.com"
    private val password = "password"
    private val deepLink = "https://example.com"
    private val aud = "test_aud"
    private val id = "test_id"
  }

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    auth = mock(Auth::class.java)

    `when`(auth.config).thenReturn(authConfig)
    `when`(pluginManagerWrapper.getAuthPlugin()).thenReturn(auth)
    `when`(authConfig.deepLinkOrNull).thenReturn(deepLink)
    authModel = AuthenticationModelSupabase(supabaseClient, pluginManagerWrapper)
  }

  @Test
  fun `register success`() = runBlocking {
    `when`(auth.signUpWith(any<Email>(), anyOrNull(), any())).thenAnswer {}

    var successCalled = false
    authModel.register(
        email,
        password,
        { successCalled = true },
        { fail("Should not call onFailure") },
    )

    assert(successCalled)
  }

  @Test
  fun `register failure`() = runBlocking {
    val exception = RuntimeException("Registration failed")
    doThrow(exception).`when`(auth).signUpWith(any<Email>(), anyOrNull(), any())

    var failureCalled = false
    authModel.register(
        email,
        password,
        { fail("Should not call onSuccess") },
        { failureCalled = true },
    )

    assert(failureCalled)
  }

  @Test
  fun `login success`() = runBlocking {
    `when`(auth.signInWith(Email)).thenReturn(Unit)

    var successCalled = false
    authModel.login(
        email,
        password,
        { successCalled = true },
        { fail("Should not call onFailure") },
    )

    assert(successCalled)
  }

  @Test
  fun `login failure`() = runBlocking {
    val exception = RuntimeException("Login failed")
    doThrow(exception).`when`(auth).signInWith(any<Email>(), anyOrNull(), any())

    var failureCalled = false
    authModel.login(
        email,
        password,
        { fail("Should not call onSuccess") },
        { failureCalled = true },
    )

    assert(failureCalled)
  }

  @Test
  fun `logout success`() = runBlocking {
    `when`(auth.signOut(any())).thenReturn(Unit)

    var successCalled = false
    authModel.logout({ successCalled = true }, { fail("Should not call onFailure") })

    assert(successCalled)
  }

  @Test
  fun `logout failure`() = runBlocking {
    val exception = RuntimeException("Logout failed")
    doThrow(exception).`when`(auth).signOut(any())

    var failureCalled = false
    authModel.logout({ fail("Should not call onSuccess") }, { failureCalled = true })

    assert(failureCalled)
  }

  @Test
  fun `isUserLoggedIn success`() = runBlocking {
    `when`(auth.currentUserOrNull()).thenReturn(mockUserInfo)

    var successCalled = false
    authModel.isUserLoggedIn({ successCalled = true }, { fail("Should not call onFailure") })

    assert(successCalled)
  }

  @Test
  fun `isUserLoggedIn failure`() = runBlocking {
    val exception = RuntimeException("User not logged in")
    doThrow(exception).`when`(auth).retrieveUser(anyString())
    doThrow(exception).`when`(auth).refreshCurrentSession()

    var failureCalled = false
    authModel.isUserLoggedIn({ fail("Should not call onSuccess") }, { failureCalled = true })

    assert(failureCalled)
  }

  @Test
  fun `currentAuthUser success`() = runBlocking {
    val expected: UserInfo = UserInfo(aud = aud, id = id)
    `when`(auth.currentUserOrNull()).thenReturn(expected)

    authModel.currentAuthenticationUser(
        onSuccess = { assertEquals(it, expected) },
        onFailure = { fail("Should not call `onFailure`") },
    )
  }

  @Test
  fun `currentAuthUser failure`() = runBlocking {
    `when`(auth.currentUserOrNull()).thenReturn(null)

    authModel.currentAuthenticationUser(
        onSuccess = { fail("Should not call `onSuccess") },
        onFailure = { assert(true) },
    )
  }
}
