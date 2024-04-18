package ro.bagatictac.itfest2023be.domain.scheduler

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import ro.bagatictac.itfest2023be.domain.gateway.AssignmentResult
import ro.bagatictac.itfest2023be.domain.gateway.OrderActionsResponse
import ro.bagatictac.itfest2023be.domain.gateway.ResponseLambda
import ro.bagatictac.itfest2023be.domain.model.CourierOrderSortActionType
import ro.bagatictac.itfest2023be.domain.model.Order
import ro.bagatictac.itfest2023be.domain.model.OrderStatus
import ro.bagatictac.itfest2023be.domain.repository.OrdersRepository
import ro.bagatictac.itfest2023be.domain.service.OrderTransformerService
import ro.bagatictac.itfest2023be.domain.web.VenueRequestBody
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class UpdateOrdersSchedulerTest {

    private val ordersRepository: OrdersRepository = mock()
    private val orderTransformerService: OrderTransformerService = mock()

    private val updateOrdersScheduler: UpdateOrdersScheduler = UpdateOrdersScheduler(ordersRepository, orderTransformerService)

    @Test
    fun runCronJob_AssignCouriersUnassigned() {
        runBlocking {
            val now = LocalDateTime.now()
            val order = Order(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                10, now, now, 10.0, 10.0, OrderStatus.PICKING_UP, 10, now
            )

            val venueRequestBody = VenueRequestBody(order.pickupVenueId, order.deliveryVenueId)
            val responseLambda = ResponseLambda(listOf(AssignmentResult(UUID.randomUUID(), listOf(OrderActionsResponse(UUID.randomUUID(), UUID.randomUUID(), 10.0, now, CourierOrderSortActionType.PICKUP)))))

            whenever(ordersRepository.findAllByAssignedCourierIdIsNull()).thenReturn(Flux.just(order))
            whenever(orderTransformerService.getCouriersAssigned(venueRequestBody)).thenReturn(Mono.just(responseLambda))

            updateOrdersScheduler.runCronJob()

            verify(orderTransformerService).getCouriersAssigned(venueRequestBody)
            verify(ordersRepository).findAllByAssignedCourierIdIsNull()
            verifyNoMoreInteractions(ordersRepository)
            verifyNoMoreInteractions(orderTransformerService)
        }
    }

}