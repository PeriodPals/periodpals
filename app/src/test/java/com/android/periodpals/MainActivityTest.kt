package com.android.periodpals

import com.android.periodpals.model.authentication.AuthenticationModelSupabase
import com.android.periodpals.model.authentication.AuthenticationViewModel
import com.android.periodpals.model.user.UserAuthenticationState
import com.android.periodpals.ui.navigation.NavigationActions
import com.android.periodpals.ui.navigation.Screen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class MainActivityTest {

    @Mock
    private lateinit var authModel: AuthenticationModelSupabase
    private lateinit var authenticationViewModel: AuthenticationViewModel

    private lateinit var navigationActions: NavigationActions

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authenticationViewModel = AuthenticationViewModel(authModel)
        navigationActions = mock(NavigationActions::class.java)
    }

    @Test
    fun navigate_to_profile_when_user_is_logged_in() = runBlocking {
        doAnswer { inv -> inv.getArgument<() -> Unit>(0)() }
            .`when`(authModel)
            .isUserLoggedIn(any<() -> Unit>(), any<(Exception) -> Unit>())

        authenticationViewModel.isUserLoggedIn()

        userAuthStateLogic(authenticationViewModel, navigationActions)

        val result =
            when (authenticationViewModel.userAuthenticationState.value) {
                is UserAuthenticationState.SuccessIsLoggedIn -> true
                else -> false
            }
        assert(result)

        verify(navigationActions).navigateTo(Screen.PROFILE)
    }

    @Test
    fun stay_in_sign_in_screen_when_user_is_not_logged_in() = runBlocking {
        doAnswer { inv -> inv.getArgument<(Exception) -> Unit>(1)(Exception("user not logged in")) }
            .`when`(authModel)
            .isUserLoggedIn(any<() -> Unit>(), any<(Exception) -> Unit>())

        authenticationViewModel.isUserLoggedIn()

        userAuthStateLogic(authenticationViewModel, navigationActions)

        val result =
            when (authenticationViewModel.userAuthenticationState.value) {
                is UserAuthenticationState.Error -> true
                else -> false
            }
        assert(result)

        verify(navigationActions, never()).navigateTo(Screen.PROFILE)
    }
}
