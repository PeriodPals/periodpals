package com.android.periodpals.model.timer

/** Interface for the timer model. */
interface TimerRepository {

  /**
   * Saves the elapsed time of the timer.
   *
   * @param timer The timer to save the elapsed time for.
   * @param onSuccess The callback to be invoked when the elapsed time is saved successfully.
   * @param onFailure The callback to be invoked when an error occurs while saving the elapsed time.
   */
  suspend fun saveElapsedTime(timer: Timer, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Retrieves the timer for the specified user.
   *
   * @param uid The user ID associated with the timer.
   * @param onSuccess The callback to be invoked when the timer is retrieved successfully.
   * @param onFailure The callback to be invoked when an error occurs while retrieving the timer.
   */
  suspend fun getTimer(uid: String, onSuccess: (Timer) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Updates the timer with the specified data.
   *
   * @param timer The timer to update.
   * @param onSuccess The callback to be invoked when the timer is updated successfully.
   * @param onFailure The callback to be invoked when an error occurs while updating the timer.
   */
  suspend fun updateTimer(timer: Timer, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)
}
