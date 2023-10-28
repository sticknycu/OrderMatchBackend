package ro.bagatictac.itfest2023be.domain.service

import org.springframework.stereotype.Service
import ro.bagatictac.itfest2023be.domain.model.Order
import ro.bagatictac.itfest2023be.domain.repository.OrdersRepository
import ro.bagatictac.itfest2023be.domain.repository.VenuesRepository
import ro.bagatictac.itfest2023be.domain.web.VenueRequestBody

@Service
class OrdersService(
    private val ordersRepository: OrdersRepository,
    private val venueRepository: VenuesRepository
) {

    fun getBatches(venueRequestBody: VenueRequestBody) = venueRepository.findByUuid(venueRequestBody.deliveryVenueId)

    fun saveOrder(order: Order) = ordersRepository.save(order)
}