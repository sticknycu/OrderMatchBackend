    package ro.bagatictac.itfest2023be.domain.web

    import kotlinx.coroutines.runBlocking
    import org.assertj.core.api.Assertions.assertThat
    import org.junit.jupiter.api.Test
    import org.junit.jupiter.api.extension.ExtendWith
    import org.mockito.Mockito.`when`
    import org.mockito.junit.jupiter.MockitoExtension
    import org.mockito.kotlin.mock
    import reactor.core.publisher.Mono
    import ro.bagatictac.itfest2023be.domain.gateway.AssignmentResult
    import ro.bagatictac.itfest2023be.domain.gateway.OrderActionsResponse
    import ro.bagatictac.itfest2023be.domain.gateway.ResponseLambda
    import ro.bagatictac.itfest2023be.domain.model.CourierOrderSortActionType
    import ro.bagatictac.itfest2023be.domain.service.OrderTransformerService
    import java.time.LocalDateTime
    import java.util.*

    @ExtendWith(MockitoExtension::class)
    internal class PickUpControllerTest {

        private val orderTransformerService: OrderTransformerService = mock()
        private val pickupController = PickUpController(orderTransformerService)

        @Test
        fun assignCourier_thenReturnResponse() {
            runBlocking {
                val venueRequestBody = VenueRequestBody(UUID.randomUUID(), UUID.randomUUID())
                val now = LocalDateTime.now()

                val courierOrderAction = OrderActionsResponse(UUID.randomUUID(), UUID.randomUUID(), 10.0, now, CourierOrderSortActionType.PICKUP)
                val assignmentResult = AssignmentResult(UUID.randomUUID(), listOf(courierOrderAction))
                val expectedResponse = ResponseLambda(listOf(assignmentResult))

                `when`(pickupController.assignCourier(venueRequestBody)).thenReturn(Mono.just(expectedResponse))

                val response = pickupController.assignCourier(venueRequestBody).block()

                assertThat(response).isEqualTo(expectedResponse)
            }
        }
    }