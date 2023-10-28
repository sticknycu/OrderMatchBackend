package ro.bagatictac.itfest2023be.domain.web

import org.springframework.web.bind.annotation.*
import ro.bagatictac.itfest2023be.domain.model.CourierOrderSortActionType
import ro.bagatictac.itfest2023be.domain.model.Order
import ro.bagatictac.itfest2023be.domain.model.OrderStatus
import ro.bagatictac.itfest2023be.domain.model.Venue
import ro.bagatictac.itfest2023be.domain.service.OrdersService
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
class OrdersController(
    private val ordersService: OrdersService
) {

    @GetMapping
    fun getOrders() = ordersService.getOrders()

    @GetMapping("/actions/{courierId}")
    fun getCourierOrderSortStatusInProgress(@PathVariable courierId: UUID) = ordersService.getCourierOrderSortStatusInProgress(courierId)

    @PostMapping
    fun saveOrder(@RequestBody orderDto: OrderDto) = ordersService.saveOrder(orderDto.toOrder())

    @GetMapping("/batches")
    fun getBatches(@RequestBody venueRequestBody: VenueRequestBody) = ordersService.getBatches(venueRequestBody)
}

private fun OrderDto.toOrder() =
    Order(
        assignedCourierId = this.assignedCourierId,
        pickupVenueId = this.pickupVenueId,
        deliveryVenueId = this.deliveryVenueId,
        rating = this.rating,
        pickupTime = this.pickupTime,
        deliveryTime = this.deliveryTime,
        pickupDistance = this.pickupDistance,
        deliveryDistance = this.deliveryDistance,
        status = this.status,
        capacity = this.capacity,
        createdAt = this.createdAt
    )


data class OrderResponse(
    val assignedCourierId: UUID? = null,
    val pickupVenueId: Venue,
    val deliveryVenueId: Venue,
    val rating: Int,
    val pickupTime: LocalDateTime,
    val deliveryTime: LocalDateTime,
    val pickupDistance: Double,
    val deliveryDistance: Double,
    val status: OrderStatus,
    val capacity: Int,
    val createdAt: LocalDateTime
)

data class CourierActionsResponse(
    val actionType: CourierOrderSortActionType,
    val venue: Venue
)

data class OrderDto(
    val assignedCourierId: UUID? = null,
    val pickupVenueId: UUID,
    val deliveryVenueId: UUID,
    val rating: Int,
    val pickupTime: LocalDateTime,
    val deliveryTime: LocalDateTime,
    val pickupDistance: Double,
    val deliveryDistance: Double,
    val status: OrderStatus,
    val capacity: Int,
    val createdAt: LocalDateTime
)