package ro.bagatictac.itfest2023be.domain.web

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ro.bagatictac.itfest2023be.domain.model.*
import ro.bagatictac.itfest2023be.domain.service.OrdersService
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class OrdersControllerTest {

    private val ordersService: OrdersService = mock(OrdersService::class.java)
    private val ordersController = OrdersController(ordersService)

    @Test
    fun `updateActions should update order actions`() {
        val updateActionOrdersRequest = UpdateActionOrdersRequest(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), OrderStatus.PICKING_UP, CourierStatus.PICKUP)
        whenever(ordersService.updateActions(updateActionOrdersRequest)).thenReturn(Mono.just(1L))

        val result = ordersController.updateActions(updateActionOrdersRequest).block()

        assert(result == 1L)
        verify(ordersService).updateActions(updateActionOrdersRequest)
        verifyNoMoreInteractions(ordersService)
    }

    @Test
    fun `getOrders should return all orders`() {
        val now = LocalDateTime.now()
        val order = Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            10, now, now,  10.0, 10.0, OrderStatus.PICKING_UP, 10, now)
        val pickupVenue = Venue(UUID.randomUUID(), "Pickup Venue", VenueType.SHELTER, 10.0, 10.0, true)
        val deliveryVenue = Venue(UUID.randomUUID(), "Delivery Venue", VenueType.SHELTER, 10.0, 10.0, true)

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

        whenever(ordersService.getOrders()).thenReturn(Flux.just(expectedResponse))

        val result = ordersController.getOrders().collectList().block()

        assert(result == listOf(expectedResponse))
        verify(ordersService).getOrders()
    }

    @Test
    fun `getCourierOrderSortStatusInProgress should return order sort status for courier`() {
        val courierId = UUID.randomUUID()
        val orderId = UUID.randomUUID()
        val venue = Venue(UUID.randomUUID(), "Venue1", VenueType.RESTAURANT, 10.0, 10.0, false)
        val courierOrderSort = CourierOrderSort(UUID.randomUUID(), courierId, orderId, CourierOrderSortActionType.PICKUP, 10, CourierOrderSortStatus.IN_PROGRESS, UUID.randomUUID())
        val expected = CourierOrderSortResponse(courierOrderSort, venue)
        whenever(ordersService.getCourierOrderSortStatusInProgress(courierId)).thenReturn(Flux.just(expected))

        val result = ordersController.getCourierOrderSortStatusInProgress(courierId).collectList().block()

        assert(result == listOf(expected))
        verify(ordersService).getCourierOrderSortStatusInProgress(courierId)
    }

    @Test
    fun `saveOrder should save and return the order`() {
        val orderDto = OrderDto(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, LocalDateTime.now(), LocalDateTime.now(), 5.0, 10.0, OrderStatus.PICKING_UP, 20, LocalDateTime.now())
        val order = Order(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 5, LocalDateTime.now(), LocalDateTime.now(), 5.0, 10.0, OrderStatus.PICKING_UP, 20, LocalDateTime.now())

        whenever(ordersService.saveOrder(any())).thenReturn(Mono.just(order))

        val result = ordersController.saveOrder(orderDto).block()

        assert(result == order)
    }

    @Test
    fun `getBatches should retrieve batches based on venue`() {
        val venueRequestBody = VenueRequestBody(UUID.randomUUID(), UUID.randomUUID())
        val venue = Venue(UUID.randomUUID(), "Venue1", VenueType.RESTAURANT, 10.0, 10.0, false)


        whenever(ordersService.getBatches(venueRequestBody)).thenReturn(Mono.just(venue))

        val result = ordersController.getBatches(venueRequestBody).block()

        assert(result == venue)
        verify(ordersService).getBatches(venueRequestBody)
    }
}