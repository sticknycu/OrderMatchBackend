package ro.bagatictac.itfest2023be.domain.web

import org.springframework.web.bind.annotation.*
import ro.bagatictac.itfest2023be.domain.model.Order
import ro.bagatictac.itfest2023be.domain.service.OrdersService

@RestController
@RequestMapping("/orders")
@CrossOrigin("*")
class OrdersController(
    private val ordersService: OrdersService
) {

    @PostMapping
    fun saveOrder(@RequestBody order: Order) = ordersService.saveOrder(order)
}