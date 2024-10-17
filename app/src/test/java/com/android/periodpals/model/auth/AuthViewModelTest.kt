package com.android.periodpals.model.auth

import android.content.Context
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

  @Mock
  private lateinit var mockContext: Context

  @Mock
  private lateinit var authModel: AuthModelSupabase

  private lateinit var authViewModel: AuthViewModel

  @ExperimentalCoroutinesApi
  @get:Rule
  var mainCoroutineRule = MainCoroutineRule()

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    authViewModel = AuthViewModel(authModel)
  }

  @Test
  fun `signUpWithEmail success`() = runBlocking {

    doAnswer { inv ->
      (inv.getArgument<() -> Unit>(2))()
    }
      .`when`(authModel)
      .register(
        any<String>(),
        any<String>(),
        any<() -> Unit>(),
        any<(Exception) -> Unit>()
      )


    authViewModel.signUpWithEmail(
      context = mockContext,
      userEmail = "example@email.com",
      userPassword = "password"
    )

    val result = when (authViewModel.userAuthState.value) {
      is UserAuthState.Success -> true
      else -> false
    }
    assert(result)
  }

  @Test
  fun `signInWithEmail failure`() = runBlocking {
    doAnswer { inv ->
      val onFailure = inv.getArgument<() -> Unit>(3)
      onFailure()
    }
      .`when`(authModel)
      .login(
        any<String>(),
        any<String>(),
        any<() -> Unit>(),
        any<(Exception) -> Unit>()
      )


    authViewModel.logInWithEmail(
      context = mockContext,
      userEmail = "example@email.com",
      userPassword = "password"
    )

    val result = when (authViewModel.userAuthState.value) {
      is UserAuthState.Success -> false
      is UserAuthState.Error -> true
      is UserAuthState.Loading -> false
      else -> false
    }
    assert(result)
  }

}