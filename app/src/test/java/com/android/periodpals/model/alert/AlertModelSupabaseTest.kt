import com.android.periodpals.model.alert.Alert
import com.android.periodpals.model.alert.AlertModelSupabase
import com.android.periodpals.model.alert.AlertStatus
import com.android.periodpals.model.alert.Product
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
    val location = "test_location"
    val message = "test_message"
    val alertStatus = AlertStatus.CREATED
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
          message = message,
          alertStatus = alertStatus)

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
                      "\"message\":\"${defaultAlert.message}\"," +
                      "\"status\":\"${defaultAlert.alertStatus}\"}" +
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
