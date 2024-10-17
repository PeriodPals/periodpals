package com.android.periodpals.ui.navigation

import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

class NavigationActionsTest {
    private lateinit var navHostController: NavHostController
    private lateinit var navigationActions: NavigationActions
    private lateinit var navGraph: NavGraph
    private lateinit var startDestination: NavDestination

    @Before
    fun setUp() {
        navHostController = mock(NavHostController::class.java)
        navigationActions = NavigationActions(navHostController)

        //Mock Graph and Destination
        navGraph = mock(NavGraph::class.java)
        startDestination = mock(NavDestination::class.java)

        whenever(navGraph.findStartDestination()).thenReturn(startDestination)
        whenever(navHostController.graph).thenReturn(navGraph)
    }

    @Test
    fun navigateToCallsController(){
        // navController.navigate() is called with the correct destination route
        navigationActions.navigateTo(TopLevelDestinations.ALERT)
        verify(navHostController).navigate(eq(Route.ALERT), any<NavOptionsBuilder.() -> Unit>())

        // navigating to a specific screen results in the correct navController.navigate() call
        navigationActions.navigateTo(Screen.PROFILE)
        verify(navHostController).navigate(Screen.PROFILE)
    }

    @Test
    fun goBackCallsController(){
        navigationActions.goBack()
        // Verify that popBackStack is called on the navController
        verify(navHostController).popBackStack()
    }

    @Test
    fun currentRouteReturnsCorrectRoute(){
        // Mock the current destination to be the start destination
        whenever(navHostController.currentDestination).thenReturn(startDestination)
        whenever(startDestination.route).thenReturn(Route.ALERT)

        // Verify that the currentRoute() function returns the correct route
        assert(navigationActions.currentRoute() == Route.ALERT)
    }

    @Test
    fun currentRouteEmpty(){
        // Return null for current destination
        whenever(navHostController.currentDestination).thenReturn(null)

        // Assert that currentRoute returns an empty string
        assert(navigationActions.currentRoute() == "")
    }


}