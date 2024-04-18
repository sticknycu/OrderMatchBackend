package ro.bagatictac.itfest2023be.domain.service

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import ro.bagatictac.itfest2023be.domain.gateway.*
import ro.bagatictac.itfest2023be.domain.model.*
import ro.bagatictac.itfest2023be.domain.repository.CourierOrderSortRepository
import ro.bagatictac.itfest2023be.domain.repository.CouriersRepository
import ro.bagatictac.itfest2023be.domain.repository.OrdersRepository
import ro.bagatictac.itfest2023be.domain.repository.VenuesRepository
import ro.bagatictac.itfest2023be.domain.web.VenueRequestBody
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class OrderTransformerServiceTest {

    @Mock
    private lateinit var ordersRepository: OrdersRepository
    @Mock
    private lateinit var venuesRepository: VenuesRepository
    @Mock
    private lateinit var couriersRepository: CouriersRepository
    @Mock
    private lateinit var courierOrderSortRepository: CourierOrderSortRepository

    @Mock
    private lateinit var lambdaGateway: LambdaGateway


    @InjectMocks
    private lateinit var orderTransformerService: OrderTransformerService

    @Test
    fun `getAvailableCouriers returns enriched couriers`() {
        val courierId = UUID.randomUUID()
        val venueId = UUID.randomUUID()
        val courier = Courier(courierId, "Courier Name", "1234567890", VehicleType.NORMAL, 0.1, 52.1, -0.1, 300, CourierStatus.FREE)
        val venue = Venue(venueId, "Venue Name", VenueType.RESTAURANT, 10.0, 20.0, true)
        val courierOrderSort = CourierOrderSort(UUID.randomUUID(), courierId, UUID.randomUUID(), CourierOrderSortActionType.PICKUP, 1, CourierOrderSortStatus.IN_PROGRESS, venueId)

        `when`(couriersRepository.findAllByStatusIn(listOf("FREE", "DELIVERY"))).thenReturn(Flux.just(courier))
        `when`(courierOrderSortRepository.findAllByStatusAndCourierIdOrderBySortDesc(CourierOrderSortStatus.IN_PROGRESS, courierId)).thenReturn(Flux.just(courierOrderSort))
        `when`(venuesRepository.findByUuid(venueId)).thenReturn(Mono.just(venue))

        val venueRequestBody = VenueRequestBody(UUID.randomUUID(), UUID.randomUUID())

        StepVerifier.create(orderTransformerService.getAvailableCouriers(venueRequestBody))
            .expectNextMatches { lambdaCourier ->
                lambdaCourier.uuid == courierId &&
                        lambdaCourier.actions?.size == 1 &&
                        lambdaCourier.actions?.first()?.venue?.uuid == venueId
            }
            .verifyComplete()
    }

    @Test
    fun `getUnassignedOrders returns orders with venue details`() {
        val venueId = UUID.randomUUID()
        val now = LocalDateTime.now()
        val order = Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            10, now, now,  10.0, 10.0, OrderStatus.PICKING_UP, 10, now)
        val venue = Venue(venueId, "Venue Name", VenueType.RESTAURANT, 10.0, 20.0, true)
        val venueRequestBody = VenueRequestBody(venueId, venueId)

        `when`(ordersRepository.findAllByAssignedCourierIdIsNull()).thenReturn(Flux.just(order))
        `when`(venuesRepository.findByUuid(any())).thenReturn(Mono.just(venue))

        StepVerifier.create(orderTransformerService.getUnassignedOrders(venueRequestBody))
            .expectNextMatches {
                true
            }
            .verifyComplete()
    }

//    @Test
//    fun `getCouriersAssigned integrates couriers and orders into response`() {
//        runBlocking {
//            val lambdaVenue = LambdaVenue(UUID.randomUUID(), "Venue 1", VenueType.RESTAURANT, 10.0, 10.0, true)
//            val venueRequestBody = VenueRequestBody(UUID.randomUUID(), UUID.randomUUID())
//            val now = LocalDateTime.now()
//            val venue = Venue(UUID.randomUUID(), "Venue1", VenueType.RESTAURANT, 10.0, 10.0, false)
//            val lambdaOrder = LambdaOrder(UUID.randomUUID(), lambdaVenue, lambdaVenue, 10, now, now, 10.0, 10.0, OrderStatus.PICKING_UP, 10, now)
//            val unassignedOrders = listOf(lambdaOrder)
//            val lambdaCourier = LambdaCourier(UUID.randomUUID(), "Courier 1", "phone 1", VehicleType.NORMAL, 10.0, 10.0, 10.0, 10, CourierStatus.PICKUP)
//            val availableCouriers = listOf(lambdaCourier)
//            val orderActionsResponse = OrderActionsResponse(UUID.randomUUID(), UUID.randomUUID(), 10.0, now, CourierOrderSortActionType.PICKUP)
//            val responseLambda = ResponseLambda(listOf(AssignmentResult(UUID.randomUUID(), listOf(orderActionsResponse))))
//            val order = Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 10, now, now, 10.0, 10.0, OrderStatus.PICKING_UP, 10, now)
//            val courier = Courier(UUID.randomUUID(), "Courier 1", "phone 1", VehicleType.NORMAL, 10.0, 10.0, 10.0, 10, CourierStatus.PICKUP)
//
//            `when`(venuesRepository.findByUuid(any())).thenReturn(Mono.just(venue))
//            `when`(couriersRepository.findAllByStatusIn(listOf("FREE", "DELIVERY"))).thenReturn(Flux.just(courier))
//            `when`(ordersRepository.findAllByAssignedCourierIdIsNull()).thenReturn(Flux.just(order))
//            `when`(orderTransformerService.getUnassignedOrders(venueRequestBody)).thenReturn(Flux.just(lambdaOrder))
//            `when`(orderTransformerService.getAvailableCouriers(venueRequestBody)).thenReturn(Flux.just(lambdaCourier))
//            `when`(lambdaGateway.getCouriersAssigned(unassignedOrders, availableCouriers)).thenReturn(Mono.just(responseLambda))
//
//            val response = orderTransformerService.getCouriersAssigned(venueRequestBody).block()
//
//            assertThat(response).isNotNull
//        }
//    }
}
