package com.android.periodpals.model.location

interface LocationModel {
  fun search(query: String, onSuccess: (List<Location>) -> Unit, onFailure: (Exception) -> Unit)
}
