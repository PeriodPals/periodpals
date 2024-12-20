package com.android.periodpals.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NetworkChangeListenerTest {

  @Mock lateinit var mockContext: Context

  @Mock lateinit var mockConnectivityManager: ConnectivityManager

  private lateinit var networkChangeListener: NetworkChangeListener

  @Before
  fun setup() {

    MockitoAnnotations.openMocks(this)

    whenever(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE))
        .thenReturn(mockConnectivityManager)

    networkChangeListener = NetworkChangeListener(context = mockContext)
  }

  @Test
  fun `onAvailable sets isNetworkAvailable to true`() = runTest {
    val callbackCaptor = ArgumentCaptor.forClass(ConnectivityManager.NetworkCallback::class.java)

    // Start listening
    networkChangeListener.startListening()
    verify(mockConnectivityManager).registerDefaultNetworkCallback(callbackCaptor.capture())

    // Trigger the onAvailable callback
    callbackCaptor.value.onAvailable(mock(Network::class.java))

    // Check that the value was indeed updated to true
    assert(networkChangeListener.isNetworkAvailable.value)
  }

  @Test
  fun `onUnavailable sets isNetworkAvailable to false`() = runTest {
    val callbackCaptor = ArgumentCaptor.forClass(ConnectivityManager.NetworkCallback::class.java)

    // Start listening
    networkChangeListener.startListening()

    // Stop listening
    networkChangeListener.stopListening()
    verify(mockConnectivityManager).unregisterNetworkCallback(callbackCaptor.capture())

    // Trigger the unAvailable callback
    callbackCaptor.value.onLost(mock(Network::class.java))

    // Check that the value was updated to false
    assert(!networkChangeListener.isNetworkAvailable.value)
  }
}
