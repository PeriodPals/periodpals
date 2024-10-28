package com.android.periodpals.model

import com.android.periodpals.BuildConfig.SUPABASE_KEY
import com.android.periodpals.BuildConfig.SUPABASE_URL
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseModule {

  fun getClient(): SupabaseClient {
    return createSupabaseClient(supabaseUrl = SUPABASE_URL, supabaseKey = SUPABASE_KEY) {
      install(Postgrest)
      install(Auth)
    }
  }
}
