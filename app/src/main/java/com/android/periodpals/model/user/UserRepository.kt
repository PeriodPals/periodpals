package com.android.periodpals.model.user

/** Interface for user repository. Defines methods for loading and saving user profiles. */
interface UserRepository {
  /**
   * Loads the user profile for the given user ID.
   *
   * @param id The ID of the user whose profile is to be loaded.
   * @return DTO of The user profile.
   */
  suspend fun loadUserProfile(): UserDto

  /**
   * Creates or updates the user profile.
   *
   * @param user The user profile to be created or updated.
   * @return True if the user profile was successfully created, throw an error otherwise.
   */
  suspend fun createUserProfile(user: User): Boolean
}
