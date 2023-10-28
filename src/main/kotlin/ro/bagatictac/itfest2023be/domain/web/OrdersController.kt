package ro.bagatictac.itfest2023be.domain.web

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ro.bagatictac.itfest2023be.domain.model.Order
import ro.bagatictac.itfest2023be.domain.service.OrdersService

@RestController
@RequestMapping("/orders")
class OrdersController(
    private val ordersService: OrdersService
) {

    @PostMapping
    fun saveOrder(@RequestBody order: Order) = ordersService.saveOrder(order)
}