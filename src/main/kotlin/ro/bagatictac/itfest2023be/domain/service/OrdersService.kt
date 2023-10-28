package ro.bagatictac.itfest2023be.domain.service

import org.springframework.stereotype.Service
import ro.bagatictac.itfest2023be.domain.model.CourierOrderSortStatus
import ro.bagatictac.itfest2023be.domain.model.Order
import ro.bagatictac.itfest2023be.domain.repository.CourierOrderSortRepository
import ro.bagatictac.itfest2023be.domain.repository.CouriersRepository
import ro.bagatictac.itfest2023be.domain.repository.OrdersRepository
import ro.bagatictac.itfest2023be.domain.repository.VenuesRepository
import ro.bagatictac.itfest2023be.domain.web.CourierOrderSortResponse
import ro.bagatictac.itfest2023be.domain.web.OrderResponse
import ro.bagatictac.itfest2023be.domain.web.UpdateActionOrdersRequest
import ro.bagatictac.itfest2023be.domain.web.VenueRequestBody
import java.util.*

@Service
class OrdersService(
    private val ordersRepository: OrdersRepository,
    private val venueRepository: VenuesRepository,
    private val courierOrderSortRepository: CourierOrderSortRepository,
    private val couriersRepository: CouriersRepository
) {

    fun getOrders() = ordersRepository.findAll()
        .flatMap {
            venueRepository.findByUuid(it.pickupVenueId).flatMap { pickUpVenue ->
                venueRepository.findByUuid(it.deliveryVenueId).map { deliveryVenue ->
                    OrderResponse(
                        assignedCourierId = it.assignedCourierId,
                        pickupVenueId = pickUpVenue,
                        deliveryVenueId = deliveryVenue,
                        rating = it.rating,
                        pickupTime = it.pickupTime,
                        deliveryTime = it.deliveryTime,
                        pickupDistance = it.pickupDistance,
                        deliveryDistance = it.deliveryDistance,
                        status = it.status,
                        capacity = it.capacity,
                        createdAt = it.createdAt
                    )
                }

            }

        }

    fun getBatches(venueRequestBody: VenueRequestBody) = venueRepository.findByUuid(venueRequestBody.deliveryVenueId)

    fun saveOrder(order: Order) = ordersRepository.save(order)

    fun updateActions(updateActionOrdersRequest: UpdateActionOrdersRequest) =
        ordersRepository.updateStatus(updateActionOrdersRequest.orderStatus, updateActionOrdersRequest.orderId)
            .flatMap {
                couriersRepository.updateCourierStatusByUuid(updateActionOrdersRequest.courierStatus, updateActionOrdersRequest.courierId)
                    .flatMap { courierOrderSortRepository.updateStatusById(CourierOrderSortStatus.FINISHED, updateActionOrdersRequest.actionId) }}

    fun getCourierOrderSortStatusInProgress(courierId: UUID) =
        courierOrderSortRepository.findAllByCourierIdAndStatus(courierId, CourierOrderSortStatus.IN_PROGRESS)
            .flatMap { courierOrderSort ->
                venueRepository.findByUuid(courierOrderSort.venueId)
                    .map { CourierOrderSortResponse(courierOrderSort, it) }
            }
}