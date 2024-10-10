package com.android.periodpals.model.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.AuthConfig
import io.github.jan.supabase.auth.deepLinkOrNull
import io.github.jan.supabase.auth.providers.builtin.Email
import junit.framework.TestCase.fail
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class AuthModelSupabaseTest {

  private lateinit var supabaseClient: SupabaseClient
  private lateinit var pluginManagerWrapper: PluginManagerWrapper
  private lateinit var auth: Auth
  private lateinit var authModel: AuthModelSupabase
  private lateinit var authConfig: AuthConfig

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    supabaseClient = mock(SupabaseClient::class.java)
    pluginManagerWrapper = mock(PluginManagerWrapper::class.java)
    auth = mock(Auth::class.java)
    authConfig = mock(AuthConfig::class.java)

    `when`(pluginManagerWrapper.getAuthPlugin()).thenReturn(auth)
    `when`(authConfig.deepLinkOrNull).thenReturn("https://example.com")
    authModel = AuthModelSupabase(supabaseClient, pluginManagerWrapper)
  }

  @Test
  fun `register success`() = runBlocking {
    doAnswer {
        val onSuccess = it.getArgument<() -> Unit>(2)
        onSuccess()
        null
      }
      .`when`(auth)
      .signUpWith(any<Email>(), any(), any())

    var successCalled = false
    authModel.register(
      "test@example.com",
      "password",
      { successCalled = true },
      { fail("Should not call onFailure") },
    )

    assert(successCalled)
  }

  @Test
  fun `register failure`() = runBlocking {
    val exception = RuntimeException("Registration failed")
    doThrow(exception).`when`(auth).signUpWith(any<Email>(), any(), any())

    var failureCalled = false
    authModel.register(
      "test@example.com",
      "password",
      { fail("Should not call onSuccess") },
      { failureCalled = true },
    )

    assert(failureCalled)
  }

  @Test
  fun `login success`() = runBlocking {
    doAnswer {
        val onSuccess = it.getArgument<() -> Unit>(2)
        onSuccess()
        null
      }
      .`when`(auth)
      .signInWith(any<Email>(), any(), any())

    var successCalled = false
    authModel.login(
      "test@example.com",
      "password",
      { successCalled = true },
      { fail("Should not call onFailure") },
    )

    assert(successCalled)
  }

  @Test
  fun `login failure`() = runBlocking {
    val exception = RuntimeException("Login failed")
    doThrow(exception).`when`(auth).signInWith(any<Email>(), any(), any())

    var failureCalled = false
    authModel.login(
      "test@example.com",
      "password",
      { fail("Should not call onSuccess") },
      { failureCalled = true },
    )

    assert(failureCalled)
  }

  @Test
  fun `logout success`() = runBlocking {
    doAnswer {
        // Simulate successful logout
        it.getArgument<Unit>(0)
        null
      }
      .`when`(auth)
      .signOut(any())

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
    `when`(auth.retrieveUser(anyString())).thenReturn(null)
    doAnswer {
        val onSuccess = it.getArgument<() -> Unit>(0)
        onSuccess()
        null
      }
      .`when`(auth)
      .refreshCurrentSession()

    var successCalled = false
    authModel.isUserLoggedIn(
      "token",
      { successCalled = true },
      { fail("Should not call onFailure") },
    )

    assert(successCalled)
  }

  @Test
  fun `isUserLoggedIn failure`() = runBlocking {
    val exception = RuntimeException("User not logged in")
    doThrow(exception).`when`(auth).retrieveUser(anyString())
    doThrow(exception).`when`(auth).refreshCurrentSession()

    var failureCalled = false
    authModel.isUserLoggedIn(
      "token",
      { fail("Should not call onSuccess") },
      { failureCalled = true },
    )

    assert(failureCalled)
  }
}
