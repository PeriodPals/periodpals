package com.android.periodpals.ui.navigation

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

  // TODO: Uncomment the tests
  /**
   * Test that the navigateTo function calls the navHostController with the correct route and
   * NavOptionsBuilder.
   */
  @Test
  fun navigateToTopLevelDestinations() {
    navigationActions.navigateTo(TopLevelDestinations.ALERT)
    verify(navHostController).navigate(eq(Route.ALERT), any<NavOptionsBuilder.() -> Unit>())
    navigationActions.navigateTo(TopLevelDestinations.ALERT_LIST)
    verify(navHostController).navigate(eq(Route.ALERT_LIST), any<NavOptionsBuilder.() -> Unit>())
    navigationActions.navigateTo(TopLevelDestinations.MAP)
    verify(navHostController).navigate(eq(Route.MAP), any<NavOptionsBuilder.() -> Unit>())
    navigationActions.navigateTo(TopLevelDestinations.TIMER)
    verify(navHostController).navigate(eq(Route.TIMER), any<NavOptionsBuilder.() -> Unit>())
    navigationActions.navigateTo(TopLevelDestinations.PROFILE)
    verify(navHostController).navigate(eq(Route.PROFILE), any<NavOptionsBuilder.() -> Unit>())
  }

  /**
   * Test that the navigateTo function calls the navHostController with the correct route for the
   * auth screens.
   */
  @Test
  fun navigateToAuthScreens() {
    navigationActions.navigateTo(Screen.AUTH)
    verify(navHostController).navigate(Screen.AUTH)
    navigationActions.navigateTo(Screen.REGISTER)
    verify(navHostController).navigate(Screen.REGISTER)
    navigationActions.navigateTo(Screen.CREATE_PROFILE)
    verify(navHostController).navigate(Screen.CREATE_PROFILE)
  }

  /**
   * Test that the navigateTo function calls the navHostController with the correct route for the
   * other screens.
   */
  @Test
  fun navigateToOtherScreens() {
    navigationActions.navigateTo(Screen.ALERT)
    verify(navHostController).navigate(Screen.ALERT)
    navigationActions.navigateTo(Screen.ALERT_LIST)
    verify(navHostController).navigate(Screen.ALERT_LIST)
    navigationActions.navigateTo(Screen.MAP)
    verify(navHostController).navigate(Screen.MAP)
    navigationActions.navigateTo(Screen.TIMER)
    verify(navHostController).navigate(Screen.TIMER)
    navigationActions.navigateTo(Screen.PROFILE)
    verify(navHostController).navigate(Screen.PROFILE)
    navigationActions.navigateTo(Screen.EDIT_PROFILE)
    verify(navHostController).navigate(Screen.EDIT_PROFILE)
  }

  /** Test that the goBack function calls the navHostController to pop the back stack. */
  @Test
  fun goBackCallsController() {
    navigationActions.goBack()
    verify(navHostController).popBackStack()
  }

  /**
   * Test that the currentRoute function returns the correct route when the current destination is
   * set.
   */
  @Test
  fun currentRouteWorksWithDestination() {
    // Mock the current destination to be ALERT
    `when`(navHostController.currentDestination).thenReturn(navigationDestination)
    `when`(navigationDestination.route).thenReturn(Route.ALERT)
    assertThat(navigationActions.currentRoute(), `is`(Route.ALERT))

    // Mock the current destination to be MAP
    `when`(navigationDestination.route).thenReturn(Route.MAP)
    assertThat(navigationActions.currentRoute(), `is`(Route.MAP))

    // Mock the current destination to be null
    `when`(navHostController.currentDestination).thenReturn(null)
    assertThat(navigationActions.currentRoute(), `is`(""))
  }
}
