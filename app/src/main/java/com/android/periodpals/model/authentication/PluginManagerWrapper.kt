package com.android.periodpals.model.authentication

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.plugins.PluginManager

/**
 * Wrapper interface for the Supabase PluginManager to facilitate testing. This was necessary
 * because mocking Supabase Auth directly was not possible.
 */
interface PluginManagerWrapper {
  /**
   * Retrieves the Auth plugin from the PluginManager.
   *
   * @return The Auth plugin instance.
   */
  fun getAuthPlugin(): Auth
}

/**
 * Implementation of the PluginManagerWrapper interface.
 *
 * @property pluginManager The Supabase PluginManager instance.
 */
class PluginManagerWrapperImpl(private val pluginManager: PluginManager) : PluginManagerWrapper {
  /**
   * Retrieves the Auth plugin from the PluginManager.
   *
   * @return The Auth plugin instance.
   */
  override fun getAuthPlugin(): Auth {
    return pluginManager.getPlugin(Auth)
  }
}
