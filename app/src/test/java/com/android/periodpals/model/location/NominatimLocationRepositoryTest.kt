package com.android.periodpals.model.location

import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NominatimLocationRepositoryTest {

    private lateinit var mockHttpClient: OkHttpClient
    private lateinit var mockCall: Call
    private lateinit var locationRepository: NominatimLocationRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mockHttpClient = mock(OkHttpClient::class.java)
        mockCall = mock(Call::class.java)
        locationRepository = NominatimLocationRepository(mockHttpClient)
    }

    @Test
    fun search_successfulResponse() {
        val jsonResponse = """
            [
                {
                    "place_id": 123,
                    "lat": "46.5197",
                    "lon": "6.5662",
                    "display_name": "EPFL, Lausanne, Switzerland"
                }
            ]
        """.trimIndent()

        // Create a basic Request object
        val mockRequest = Request.Builder()
            .url("https://mockurl.com")
            .build()

        val response = Response.Builder()
            .request(mockRequest)
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .body(jsonResponse.toResponseBody("application/json".toMediaType()))
            .build()

        whenever(mockHttpClient.newCall(any())).thenReturn(mockCall)
        doAnswer { invocation ->
            val callback = invocation.getArgument<okhttp3.Callback>(0)
            callback.onResponse(mockCall, response)
        }.whenever(mockCall).enqueue(any())

        locationRepository.search("EPFL", onSuccess = { locations ->
            assert(locations.isNotEmpty())
            assert(locations[0].name == "EPFL, Lausanne, Switzerland")
            assert(locations[0].latitude == 46.5197)
            assert(locations[0].longitude == 6.5662)
        }, onFailure = {
            assert(false) { "Expected success, but got failure" }
        })
    }

    @Test
    fun search_failureResponse() {
        val mockRequest = Request.Builder()
            .url("https://mockurl.com")
            .build()

        val response = Response.Builder()
            .request(mockRequest)
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(500)
            .message("Server Error")
            .body("Internal Server Error".toResponseBody("text/plain".toMediaType()))
            .build()

        whenever(mockHttpClient.newCall(any())).thenReturn(mockCall)
        doAnswer { invocation ->
            val callback = invocation.getArgument<okhttp3.Callback>(0)
            callback.onResponse(mockCall, response)
        }.whenever(mockCall).enqueue(any())

        locationRepository.search("EPFL", onSuccess = {
            assert(false) { "Expected failure, but got success" }
        }, onFailure = { exception ->
            assert(exception.message?.contains("Server Error") == true)
        })
    }
}
