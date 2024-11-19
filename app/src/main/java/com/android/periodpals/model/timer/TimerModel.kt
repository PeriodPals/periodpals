package com.android.periodpals.model.timer

/** Interface for the timer model. */
interface TimerModel {
  /**
   * Retrieves the timer data for the given user ID.
   *
   * @param uid The user ID to retrieve the timer data for.
   * @param onSuccess Callback function to be called on successful retrieval, with the timer data as
   *   a parameter.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  suspend fun getTimer(uid: String, onSuccess: (TimerDto) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Creates a new timer for the given user ID when the user creates a new account.
   *
   * @param timer The timer to be created for a new user.
   * @param onSuccess Callback function to be called on successful creation.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  suspend fun createTimer(timer: Timer, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Upsert the timer data for the given user ID. To upsert means to check if the database row
   * exists, if so update with new info, else create new.
   *
   * @param timer The timer data to be checked.
   * @param onSuccess Callback function to be called on successful upsert.
   * @param onFailure Callback function to be called on failure, with the exception as a parameter.
   */
  suspend fun upsertTimer(
      timer: TimerDto,
      onSuccess: (TimerDto) -> Unit,
      onFailure: (Exception) -> Unit
  )
}
