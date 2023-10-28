package ro.bagatictac.itfest2023be.domain.service

import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono
import ro.bagatictac.itfest2023be.domain.gateway.*
import ro.bagatictac.itfest2023be.domain.model.*
import ro.bagatictac.itfest2023be.domain.repository.CourierOrderSortRepository
import ro.bagatictac.itfest2023be.domain.repository.CouriersRepository
import ro.bagatictac.itfest2023be.domain.repository.OrdersRepository
import ro.bagatictac.itfest2023be.domain.repository.VenuesRepository
import ro.bagatictac.itfest2023be.domain.web.VenueRequestBody

@Service
class OrderTransformerService(
    private val ordersRepository: OrdersRepository,
    private val venuesRepository: VenuesRepository,
    private val couriersRepository: CouriersRepository,
    private val lambdaGateway: LambdaGateway,
    private val courierOrderSortRepository: CourierOrderSortRepository
) {

    fun computeTransformation(venueRequestBody: VenueRequestBody) {
        // SELECT * FROM couriers
        //WHERE status IN ('FREE', 'DELIVERY');

        val availableCouriers = couriersRepository.findAllByStatusIn(listOf("FREE", "DELIVERY"))
            .map { courier ->
                courierOrderSortRepository.findAllByStatusInAndCourierIdOrderBySortDesc(
                    CourierOrderSortStatus.IN_PROGRESS,
                    courier.uuid
                ).collectList()
                    .map { courierOrderSortList ->
                        courier.toLambdaCourier().copy(actions = courierOrderSortList.map { courierOrderSort ->
                            venuesRepository.findById(courierOrderSort.venueId)
                                .map { venue ->
                                    courierOrderSort.toLambdaCourierOrderSort(venue)
                                }
                        })
                    }
            }
    }

    private fun Order.toLambdaOrder(pickupVenue: Venue, deliveryVenue: Venue) =
        LambdaOrder(
            pickupVenue = pickupVenue.toLambdaVenue(),
            deliveryVenue = deliveryVenue.toLambdaVenue(),
            rating = this.rating,
            pickupTime = this.pickupTime,
            deliveryTime = this.deliveryTime,
            pickupDistance = this.pickupDistance,
            deliveryDistance = this.deliveryDistance,
            status = this.status,
            capacity = this.capacity,
            createdAt = this.createdAt
        )

    private fun Venue.toLambdaVenue() =
        LambdaVenue(
            name = this.name,
            typeOfVenue = this.typeOfVenue,
            long = this.long,
            lat = this.lat,
            isDonating = this.isDonating
        )

    private fun CourierOrderSort.toLambdaCourierOrderSort(venue: Venue) =
        LambdaCourierOrderSort(
            actionType = this.actionType,
            sort = this.sort,
            status = this.status,
            venue = venue.toLambdaVenue()
        )

    private fun Courier.toLambdaCourier() =
        LambdaCourier(
            uuid = this.uuid,
            name = this.name,
            phoneNumber = this.phoneNumber,
            vehicleType = this.vehicleType,
            vehicleEmission = this.vehicleEmission,
            long = this.lastLong,
            lat = this.lastLat,
            maxCapacity = this.maxCapacity,
            status = this.status
        )

}