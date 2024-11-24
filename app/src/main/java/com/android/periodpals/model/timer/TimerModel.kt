package com.android.periodpals.model.timer

/** Interface for the timer model. */
interface TimerRepository {

  /**
   * Retrieves the timer for the specified user. RLS rules are applied to ensure that the user can
   * only access their own timer.
   *
   * @param onSuccess The callback to be invoked when the timer is retrieved successfully.
   * @param onFailure The callback to be invoked when an error occurs while retrieving the timer.
   */
  suspend fun loadTimer(onSuccess: (TimerDto) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Updates the specified timer. If the timer does not exist, it is created.
   *
   * @param timerDto The timer data to update.
   * @param onSuccess The callback to be invoked when the timer is updated successfully.
   * @param onFailure The callback to be invoked when an error occurs while updating the timer.
   */
  suspend fun upsertTimer(
      timerDto: TimerDto,
      onSuccess: (TimerDto) -> Unit,
      onFailure: (Exception) -> Unit
  )
}
