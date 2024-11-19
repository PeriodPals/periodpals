package com.android.periodpals.model.alert

import com.android.periodpals.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
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
    const val id = "idAlert"
    const val uid = "mock_uid"
    val name = "test_name"
    val product = Product.PAD
    val urgency = Urgency.LOW
    val createdAt = LocalDateTime(2022, 1, 1, 0, 0).toString()
    val location = "test_location"
    val message = "test_message"
    val status = Status.CREATED

    val alert =
        Alert(
            id = id,
            uid = uid,
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
            uid = uid,
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
}
