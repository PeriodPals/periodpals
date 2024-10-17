package com.android.periodpals.model.user

interface UserRepository {

  suspend fun loadUserProfile(id: Int): UserDto

  suspend fun createUserProfile(user: User): Boolean
}
