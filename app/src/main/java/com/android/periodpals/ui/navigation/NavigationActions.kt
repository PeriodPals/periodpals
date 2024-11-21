package com.android.periodpals.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object Route {
  const val AUTH = "Auth"
  const val ALERT = "Alert"
  const val ALERT_LIST = "Alert List"
  const val MAP = "Map"
  const val SETTINGS = "Settings"
  const val TIMER = "Timer"
  const val PROFILE = "Profile"
}

object Screen {
  const val SIGN_IN = "Auth Screen"
  const val ALERT = "Alert Screen"
  const val ALERT_LIST = "AlertList Screen"
  const val MAP = "Map Screen"
  const val TIMER = "Timer Screen"
  const val PROFILE = "Profile Screen"
  const val SIGN_UP = "Register Screen"
  const val CREATE_PROFILE = "CreateProfile Screen"
  const val EDIT_PROFILE = "EditProfile Screen"
  const val SETTINGS = "Settings Screen"
  // TODO: Add as app is being built
}

data class TopLevelDestination(val route: String, val icon: ImageVector, val textId: String)

object TopLevelDestinations {
  val ALERT =
      TopLevelDestination(route = Route.ALERT, icon = Icons.Outlined.WarningAmber, textId = "Alert")
  val ALERT_LIST =
      TopLevelDestination(
          route = Route.ALERT_LIST,
          icon = Icons.AutoMirrored.Outlined.List,
          textId = "Alert List",
      )
  val MAP = TopLevelDestination(route = Route.MAP, icon = Icons.Outlined.Map, textId = "Map")
  val TIMER =
      TopLevelDestination(
          route = Route.TIMER, icon = Icons.Outlined.HourglassEmpty, textId = "Timer")
  val PROFILE =
      TopLevelDestination(
          route = Route.PROFILE,
          icon = Icons.Outlined.AccountCircle,
          textId = "Profile",
      )
}

val LIST_TOP_LEVEL_DESTINATION =
    listOf(
        TopLevelDestinations.MAP,
        TopLevelDestinations.ALERT_LIST,
        TopLevelDestinations.ALERT,
        TopLevelDestinations.TIMER,
        TopLevelDestinations.PROFILE,
    )

open class NavigationActions(private val navController: NavHostController) {
  /**
   * Navigate to the specified [TopLevelDestination]
   *
   * @param destination The top level destination to navigate to Clear the back stack when
   *   navigating to a new destination This is useful when navigating to a new screen from the
   *   bottom navigation bar as we don't want to keep the previous screen in the back stack
   */
  open fun navigateTo(destination: TopLevelDestination) {

    navController.navigate(destination.route) {
      // Pop up to the start destination of the graph to
      // avoid building up a large stack of destinations
      popUpTo(navController.graph.findStartDestination().id) {
        saveState = true
        inclusive = true
      }

      // Avoid multiple copies of the same destination when reselecting same item
      launchSingleTop = true

      // Restore state when reselecting a previously selected item
      if (destination.route != Route.AUTH) {
        restoreState = true
      }
    }
  }

  /**
   * Navigate to the specified screen.
   *
   * @param screen The screen to navigate to
   */
  open fun navigateTo(screen: String) {
    navController.navigate(screen)
  }

  /** Navigate back to the previous screen */
  open fun goBack() {
    navController.popBackStack()
  }

  /**
   * Get the current route of the navigation controller.
   *
   * @return The current route
   */
  open fun currentRoute(): String {
    return navController.currentDestination?.route ?: ""
  }
}
