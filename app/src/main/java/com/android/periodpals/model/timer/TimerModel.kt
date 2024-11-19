package com.android.periodpals.model.timer

/** Interface for the timer model. */
interface TimerModel {
  /**
   * Retrieves the timer data for the given user ID.
   *
   * @param uid The user ID to retrieve the timer data for.
   * @param onSuccess Callback function to be called on successful retrieval.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  suspend fun getTimer(uid: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Creates a new timer for the given user ID when the user creates a new account.
   *
   * @param uid The user ID to create the timer for.
   * @param onSuccess Callback function to be called on successful creation.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  suspend fun createTimer(uid: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Updates the timer data for the given user ID.
   *
   * @param uid The user ID to update the timer data for.
   * @param onSuccess Callback function to be called on successful update.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  suspend fun updateTimer(uid: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
