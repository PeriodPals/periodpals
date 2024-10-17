package com.android.periodpals.model

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

object SupabaseClient {
  val client =
      createSupabaseClient(
          supabaseUrl = System.getenv("SUPABASE_URL") ?: "",
          supabaseKey = System.getenv("SUPABASE_KEY") ?: "",
      ) {
        install(Auth)
      }
}
