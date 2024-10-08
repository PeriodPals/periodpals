package com.android.sample.ui.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class NavigationActionsTest {

  private lateinit var navigationDestination: NavDestination
  private lateinit var navHostController: NavHostController
  private lateinit var navigationActions: NavigationActions

  @Before
  fun setUp() {
    navigationDestination = mock(NavDestination::class.java)
    navHostController = mock(NavHostController::class.java)
    navigationActions = NavigationActions(navHostController)
  }

  /**
   * Test that [NavigationActions.navigateTo] calls the [NavHostController.navigate] method with the
   * correct route.
   */
  @Test
  fun navigateToCallsController() {
    navigationActions.navigateTo(TopLevelDestinations.OVERVIEW)
    verify(navHostController).navigate(eq(Route.OVERVIEW), any<NavOptionsBuilder.() -> Unit>())
    navigationActions.navigateTo(TopLevelDestinations.PROFILE)
    verify(navHostController).navigate(eq(Route.PROFILE), any<NavOptionsBuilder.() -> Unit>())

    navigationActions.navigateTo(Screen.PROFILE)
    verify(navHostController).navigate(Screen.PROFILE)
    navigationActions.navigateTo(Screen.REQUEST)
    verify(navHostController).navigate(Screen.REQUEST)
    navigationActions.navigateTo(Screen.OFFER)
    verify(navHostController).navigate(Screen.OFFER)
    navigationActions.navigateTo(Screen.SETTINGS)
    verify(navHostController).navigate(Screen.SETTINGS)
    navigationActions.navigateTo(Screen.CREATE_PROFILE)
    verify(navHostController).navigate(Screen.CREATE_PROFILE)
    navigationActions.navigateTo(Screen.EDIT_PROFILE)
    verify(navHostController).navigate(Screen.EDIT_PROFILE)
  }

  @Test
  fun goBackCallsController() { // TODO: Remove if function is unnecessary
    navigationActions.goBack()
    verify(navHostController).popBackStack()
  }

  @Test
  fun currentRouteWorksWithDestination() {
    `when`(navHostController.currentDestination).thenReturn(navigationDestination)
    `when`(navigationDestination.route).thenReturn(Route.OVERVIEW)

    assertThat(navigationActions.currentRoute(), `is`(Route.OVERVIEW))
  }
}
