package com.android.periodpals.model.location

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient

private const val TAG = "LocationViewModel"

/**
 * ViewModel responsible for managing and providing location data for UI components.
 *
 * @property repository The repository used to perform location-based search operations.
 *
 * This ViewModel maintains the user's search query and provides location suggestions based on the
 * query input. It exposes StateFlows to observe query changes and location suggestions in a
 * reactive way.
 */
class LocationViewModel(val repository: LocationModel) : ViewModel() {

  private val _query = MutableStateFlow("")
  val query: StateFlow<String> = _query

  private var _locationSuggestions = MutableStateFlow(emptyList<Location>())
  val locationSuggestions: StateFlow<List<Location>> get() = _locationSuggestions

  private val _address = MutableStateFlow("")
  val address: StateFlow<String> get() = _address

  // create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LocationViewModel(LocationModelNominatim(OkHttpClient())) as T
          }
        }
  }

  /**
   * Sets the query for the location search. Updates the [query] StateFlow and initiates a search
   * request through the repository if the query is not empty.
   *
   * @param query The search string input by the user.
   */
  fun setQuery(query: String) {
    _query.value = query

    if (query.isNotEmpty()) {
      repository.search(
          query,
          {
            _locationSuggestions.value = it
            Log.d("SearchSuccess", "Successfully fetched location suggestions for query: $query")
          },
          { Log.d("SearchError", "Failed to fetch location suggestions for query: $query") })
    }
  }

  fun getAddressFromCoordinates(location: Location) {
    viewModelScope.launch {
      val result = repository.reverseSearch(
        gpsCoordinates = location,
        onSuccess = { resultAddress ->
          Log.d(TAG, "Successfully fetched address related to coordinates: (${location.latitude}, ${location.longitude}")
          _address.value = resultAddress
        },
        onFailure = {
          Log.d(TAG, "Failed to fetch address related to the coordinates: (${location.latitude}, ${location.longitude})")
        }
      )
    }
  }
}
