package com.android.periodpals.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkChangeListener(private val context: Context) {
  private var _isNetworkAvailable = MutableStateFlow(false)
  val isNetworkAvailable = _isNetworkAvailable.asStateFlow()

  private val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  private val networkCallback =
      object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
          super.onAvailable(network)
          _isNetworkAvailable.value = true
        }

        override fun onUnavailable() {
          super.onUnavailable()
          _isNetworkAvailable.value = false
        }
      }

  fun startListening() {
    connectivityManager.registerDefaultNetworkCallback(networkCallback)
  }

  fun stopListening() {
    connectivityManager.unregisterNetworkCallback(networkCallback)
  }
}
