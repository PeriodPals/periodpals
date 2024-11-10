package com.android.periodpals.model.alert

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.periodpals.MainCoroutineRule
import com.android.periodpals.model.user.UserDto
import io.mockk.MockKAnnotations
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

@OptIn(ExperimentalCoroutinesApi::class)
class AlertViewModelTest {
    @Mock private lateinit var alertModelSupabase: AlertModelSupabase
    private lateinit var viewModel: AlertViewModel

    @ExperimentalCoroutinesApi @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    companion object{
        const val id = "idAlert"
        const val uid = "mock_uid"
        val name = "test_name"
        val product = Product.PAD
        val urgency = Urgency.LOW
        val createdAt = LocalDateTime(2022, 1, 1, 0, 0).toString()
        val location = "test_location"
        val message = "test_message"
        val status = Status.CREATED
    }

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        // Create ViewModel with mocked AlertModelSupabase
        viewModel = AlertViewModel(alertModelSupabase)
    }

    @Test
    fun createAlertSuccess() = runBlocking{
        val alert = Alert(
            id = id,
            uid = uid,
            name = name,
            product = product,
            urgency = urgency,
            createdAt = createdAt,
            location = location,
            message = message,
            status = status
        )

        // Mock addAlert success behavior
        doAnswer { it.getArgument<(Alert) -> Unit>(0)(alert)
        }.`when`(alertModelSupabase).addAlert(eq(alert), any<() -> Unit>(), any<(Exception) -> Unit>())

        // Mock getAllAlerts to verify it is called after successful addition
        doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<Alert>) -> Unit>(0)
            onSuccess(listOf(alert)) // Return a list with our mock alert
            null
        }.`when`(alertModelSupabase).getAllAlerts(any(), any())

        viewModel.createAlert(alert)

        assertEquals(listOf(alert), viewModel.alerts.value)


    }
}