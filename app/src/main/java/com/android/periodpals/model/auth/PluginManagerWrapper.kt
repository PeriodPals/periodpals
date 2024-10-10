package com.android.periodpals.model.auth

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.plugins.PluginManager

interface PluginManagerWrapper {
  fun getAuthPlugin(): Auth
}

class PluginManagerWrapperImpl(private val pluginManager: PluginManager) : PluginManagerWrapper {
  override fun getAuthPlugin(): Auth {
    return pluginManager.getPlugin(Auth)
  }
}
