package com.android.periodpals.model.alert

import com.android.periodpals.MainCoroutineRule
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

@OptIn(ExperimentalCoroutinesApi::class)
class AlertViewModelTest {
  @Mock private lateinit var alertModelSupabase: AlertModelSupabase
  private lateinit var viewModel: AlertViewModel

  @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  companion object {
    const val ID = "idAlert"
    const val ID2 = "idAlert2"
    const val UID = "mock_uid"
    const val UID2 = "mock_uid2"
    val name = "test_name"
    val name_update = "test_update_name"
    val product = Product.PAD
    val product_update = Product.TAMPON
    val urgency = Urgency.LOW
    val urgency_update = Urgency.MEDIUM
    val createdAt = LocalDateTime(2022, 1, 1, 0, 0).toString()
    val createdAt_update = LocalDateTime(2023, 2, 2, 1, 1).toString()
    val location = "test_location"
    val location_update = "test_update_location"
    val message = "test_message"
    val message_update = "test_update_message"
    val status = Status.CREATED
    val status_update = Status.PENDING

    val alert =
        Alert(
            id = ID,
            uid = UID,
            name = name,
            product = product,
            urgency = urgency,
            createdAt = createdAt,
            location = location,
            message = message,
            status = status)
    val alertNullID =
        Alert(
            id = null,
            uid = UID,
            name = name,
            product = product,
            urgency = urgency,
            createdAt = createdAt,
            location = location,
            message = message,
            status = status)
    val alertUpdated =
        Alert(
            id = ID,
            uid = UID,
            name = name_update,
            product = product_update,
            urgency = urgency_update,
            createdAt = createdAt_update,
            location = location_update,
            message = message_update,
            status = status_update)
    val alertOther =
        Alert(
            id = ID2,
            uid = UID2,
            name = name,
            product = product,
            urgency = urgency,
            createdAt = createdAt,
            location = location,
            message = message,
            status = status)
  }

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    // Create ViewModel with mocked AlertModelSupabase
    viewModel = AlertViewModel(alertModelSupabase)
  }

  @Test
  fun createAlertSuccess() = runBlocking {
    // Mock addAlert success behavior
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    // Mock getAllAlerts to verify it is called after successful addition
    doAnswer { invocation ->
          val onSuccess = invocation.getArgument<(List<Alert>) -> Unit>(0)
          onSuccess(listOf(alert)) // Return a list with our mock alert
          null
        }
        .`when`(alertModelSupabase)
        .getAllAlerts(any(), any())

    viewModel.createAlert(alert)

    assertEquals(listOf(alert), viewModel.alerts.value)
  }

  @Test
  fun createAlertAddAlertFailure() = runBlocking {
    // Mock addAlert success behavior
    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("createAlert failure")) }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    viewModel.createAlert(alert)
    assert(viewModel.alerts.value!!.isEmpty())
  }

  @Test
  fun createAlertGetAlertFailure() = runBlocking {
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    doAnswer { it.getArgument<(Exception) -> Unit>(1)(Exception(" ")) }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    viewModel.createAlert(alert)

    assert(viewModel.alerts.value!!.isEmpty())
  }

  @Test
  fun deleteAlertSuccess() = runBlocking {
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    var calls = 0
    doAnswer {
          if (calls == 0) {
            it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alert))
          } else {
            it.getArgument<(List<Alert>) -> Unit>(0)(listOf())
          }
          calls++
        }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .deleteAlertById(any<String>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    viewModel.createAlert(alert)
    assert(viewModel.alerts.value!!.isNotEmpty())
    assertEquals(listOf(alert), viewModel.alerts.value)

    viewModel.deleteAlert(alert)
    assert(viewModel.alerts.value!!.isEmpty())
  }

  @Test
  fun deleteAlertNullIdFailure() = runBlocking {
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    doAnswer { it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alert)) }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    viewModel.createAlert(alert)
    viewModel.deleteAlert(alertNullID)

    assert(!viewModel.alerts.value!!.isEmpty())
    assertEquals(listOf(alert), viewModel.alerts.value)
  }

  @Test
  fun deleteAlertDeleteFailure() = runBlocking {
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    doAnswer { it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alert)) }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("deleteAlertFailure")) }
        .`when`(alertModelSupabase)
        .deleteAlertById(any<String>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    viewModel.createAlert(alert)
    assert(viewModel.alerts.value!!.isNotEmpty())
    assertEquals(listOf(alert), viewModel.alerts.value)

    viewModel.deleteAlert(alert)
    assert(viewModel.alerts.value!!.isNotEmpty())
    assertEquals(listOf(alert), viewModel.alerts.value)
  }

  @Test
  fun getAlertSuccess() = runBlocking {
    doAnswer {
          assertEquals(ID, it.getArgument<String>(0))
          it.getArgument<(Alert) -> Unit>(1)(alert)
        }
        .`when`(alertModelSupabase)
        .getAlert(any<String>(), any<(Alert) -> Unit>(), any<(Exception) -> Unit>())

    val result = viewModel.getAlert(ID)
    assertEquals(alert, result)
  }

  @Test
  fun getAlertGetAlertFailure() = runBlocking {
    doAnswer {
          assertEquals(ID, it.getArgument<String>(0))
          it.getArgument<(Exception) -> Unit>(2)(Exception("Supabase Fails :("))
        }
        .`when`(alertModelSupabase)
        .getAlert(any<String>(), any<(Alert) -> Unit>(), any<(Exception) -> Unit>())

    val result = viewModel.getAlert(ID)
    assertNull(result)
  }

  @Test
  fun getAlertsByUserSuccess() = runBlocking {
    doAnswer { it.getArgument<(List<Alert>) -> Unit>(1)(listOf(alert)) }
        .`when`(alertModelSupabase)
        .getAlertsFilteredBy(
            any<PostgrestFilterBuilder.() -> Unit>(),
            any<(List<Alert>) -> Unit>(),
            any<(Exception) -> Unit>())

    val result = viewModel.getAlertsByUser(UID)
    assertEquals(listOf(alert), result)
  }

  @Test
  fun getAlertByUserFailure() = runBlocking {
    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("Supabase Fails :(")) }
        .`when`(alertModelSupabase)
        .getAlertsFilteredBy(
            any<PostgrestFilterBuilder.() -> Unit>(),
            any<(List<Alert>) -> Unit>(),
            any<(Exception) -> Unit>())

    val result = viewModel.getAlertsByUser(UID)
    assertNull(result)
  }

  @Test
  fun updateAlertSuccess() = runBlocking {
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .updateAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())
    var count = 0
    doAnswer {
          if (count < 1) {
            it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alert))
          } else {
            it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alertUpdated))
          }
          count++
        }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    viewModel.createAlert(alert)
    assertEquals(listOf(alert), viewModel.alerts.value)

    viewModel.updateAlert(alertUpdated)
    assertEquals(listOf(alertUpdated), viewModel.alerts.value)
  }

  @Test
  fun updateAlertFailure() = runBlocking {
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    doAnswer { it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alert)) }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("Supabase fail")) }
        .`when`(alertModelSupabase)
        .updateAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    viewModel.createAlert(alert)
    assertEquals(listOf(alert), viewModel.alerts.value)

    viewModel.updateAlert(alertUpdated)
    assertEquals(listOf(alert), viewModel.alerts.value)
  }

  @Test
  fun getPalAlertsSuccess() = runBlocking {
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())
    var count = 0
    doAnswer {
          if (count == 0) {
            it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alert))
          } else {
            it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alert, alertOther))
          }
          count++
        }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())
    assertEquals(listOf<Alert>(), viewModel.alerts.value)
    viewModel.createAlert(alert)
    assertEquals(listOf(alert), viewModel.alerts.value)
    viewModel.createAlert(alertOther)
    assertEquals(listOf(alert, alertOther), viewModel.alerts.value)

    val result: List<Alert>? = viewModel.getPalAlerts(alert.uid)

    assertNotNull(result)
    assert(result!!.isNotEmpty())
    assertEquals(listOf(alertOther), result)
  }

  @Test
  fun getPalAlertsFailure() = runBlocking {
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())
    var count = 0
    doAnswer {
          if (count == 0) {
            it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alert))
          } else if (count == 1) {
            it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alert, alertOther))
          } else {
            it.getArgument<(Exception) -> Unit>(1)(Exception("Supabase fail :("))
          }
          count++
        }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())
    assertEquals(listOf<Alert>(), viewModel.alerts.value)
    viewModel.createAlert(alert)
    assertEquals(listOf(alert), viewModel.alerts.value)
    viewModel.createAlert(alertOther)
    assertEquals(listOf(alert, alertOther), viewModel.alerts.value)

    val result: List<Alert>? = viewModel.getPalAlerts(alert.uid)
    assertNull(result)
  }
}
