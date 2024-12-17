package com.android.periodpals.model.location

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

private const val TAG = "LocationModelNominatim"

private const val SCHEME = "https"
private const val HOST = "nominatim.openstreetmap.org"
private const val FORMAT_NAME = "format"
private const val FORMAT_VAL = "json"
private const val HEADER_NAME = "User-Agent"
private const val HEADER_VALUE = "PeriodPals"

/**
 * A concrete implementation of the [LocationModel] interface that uses the Nominatim API from
 * OpenStreetMap to search for locations.
 *
 * This class utilizes [OkHttpClient] to perform network requests and fetch location data based on a
 * query string. It parses the response into a list of [Location] objects and invokes the
 * appropriate callback functions on success or failure.
 *
 * @param client The OkHttpClient used to make network requests.
 */
class LocationModelNominatim(val client: OkHttpClient) : LocationModel {

  /**
   * Parses the response body from the Nominatim API into a list of [Location] objects.
   *
   * @param body The response body as a string, expected to be a JSON array.
   * @return A list of [Location] objects created from the parsed JSON data.
   */
  private fun parseBody(body: String): List<Location> {
    val jsonArray = JSONArray(body)

    return List(jsonArray.length()) { i ->
      val jsonObject = jsonArray.getJSONObject(i)
      val lat = jsonObject.getDouble("lat")
      val lon = jsonObject.getDouble("lon")
      val name = jsonObject.getString("display_name")
      Location(lat, lon, name)
    }
  }

  /**
   * Searches for locations based on the given query string using the Nominatim API.
   *
   * If the request is successful, the list of location suggestions is passed to [onSuccess]. In
   * case of failure, an exception is passed to [onFailure].
   *
   * @param query The search query string.
   * @param onSuccess A callback function to handle successful retrieval of location data. It takes
   *   a list of [Location] objects as its parameter.
   * @param onFailure A callback function to handle failure scenarios. It takes an [Exception] as
   *   its parameter.
   */
  override fun search(
    query: String,
    onSuccess: (List<Location>) -> Unit,
    onFailure: (Exception) -> Unit,
  ) {
    // Using HttpUrl.Builder to properly construct the URL with query parameters.
    val url =
      HttpUrl.Builder()
        .scheme(SCHEME)
        .host(HOST)
        .addPathSegment("search")
        .addQueryParameter("q", query)
        .addQueryParameter(FORMAT_NAME, FORMAT_VAL)
        .build()

    // Log the URL to Logcat for inspection
    Log.d(TAG, "Request URL: $url")

    // Create the request with a custom User-Agent and optional Referer
    val request = Request.Builder().url(url).header(HEADER_NAME, HEADER_VALUE).build()
    client
      .newCall(request)
      .enqueue(
        object : Callback {
          override fun onFailure(call: Call, e: IOException) {
            Log.e(TAG, "Failed to execute request", e)
            onFailure(e)
          }

          override fun onResponse(call: Call, response: Response) {
            response.use {
              if (!response.isSuccessful) {
                onFailure(Exception("Unexpected code $response"))
                Log.d(TAG, "Unexpected code $response")
                return
              }

              val body = response.body?.string()
              if (body != null) {
                onSuccess(parseBody(body))
                Log.d(TAG, "Body: $body")
              } else {
                Log.d(TAG, "Empty body")
                onSuccess(emptyList())
              }
            }
          }
        }
      )
  }

  override fun reverseSearch(
    lat: Double,
    lon: Double,
    onSuccess: (String) -> Unit,
    onFailure: (Exception) -> Unit
  ){

    val url =
      HttpUrl.Builder()
        .scheme(SCHEME)
        .host(HOST)
        .addPathSegment("reverse")
        .addQueryParameter("lat", lat.toString())
        .addQueryParameter("lon", lon.toString())
        .addQueryParameter(FORMAT_NAME, FORMAT_VAL)
        .build()

    val request = Request.Builder().url(url).header(HEADER_NAME, HEADER_VALUE).build()
    Log.d(TAG, "Request url: $url")

    client
      .newCall(request)
      .enqueue(
        object : Callback {

          override fun onFailure(call: Call, e: IOException) {
            Log.e(TAG, "Failed to execute request", e)
            onFailure(e)
          }

          override fun onResponse(call: Call, response: Response) {
            response.use {
              if(!response.isSuccessful){
                Log.e(TAG, "Unexpected code: $response")
                onFailure( Exception("Unexpected code: $response"))
                return
              }

              val responseBody = response.body?.string()
              responseBody?.let {
                val jsonObject = org.json.JSONObject(it)
                val displayName = jsonObject.getString("display_name")
                onSuccess(displayName)
              } ?: onSuccess("Unknown address")
            }
          }
        }
      )
  }
}
