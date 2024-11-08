package com.android.periodpals.model.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient

class LocationViewModel(val repository: LocationRepository) : ViewModel() {
  private val query_ = MutableStateFlow("")
  val query: StateFlow<String> = query_

  private var locationSuggestions_ = MutableStateFlow(emptyList<Location>())
  val locationSuggestions: StateFlow<List<Location>> = locationSuggestions_

  // create factory
  companion object {
    val Factory: ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
          @Suppress("UNCHECKED_CAST")
          override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LocationViewModel(NominatimLocationRepository(OkHttpClient())) as T
          }
        }
  }

  fun setQuery(query: String) {
    query_.value = query

    if (query.isNotEmpty()) {
      repository.search(query, { locationSuggestions_.value = it }, {})
    }
  }
}
