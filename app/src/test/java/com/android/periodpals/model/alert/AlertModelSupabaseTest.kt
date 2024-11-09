import com.android.periodpals.model.alert.*
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.util.UUID

class AlertModelSupabaseTest {

    private lateinit var alertModelSupabase: AlertModelSupabase

    companion object {
        const val id = "idalert"
        const val name = "test_name"
        val product = Product.PAD
        val urgency = Urgency.LOW
        val createdAt = LocalDateTime.now()
        const val location = "test_location"
        const val message = "test_message"
        val status = Status.CREATED
        val uid = null
    }

    private val defaultAlert: Alert =
        Alert(
            id = "idalert",
            uid = uid,
            name = name,
            product = product,
            urgency = urgency,
            createdAt = createdAt,
            location = location,
            message = message,
            status = status
        )

//    private val supabaseClientSuccess = createSupabaseClient("", "") {
//        httpEngine = MockEngine { _ ->
//            respond(
//                content = """
//                [{"id":"${id}","uid":"${uid}","name":"${name}","product":"PAD","urgency":"LOW","createdAt":"${createdAt}","location":"${location}","message":"${message}","status":"CREATED"}]
//                """
//            )
//        }
//        install(Postgrest)
//    }
    private val supabaseClientSuccess = createSupabaseClient("", "") {
        httpEngine = MockEngine { request ->
            println("Received request: ${request.url}")
            respond(
                content = """[{"id":"idalert"}]""", // Minimal response to confirm success
                status = HttpStatusCode.OK
            )
        }
        install(Postgrest)
    }

    private val supabaseClientFailure = createSupabaseClient("", "") {
        httpEngine = MockEngine { _ -> respondBadRequest() }
        install(Postgrest)
    }

    @Before
    fun setUp() {
        alertModelSupabase = AlertModelSupabase(supabaseClientSuccess)
    }

    @Test
    fun `addAlert success`() = runBlocking {
        var result = ""

        alertModelSupabase.addAlert(
            alert = defaultAlert,
            onSuccess = { result = it }, // Ensuring match with test expectation
            onFailure = { fail("should not call onFailure") }
        )

        assertEquals(defaultAlert.id, result)
    }
}
