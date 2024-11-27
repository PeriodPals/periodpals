package com.android.periodpals.model.timer

/** Interface for the timer model. */
interface TimerRepository {

  /**
   * Adds a new timer to the database.
   *
   * @param timer The timer to be added.
   * @param onSuccess The callback to be invoked when the timer is updated successfully.
   * @param onFailure The callback to be invoked when an error occurs while updating the timer.
   */
  suspend fun addTimer(timer: Timer, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Retrieves all timers of a user from the database.
   *
   * @param userID The ID of the user whose timers are to be retrieved.
   * @param onSuccess The callback to be invoked when the timers are retrieved successfully.
   * @param onFailure The callback to be invoked when an error occurs while retrieving the timers.
   */
  suspend fun getTimersOfUser(
      userID: String,
      onSuccess: (List<Timer>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Deletes the timer with the specified ID.
   *
   * @param timerID The ID of the timer to delete.
   * @param onSuccess The callback to be invoked when the timer is deleted successfully.
   * @param onFailure The callback to be invoked when an error occurs while deleting the timer.
   */
  suspend fun deleteTimerById(
      timerID: String,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )
}
