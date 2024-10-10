package com.android.periodpals.utils

import android.content.Context

class SharedPreferenceHelper(private val context: Context) {
  companion object {
    private const val PREFS_NAME = "period_pals_prefs"
  }

  fun saveStringData(key: String, value: String) {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
      putString(key, value)
      apply()
    }
  }

  fun getStringData(key: String): String? {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPref.getString(key, null)
  }

  fun clearPreferences() {
    val sharedPreferencesHelper = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    with(sharedPreferencesHelper.edit()) {
      clear()
      apply()
    }
  }
}
