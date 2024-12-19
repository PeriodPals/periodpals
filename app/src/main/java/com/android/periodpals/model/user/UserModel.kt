package com.android.periodpals.model.user

/** Interface for user repository. Defines methods for loading and saving user profiles. */
interface UserRepository {
  /**
   * Loads the user profile for the given user ID.
   *
   * @param idUser The ID of the user profile to be loaded.
   * @param onSuccess callback to be called on successful call on this function returning the
   *   UserDto
   * @param onFailure callback to be called when error is caught
   */
  suspend fun loadUserProfile(
      idUser: String,
      onSuccess: (UserDto) -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Loads all user profiles.
   *
   * @param onSuccess callback to be called on successful call on this function returning the list
   *   of UserDto
   * @param onFailure callback to be called when error is caught
   */
  suspend fun loadUserProfiles(onSuccess: (List<UserDto>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Creates the user profile.
   *
   * @param user The user profile to be created or updated.
   * @param onSuccess callback block to be called on success
   * @param onFailure callback block to be called when exception is caught
   */
  suspend fun createUserProfile(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Upsert a user profile. To upsert means to check if db row exists, if so update with new info,
   * else create new.
   *
   * @param userDto The user profile to be checked
   * @param onSuccess callback block to be called on success
   * @param onFailure callback block to be called when exception is caught
   */
  suspend fun upsertUserProfile(
      userDto: UserDto,
      onSuccess: (UserDto) -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Deletes the user profile from the database.
   *
   * @param idUser The ID of the user profile to be deleted.
   * @param onSuccess Callback function to be called on success.
   * @param onFailure Callback function to be called when there is an exception.
   */
  suspend fun deleteUserProfile(
      idUser: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Uploads a file to the storage.
   *
   * @param filePath The path of the file to be uploaded.
   * @param bytes The bytes of the file to be uploaded.
   * @param onSuccess Callback function to be called on success.
   * @param onFailure Callback function to be called when there is an exception.
   */
  suspend fun uploadFile(
      filePath: String,
      bytes: ByteArray,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Downloads a file from the storage.
   *
   * @param filePath The path of the file to be downloaded.
   * @param onSuccess Callback function to be called on success.
   * @param onFailure Callback function to be called when there is an exception.
   */
  suspend fun downloadFilePublic(
      filePath: String,
      onSuccess: (bytes: ByteArray) -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Downloads a file from the storage.
   *
   * @param filePath The path of the file to be downloaded.
   * @param onSuccess Callback function to be called on success.
   * @param onFailure Callback function to be called when there is an exception.
   */
  suspend fun downloadFile(
      filePath: String,
      onSuccess: (bytes: ByteArray) -> Unit,
      onFailure: (Exception) -> Unit
  )
}
