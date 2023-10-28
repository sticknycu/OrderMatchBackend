package ro.bagatictac.itfest2023be.domain.service

import org.springframework.stereotype.Service
import ro.bagatictac.itfest2023be.domain.model.Order
import ro.bagatictac.itfest2023be.domain.repository.OrdersRepository

@Service
class OrdersService(
    private val ordersRepository: OrdersRepository
) {

    fun saveOrder(order: Order) = ordersRepository.save(order)
}