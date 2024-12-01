package com.android.periodpals.model.alert

import com.android.periodpals.MainCoroutineRule
import kotlin.random.Random
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.never
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

const val EXAMPLES = 2

@OptIn(ExperimentalCoroutinesApi::class)
class AlertViewModelTest {
  @Mock private lateinit var alertModelSupabase: AlertModelSupabase
  private lateinit var viewModel: AlertViewModel

  @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  companion object {
    val tagList: (String) -> List<String> = {
      (0 until EXAMPLES).toList().map { n -> it + "_" + n.toString() }
    }

    val id = tagList("id")
    val uid = tagList("uid")
    val name = tagList("name")
    val product = List(EXAMPLES) { Product.entries[Random.nextInt(Product.entries.size)] }
    val urgency = List(EXAMPLES) { Urgency.entries[Random.nextInt(Urgency.entries.size)] }
    val createdAt =
        (0 until EXAMPLES).toList().map {
          LocalDateTime(2022 + it, 1 + it, 1 + it, it, it).toString()
        }
    val location = tagList("location")
    val message = tagList("message")
    val status = List(EXAMPLES) { Status.entries[Random.nextInt(Status.entries.size)] }
  }

  private fun alertBuild(index: Int): Alert =
      Alert(
          id = id[index],
          uid = uid[index],
          name = name[index],
          product = product[index],
          urgency = urgency[index],
          createdAt = createdAt[index],
          location = location[index],
          message = message[index],
          status = status[index])

  private fun updateAlert(index: Int, offset: Int): Alert {
    val n = (index + offset) % EXAMPLES
    return Alert(
        id = id[index],
        uid = uid[n],
        name = name[n],
        product = product[n],
        urgency = urgency[n],
        createdAt = createdAt[n],
        location = location[n],
        message = message[n],
        status = status[n])
  }

  val alerts = (0 until EXAMPLES).toList().map { alertBuild(it) }

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    // Create ViewModel with mocked AlertModelSupabase
    viewModel = AlertViewModel(alertModelSupabase)
    viewModel.setUserID(id[0])
  }

  @Test
  fun createAlertSuccess() = runBlocking {
    // Mock addAlert success behavior
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    // Mock getAllAlerts to verify it is called after successful addition
    doAnswer { it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alerts[0])) }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    viewModel.createAlert(alerts[0], {}, { fail("Should not call `onFailure`") })

    verify(alertModelSupabase).addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())
    verify(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())
    assertEquals(listOf(alerts[0]), viewModel.alerts.value)
  }

  @Test
  fun createAlertAddAlertFailure() = runBlocking {
    // Mock addAlert success behavior
    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("createAlert failure")) }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    viewModel.createAlert(alerts[0], { fail("Should not call `onSuccess`") }, {})

    verify(alertModelSupabase).addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())
    assert(viewModel.alerts.value.isEmpty())
  }

  @Test
  fun createAlertGetAlertFailure() = runBlocking {
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    doAnswer { it.getArgument<(Exception) -> Unit>(1)(Exception("Supabase faild :(")) }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    viewModel.createAlert(alerts[0], { fail("Should not call `onSuccess`") }, {})

    verify(alertModelSupabase).addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())
    verify(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())
    assert(viewModel.alerts.value.isEmpty())
  }

  @Test
  fun deleteAlertSuccess() = runBlocking {
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    var calls = 0
    doAnswer {
          if (calls == 0) {
            it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alerts[0]))
          } else {
            it.getArgument<(List<Alert>) -> Unit>(0)(listOf())
          }
          calls++
        }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    doAnswer {
          assertEquals(id[0], it.getArgument<String>(0))
          it.getArgument<() -> Unit>(1)()
        }
        .`when`(alertModelSupabase)
        .deleteAlertById(any<String>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    viewModel.createAlert(alerts[0], {}, { fail("Should not `onFailure`") })

    verify(alertModelSupabase).addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())
    verify(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    assert(viewModel.alerts.value.isNotEmpty())
    assertEquals(listOf(alerts[0]), viewModel.alerts.value)

    viewModel.deleteAlert(id[0], {}, { fail("Should not `onFailure`") })

    verify(alertModelSupabase)
        .deleteAlertById(eq(id[0]), any<() -> Unit>(), any<(Exception) -> Unit>())

    assert(viewModel.alerts.value.isEmpty())
  }

  @Test
  fun deleteAlertDeleteFailure() = runBlocking {
    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    doAnswer { it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alerts[0])) }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("deleteAlertFailure")) }
        .`when`(alertModelSupabase)
        .deleteAlertById(any<String>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    viewModel.createAlert(alerts[0], {}, { fail("Should not call `onFailure`") })

    verify(alertModelSupabase).addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())
    verify(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    assert(viewModel.alerts.value.isNotEmpty())
    assertEquals(alerts.slice(0 until 1), viewModel.alerts.value)

    viewModel.deleteAlert(id[0], { fail("Should not call `onSuccess`") }, {})
    assert(viewModel.alerts.value.isNotEmpty())
    assertEquals(alerts.slice(0 until 1), viewModel.alerts.value)
  }

  @Test
  fun getAlertSuccess() = runBlocking {
    val n = Random.nextInt(EXAMPLES)
    val alertID = id[n]
    val alert = alerts[n]

    doAnswer {
          assertEquals(alertID, it.getArgument<String>(0))
          it.getArgument<(Alert) -> Unit>(1)(alert)
        }
        .`when`(alertModelSupabase)
        .getAlert(any<String>(), any<(Alert) -> Unit>(), any<(Exception) -> Unit>())

    var result: Alert? = null
    viewModel.getAlert(alertID, { result = it }, { fail("Should not call `onFailure`") })

    assertNotNull(result)
    assertEquals(alert, result)
  }

  @Test
  fun getAlertGetAlertFailure() = runBlocking {
    val n = Random.nextInt(EXAMPLES)
    val alertID = id[n]
    val alert = alerts[n]
    doAnswer {
          assertEquals(alertID, it.getArgument<String>(0))
          it.getArgument<(Exception) -> Unit>(2)(Exception("Supabase Fails :("))
        }
        .`when`(alertModelSupabase)
        .getAlert(any<String>(), any<(Alert) -> Unit>(), any<(Exception) -> Unit>())

    var result = false
    viewModel.getAlert(alertID, { fail("Supabase Fails :(") }, { result = true })

    verify(alertModelSupabase)
        .getAlert(any<String>(), any<(Alert) -> Unit>(), any<(Exception) -> Unit>())
    verify(alertModelSupabase, never())
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    assert(result)
  }

  @Test
  fun updateAlertSuccess() = runBlocking {
    val n = Random.nextInt(EXAMPLES)
    val alert = alerts[n]
    val alertUpdate = updateAlert(n, 1)

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
            it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alertUpdate))
          }
          count++
        }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    viewModel.createAlert(alert, {}, { fail("Should not call `onFailure`") })
    assertEquals(listOf(alert), viewModel.alerts.value)

    viewModel.updateAlert(alertUpdate, {}, { fail("Should not call `onFailure`") })
    assertEquals(listOf(alertUpdate), viewModel.alerts.value)
  }

  @Test
  fun updateAlertFailure() = runBlocking {
    val n = Random.nextInt(EXAMPLES)
    val alert = alerts[n]
    val alertUpdate = updateAlert(n, 1)

    doAnswer { it.getArgument<() -> Unit>(1)() }
        .`when`(alertModelSupabase)
        .addAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    doAnswer { it.getArgument<(List<Alert>) -> Unit>(0)(listOf(alert)) }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    doAnswer { it.getArgument<(Exception) -> Unit>(2)(Exception("Supabase fail")) }
        .`when`(alertModelSupabase)
        .updateAlert(any<Alert>(), any<() -> Unit>(), any<(Exception) -> Unit>())

    viewModel.createAlert(alert, {}, { fail("Should not call `onFailure`") })
    assertEquals(listOf(alert), viewModel.alerts.value)

    viewModel.updateAlert(alertUpdate, { fail("Should not cal `onSuccess`") }, {})
    assertEquals(listOf(alert), viewModel.alerts.value)
  }

  @Test
  fun fetchAlertsSuccess() = runBlocking {
    doAnswer { it.getArgument<(List<Alert>) -> Unit>(0)(alerts) }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    assert(viewModel.alerts.value.isEmpty())

    viewModel.fetchAlerts()
    verify(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())
    assert(viewModel.alerts.value.isNotEmpty())
    assertEquals(alerts, viewModel.alerts.value)
  }

  @Test
  fun fetchAlertsFailure() = runBlocking {
    doAnswer { it.getArgument<(Exception) -> Unit>(1)(Exception("Supabase Fail :(")) }
        .`when`(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())

    assert(viewModel.alerts.value.isEmpty())

    viewModel.fetchAlerts(onSuccess = { fail("Should not call `onSuccess`") })

    verify(alertModelSupabase)
        .getAllAlerts(any<(List<Alert>) -> Unit>(), any<(Exception) -> Unit>())
    assert(viewModel.alerts.value.isEmpty())
  }

  @Test
  fun selectEditAlertCallsRepository() {
    val alert =
        Alert(
            id = "id",
            uid = "uid",
            name = "name",
            product = Product.TAMPON,
            urgency = Urgency.LOW,
            createdAt = "createdAt",
            location = "location",
            message = "message",
            status = Status.CREATED)
    viewModel.selectEditAlert(alert)
    assertEquals(alert, viewModel.selectedEditAlert.value)
  }
}
