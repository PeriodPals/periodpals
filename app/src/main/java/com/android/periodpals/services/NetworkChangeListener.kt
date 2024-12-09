package com.android.periodpals.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "NetworkChangeListener"

/**
 * Listens to changes in the network connectivity and exposes whether a network connection is
 * available or not through the [isNetworkAvailable] StateFlow.
 *
 * @param context Activity context
 */
class NetworkChangeListener(context: Context) {
  private var _isNetworkAvailable = MutableStateFlow(false)
  val isNetworkAvailable = _isNetworkAvailable.asStateFlow()

  private val connectivityManager =
      context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  private val networkCallback =
      object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
          super.onAvailable(network)
          Log.d(TAG, "Network is available")
          _isNetworkAvailable.value = true
        }

        override fun onLost(network: Network) {
          super.onLost(network)
          Log.d(TAG, "Network is lost")
          _isNetworkAvailable.value = false
        }
      }

  /**
   * Starts listening to changes in the network.
   */
  fun startListening() {
    Log.d(TAG, "Started listening to network changes")
    connectivityManager.registerDefaultNetworkCallback(networkCallback)
  }

  /**
   * Stops listening for changes in the network.
   */
  fun stopListening() {
    Log.d(TAG, "Stopped listening to network changes")
    connectivityManager.unregisterNetworkCallback(networkCallback)
  }
}
