package com.android.periodpals.model.timer

import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder

/** Interface for the timer model. */
interface TimerRepository {

    /**
     * Retrieves the active timer of a user from the database. If no active timer or more than one
     * active timer is found, the onFailure callback is invoked.
     *
     * @param uid The user ID whose active timer is to be retrieved.
     * @param onSuccess The callback to be invoked when the active timer is retrieved successfully.
     * @param onFailure The callback to be invoked when an error occurs while retrieving the active
     */
    suspend fun getActiveTimer(
        uid: String,
        onSuccess: (Timer?) -> Unit,
        onFailure: (Exception) -> Unit
    )

    /**
   * Adds a new timer to the database.
   *
   * @param timerDto The timer data to be added.
   * @param onSuccess The callback to be invoked when the timer is updated successfully.
   * @param onFailure The callback to be invoked when an error occurs while updating the timer.
   */
  suspend fun addTimer(timerDto: TimerDto, onSuccess: () -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Updates the timer in the database.
     *
     * @param timerDto The timer data to be updated.
     * @param onSuccess The callback to be invoked when the timer is updated successfully.
     * @param onFailure The callback to be invoked when an error occurs while updating the timer.
     */
    suspend fun updateTimer(
        timerDto: TimerDto,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )

    /**
   * Retrieves all timers of a user from the database.
   *
     * @param uid The ID of the user whose timers are to be retrieved.
   * @param onSuccess The callback to be invoked when the timers are retrieved successfully.
   * @param onFailure The callback to be invoked when an error occurs while retrieving the timers.
   */
  suspend fun getTimersOfUser(
        uid: String,
      onSuccess: (List<Timer>) -> Unit,
      onFailure: (Exception) -> Unit
  )

  /**
   * Deletes the timers filtered by the specified condition.
   *
   * @param cond The condition to filter the timers by, eg. `uid == user.uid`.
   * @param onSuccess The callback to be invoked when the timer is deleted successfully.
   * @param onFailure The callback to be invoked when an error occurs while deleting the timer.
   */
  suspend fun deleteTimersFilteredBy(
      cond: PostgrestFilterBuilder.() -> Unit,
      onSuccess: () -> Unit,
      onFailure: (Exception) -> Unit
  )
}
