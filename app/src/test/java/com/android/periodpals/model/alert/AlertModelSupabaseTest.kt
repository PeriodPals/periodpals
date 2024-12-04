import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertDto
import com.android.periodpals.model.alert.AlertModelSupabase
import com.android.periodpals.model.alert.LocationGIS
import com.android.periodpals.model.alert.Product
import com.android.periodpals.model.alert.Status
import com.android.periodpals.model.alert.Urgency
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class AlertModelSupabaseTest {

  private lateinit var alertModelSupabase: AlertModelSupabase

  companion object {
    const val id = "idAlert"
    const val uid = "mock_uid"
    val name = "test_name"
    val product = Product.PAD
    val urgency = Urgency.LOW
    val createdAt = LocalDateTime(2022, 1, 1, 0, 0).toString()
    val location = "46.9481,7.4474,Bern"
    val locationGIS = LocationGIS(type = "Point", coordinates = listOf(7.4474, 46.9481))
    val message = "test_message"
    val status = Status.CREATED
  }

  private var defaultAlert: Alert =
      Alert(
          id = id,
          uid = uid,
          name = name,
          product = product,
          urgency = urgency,
          createdAt = createdAt,
          location = location,
          locationGIS = "POINT(${locationGIS.coordinates[0]} ${locationGIS.coordinates[1]})",
          message = message,
          status = status)

  private val originalAlert: Alert =
      defaultAlert.copy() // copy to restore after testing for updateAlert()

  private val supabaseClientSuccess =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { request ->
          println("Received request: ${request.url}")
          respond(
              content =
                  "[" +
                      "{\"id\":\"${defaultAlert.id}\"," +
                      "\"uid\":\"${defaultAlert.uid}\"," +
                      "\"name\":\"${defaultAlert.name}\"," +
                      "\"product\":\"${defaultAlert.product}\"," +
                      "\"urgency\":\"${defaultAlert.urgency}\"," +
                      "\"createdAt\":\"${defaultAlert.createdAt}\"," +
                      "\"location\":\"${defaultAlert.location}\"," +
                      "\"locationGIS\":{\"type\":\"Point\",\"coordinates\":[${locationGIS.coordinates[0]}, ${locationGIS.coordinates[1]}]}," +
                      "\"message\":\"${defaultAlert.message}\"," +
                      "\"status\":\"${defaultAlert.status}\"}" +
                      "]",
              status = HttpStatusCode.OK)
        }
        install(Postgrest)
      }

  private val supabaseClientFailure =
      createSupabaseClient("", "") {
        httpEngine = MockEngine { _ -> respondBadRequest() }
        install(Postgrest)
      }

  @Before
  fun setUp() {
    alertModelSupabase = AlertModelSupabase(supabaseClientSuccess)
  }

  @Test
  fun addAlertSuccess() = runBlocking {
    var result = false

    alertModelSupabase.addAlert(
        alert = defaultAlert,
        onSuccess = { result = true }, // Ensuring match with test expectation
        onFailure = { fail("should not call onFailure") })

    assertEquals(true, result)
  }

  @Test
  fun addAlertFailure() = runBlocking {
    alertModelSupabase = AlertModelSupabase(supabaseClientFailure)
    var onFailureCalled = false

    alertModelSupabase.addAlert(
        alert = defaultAlert,
        onSuccess = { fail("should not call onSuccess") },
        onFailure = { onFailureCalled = true })

    assert(onFailureCalled)
  }

  @Test
  fun getAlertsuccess() = runBlocking {
    var result: Alert? = null

    alertModelSupabase.getAlert(
        idAlert = id,
        onSuccess = { result = it },
        onFailure = { fail("should not call onFailure") })

    assertEquals(defaultAlert, result)
  }

  @Test
  fun getAlertFailure() = runBlocking {
    alertModelSupabase = AlertModelSupabase(supabaseClientFailure)
    var onFailureCalled = false

    alertModelSupabase.getAlert(
        idAlert = id,
        onSuccess = { fail("should not call onSuccess") },
        onFailure = { onFailureCalled = true })

    assert(onFailureCalled)
  }

  @Test
  fun getAllAlertsSuccess() = runBlocking {
    var result: List<Alert>? = null

    alertModelSupabase.getAllAlerts(
        onSuccess = { result = it }, onFailure = { fail("should not call onFailure") })

    assertEquals(listOf(defaultAlert), result)
  }

  @Test
  fun getAllAlertsFailure() = runBlocking {
    alertModelSupabase = AlertModelSupabase(supabaseClientFailure)
    var onFailureCalled = false

    alertModelSupabase.getAllAlerts(
        onSuccess = { fail("should not call onSuccess") }, onFailure = { onFailureCalled = true })

    assert(onFailureCalled)
  }

  @Test
  fun getAlertsFilteredBySuccess() = runBlocking {
    var result: List<Alert>? = null

    alertModelSupabase.getAlertsFilteredBy(
        cond = { eq("uid", uid) },
        onSuccess = { result = it },
        onFailure = { fail("should not call onFailure") })

    assertEquals(listOf(defaultAlert), result)
  }

  @Test
  fun getAlertsFilteredByFailure() = runBlocking {
    alertModelSupabase = AlertModelSupabase(supabaseClientFailure)
    var onFailureCalled = false

    alertModelSupabase.getAlertsFilteredBy(
        cond = { eq("uid", uid) },
        onSuccess = { fail("should not call onSuccess") },
        onFailure = { onFailureCalled = true })

    assert(onFailureCalled)
  }

  @Test
  fun getAlertsWithinRadiusSuccess() = runBlocking {
    val testLatitude = 46.9481
    val testLongitude = 7.4474
    val testRadius = 1000.0 // 1 km

    // Second alert outside the radius
    val outsideLocationGIS = LocationGIS(type = "Point", coordinates = listOf(8.5417, 47.3769))
    val outsideRadiusAlert =
        defaultAlert.copy(
            id = "idOutside",
            location = "47.3769,8.5417,Zürich",
            locationGIS =
                "POINT(${outsideLocationGIS.coordinates[0]} ${outsideLocationGIS.coordinates[1]})",
            message = "Outside radius message")

    val allAlerts = listOf(AlertDto(defaultAlert), AlertDto(outsideRadiusAlert))

    // Mock Supabase response to filter alerts dynamically based on the radius
    alertModelSupabase =
        AlertModelSupabase(
            createSupabaseClient("", "") {
              httpEngine = MockEngine { request ->
                println("Received request: ${request.url}")

                // Simulate filtering based on the radius
                val filteredAlerts =
                    allAlerts.filter { alertDto ->
                      val alertCoordinates = alertDto.locationGIS?.coordinates
                      if (alertCoordinates != null) {
                        val alertLatitude = alertCoordinates[1]
                        val alertLongitude = alertCoordinates[0]
                        val distance =
                            calculateDistance(
                                testLatitude,
                                testLongitude,
                                alertLatitude,
                                alertLongitude) // helper function
                        distance <= testRadius
                      } else {
                        false
                      }
                    }

                // Return filtered alerts as JSON
                respond(
                    content =
                        filteredAlerts.joinToString(prefix = "[", postfix = "]") {
                          """
                        {
                            "id": "${it.id}",
                            "uid": "${it.uid}",
                            "name": "${it.name}",
                            "product": "${it.product}",
                            "urgency": "${it.urgency}",
                            "createdAt": "${it.createdAt}",
                            "location": "${it.location}",
                            "locationGIS": {"type": "Point", "coordinates": [${it.locationGIS!!.coordinates[0]}, ${it.locationGIS!!.coordinates[1]}]},
                            "message": "${it.message}",
                            "status": "${it.status}"
                        }
                        """
                              .trimIndent()
                        },
                    status = HttpStatusCode.OK)
              }
              install(Postgrest)
            })

    var result: List<Alert>? = null

    alertModelSupabase.getAlertsWithinRadius(
        latitude = testLatitude,
        longitude = testLongitude,
        radius = testRadius,
        onSuccess = { result = it },
        onFailure = { fail("should not call onFailure") })

    assertNotNull(result)
    println(result)
    assertEquals(1, result!!.size)
    assertEquals(defaultAlert.id, result!![0].id) // defaultAlert is the only one within the radius
  }

  @Test
  fun getAlertsWithinRadiusFailure() = runBlocking {
    // Switch to the failure client
    alertModelSupabase = AlertModelSupabase(supabaseClientFailure)

    var onFailureCalled = false

    // Call the function with mock failure client
    alertModelSupabase.getAlertsWithinRadius(
        latitude = 46.9481,
        longitude = 7.4474,
        radius = 10000.0, // 10 km
        onSuccess = { fail("should not call onSuccess") },
        onFailure = { onFailureCalled = true } // Track failure callback
        )

    // Assert that the failure callback was invoked
    assert(onFailureCalled)
  }

  @Test
  fun updateAlertSuccess() = runBlocking {
    var updateResult = false
    var retrievedAlert: Alert? = null

    // Modify the alert
    defaultAlert = defaultAlert.copy(product = Product.TAMPON, urgency = Urgency.HIGH)

    // Update the alert
    alertModelSupabase.updateAlert(
        alert = defaultAlert,
        onSuccess = { updateResult = true },
        onFailure = { fail("should not call onFailure") })

    // Assert the update was successful
    assert(updateResult)

    // Retrieve the updated alert
    alertModelSupabase.getAlert(
        idAlert = id,
        onSuccess = { retrievedAlert = it },
        onFailure = { fail("should not call onFailure") })

    // Assert the retrieved alert has the updated fields
    assertEquals(Product.TAMPON, retrievedAlert?.product)
    assertEquals(Urgency.HIGH, retrievedAlert?.urgency)
    defaultAlert = originalAlert.copy() // Restore the original alert
  }

  @Test
  fun updateAlertFailure() = runBlocking {
    alertModelSupabase = AlertModelSupabase(supabaseClientFailure)
    var onFailureCalled = false

    alertModelSupabase.updateAlert(
        alert = defaultAlert,
        onSuccess = { fail("should not call onSuccess") },
        onFailure = { onFailureCalled = true })

    assert(onFailureCalled)
  }

  @Test
  fun deleteAlertSuccess() = runBlocking {
    var deleteResult = false

    alertModelSupabase.deleteAlertById(
        idAlert = id,
        onSuccess = { deleteResult = true },
        onFailure = { fail("should not call onFailure") })

    assert(deleteResult)
  }

  @Test
  fun deleteAlertFailure() = runBlocking {
    alertModelSupabase = AlertModelSupabase(supabaseClientFailure)
    var onFailureCalled = false

    alertModelSupabase.deleteAlertById(
        idAlert = id,
        onSuccess = { fail("should not call onSuccess") },
        onFailure = { onFailureCalled = true })

    assert(onFailureCalled)
  }
}

// Haversine formula
/* Helper function for getAlertsWithinRadiusSuccess() test */
private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
  val radius = 6371000.0 // Earth's radius in meters
  val dLat = Math.toRadians(lat2 - lat1)
  val dLon = Math.toRadians(lon2 - lon1)
  val a =
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
          Math.cos(Math.toRadians(lat1)) *
              Math.cos(Math.toRadians(lat2)) *
              Math.sin(dLon / 2) *
              Math.sin(dLon / 2)
  val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  return radius * c
}
