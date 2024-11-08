package com.android.periodpals.model.location

interface LocationRepository {
  fun search(query: String, onSuccess: (List<Location>) -> Unit, onFailure: (Exception) -> Unit)
}
