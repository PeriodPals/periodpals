package com.android.periodpals.model.location

/**
 * Interface that defines the contract for a location model, responsible for performing
 * location-based searches and handling the results.
 *
 * This interface allows searching for locations based on a query and provides callback functions to
 * handle both successful and failed search results.
 */
interface LocationModel {

  /**
   * Performs a location search based on the given query.
   *
   * @param query The search string to find relevant locations.
   * @param onSuccess A callback function to handle the successful retrieval of location
   *   suggestions. It takes a list of [Location] objects as its parameter.
   * @param onFailure A callback function to handle any errors or exceptions encountered during the
   *   search. It takes an [Exception] as its parameter.
   */
  fun search(query: String, onSuccess: (List<Location>) -> Unit, onFailure: (Exception) -> Unit)

  /**
   * Performs a "reverse location search".
   *
   * In other words, the function returns the closest address to the latitude and longitude of the
   * [Location] parameter.
   *
   * @param gpsCoordinates The location object containing the latitude and longitude.
   * @param onSuccess A callback function to handle the succesful retrieval of the address.
   * @param onFailure A callback function to handle any errors or exceptions encountered during the search.
   */
  fun addressFromCoordinates(
    gpsCoordinates: Location,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
  )
}
