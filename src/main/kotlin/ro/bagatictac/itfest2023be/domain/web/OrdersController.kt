package ro.bagatictac.itfest2023be.domain.web

import org.springframework.web.bind.annotation.*
import ro.bagatictac.itfest2023be.domain.model.Order
import ro.bagatictac.itfest2023be.domain.model.OrderStatus
import ro.bagatictac.itfest2023be.domain.service.OrdersService
import java.util.*

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
class OrdersController(
    private val ordersService: OrdersService
) {

    @PostMapping
    fun saveOrder(@RequestBody orderDto: OrderDto) = ordersService.saveOrder(orderDto.toOrder())
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

data class OrderDto(
    val assignedCourierId: UUID? = null,
    val pickupVenueId: UUID,
    val deliveryVenueId: UUID,
    val rating: Int,
    val pickupTime: Date,
    val deliveryTime: Date,
    val pickupDistance: Double,
    val deliveryDistance: Double,
    val status: OrderStatus,
    val capacity: Int,
    val createdAt: Date
)