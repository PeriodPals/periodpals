package com.android.periodpals.model.user

/** Interface for user repository. Defines methods for loading and saving user profiles. */
interface UserRepository {
  /**
   * Loads the user profile for the given user ID.
   *
   * @param id The ID of the user whose profile is to be loaded.
   * @param onSuccess callback to be called on successful call on this function
   * @param onFailure callback to be called when error is caught
   * @return DTO of The user profile.
   */
  suspend fun loadUserProfile(onSuccess: (UserDto) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Creates or updates the user profile.
   *
   * @param user The user profile to be created or updated.
   * @param onSuccess callback block to be called on success
   * @param onFailure callback block to be called when exception is caught
   * @return True if the user profile was successfully created, throw an error otherwise.
   */
  suspend fun createUserProfile(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
