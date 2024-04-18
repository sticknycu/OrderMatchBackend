package ro.bagatictac.itfest2023be.domain.service

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ro.bagatictac.itfest2023be.domain.model.*
import ro.bagatictac.itfest2023be.domain.repository.CourierOrderSortRepository
import ro.bagatictac.itfest2023be.domain.repository.CouriersRepository
import ro.bagatictac.itfest2023be.domain.repository.OrdersRepository
import ro.bagatictac.itfest2023be.domain.repository.VenuesRepository
import ro.bagatictac.itfest2023be.domain.web.OrderResponse
import ro.bagatictac.itfest2023be.domain.web.UpdateActionOrdersRequest
import ro.bagatictac.itfest2023be.domain.web.VenueRequestBody
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class OrderServiceTests {

    @Mock
    lateinit var ordersRepository: OrdersRepository

    @Mock
    lateinit var venueRepository: VenuesRepository

    @Mock
    lateinit var courierOrderSortRepository: CourierOrderSortRepository

    @Mock
    lateinit var couriersRepository: CouriersRepository

    @InjectMocks
    lateinit var ordersService: OrdersService

    @Test
    fun testGetOrders() {
        runBlocking {
            val now = LocalDateTime.now()
            val order = Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                10, now, now,  10.0, 10.0, OrderStatus.PICKING_UP, 10, now)
            val venue = Venue(UUID.randomUUID(), "Venue 1", VenueType.SHELTER, 10.0, 10.0, true)
            val pickupVenue = Venue(UUID.randomUUID(), "Pickup Venue", VenueType.SHELTER, 10.0, 10.0, true)
            val deliveryVenue = Venue(UUID.randomUUID(), "Delivery Venue", VenueType.SHELTER, 10.0, 10.0, true)
            whenever(ordersRepository.findAll()).thenReturn(Flux.just(order))
            whenever(venueRepository.findByUuid(order.pickupVenueId)).thenReturn(Mono.just(pickupVenue))
            whenever(venueRepository.findByUuid(order.deliveryVenueId)).thenReturn(Mono.just(deliveryVenue))

            val expectedResponse = OrderResponse(
                assignedCourierId = order.assignedCourierId,
                pickupVenueId = pickupVenue,
                deliveryVenueId = deliveryVenue,
                rating = order.rating,
                pickupTime = order.pickupTime,
                deliveryTime = order.deliveryTime,
                pickupDistance = order.pickupDistance,
                deliveryDistance = order.deliveryDistance,
                status = order.status,
                capacity = order.capacity,
                createdAt = order.createdAt
            )

            val results = ordersService.getOrders().collectList().block()

            Assertions.assertThat(results).containsExactly(expectedResponse)
        }
    }


    @Test
    fun testGetBatches() {
        runBlocking {
            val deliveryVenueId = UUID.randomUUID()
            val requestBody = VenueRequestBody(deliveryVenueId, deliveryVenueId)
            val expectedVenue = Venue(deliveryVenueId, "Venue 1", VenueType.SHELTER, 10.0, 10.0, true)
            whenever(venueRepository.findByUuid(deliveryVenueId)).thenReturn(Mono.just(expectedVenue))

            val result = ordersService.getBatches(requestBody).blockOptional()
            assertTrue(result.isPresent)
            assertTrue(result.get() == expectedVenue)
        }
    }

    @Test
    fun testSaveOrder() {
        runBlocking {
            val now = LocalDateTime.now()
            val order = Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                10, now, now,  10.0, 10.0, OrderStatus.PICKING_UP, 10, now)
            whenever(ordersRepository.save(order)).thenReturn(Mono.just(order))

            val savedOrder = ordersService.saveOrder(order).block()

            assertEquals(order, savedOrder)
        }
    }

    @Test
    fun testUpdateActions() {
        runBlocking {
            val request = UpdateActionOrdersRequest(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), OrderStatus.PICKING_UP, CourierStatus.FREE)
            whenever(ordersRepository.updateStatus(request.orderStatus, request.orderId)).thenReturn(Mono.just(1))
            whenever(couriersRepository.updateCourierStatusByUuid(request.courierStatus, request.courierId)).thenReturn(
                Mono.just(1)
            )
            whenever(
                courierOrderSortRepository.updateStatusById(
                    CourierOrderSortStatus.FINISHED,
                    request.actionId
                )
            ).thenReturn(Mono.just(1))

            val result = ordersService.updateActions(request).block()

            Assertions.assertThat(result).isEqualTo(1)
        }
    }
}
