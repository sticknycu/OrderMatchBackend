package ro.bagatictac.itfest2023be.domain.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
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

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    fun getAvailableCouriers(venueRequestBody: VenueRequestBody): Flux<LambdaCourier> {
        return couriersRepository.findAllByStatusIn(listOf("FREE", "DELIVERY"))
            .flatMap { courier ->
                courierOrderSortRepository.findAllByStatusAndCourierIdOrderBySortDesc(
                    CourierOrderSortStatus.IN_PROGRESS,
                    courier.uuid!!
                ).collectList()
                    .flatMap { courierOrderSortList ->
                        Flux.fromIterable(courierOrderSortList)
                            .flatMap { courierOrderSort ->
                                venuesRepository.findByUuid(courierOrderSort.venueId)
                                    .map { venue ->
                                        courierOrderSort.toLambdaCourierOrderSort(venue)
                                    }
                            }
                            .collectList()
                            .map { sortedCourierOrders ->
                                courier.toLambdaCourier().copy(actions = sortedCourierOrders)
                            }
                    }
            }
    }

    fun getUnassignedOrders(venueRequestBody: VenueRequestBody): Flux<LambdaOrder> {
        return ordersRepository.findAllByAssignedCourierIdIsNull()
            .flatMap { order ->
                venuesRepository.findByUuid(venueRequestBody.pickUpVenueId).flatMap { pickUpVenue ->
                    venuesRepository.findByUuid(venueRequestBody.deliveryVenueId).map { deliveryVenue ->
                        order.toLambdaOrder(pickUpVenue, deliveryVenue)
                    }
                }
            }
    }

    fun getCouriersAssigned(venueRequestBody: VenueRequestBody): Mono<ResponseLambda> {
        return getUnassignedOrders(venueRequestBody).collectList()
            .flatMap { unassignedOrders ->
                getAvailableCouriers(venueRequestBody).collectList()
                    .flatMap { availableCouriers ->
                        lambdaGateway.getCouriersAssigned(unassignedOrders, availableCouriers)
                    }
                    .onErrorResume { log.info("Cannot assign available couriers to available orders. They are too far!"); Mono.empty()}
            }
    }

    private fun Order.toLambdaOrder(pickupVenue: Venue, deliveryVenue: Venue) =
        LambdaOrder(
            uuid = this.uuid!!,
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
            uuid = this.uuid!!,
            name = this.name,
            typeOfVenue = this.typeOfVenue,
            long = this.long,
            lat = this.lat,
            isDonating = this.isDonating
        )

    private fun CourierOrderSort.toLambdaCourierOrderSort(venue: Venue) =
        LambdaCourierOrderSort(
            uuid = this.uuid!!,
            actionType = this.actionType,
            sort = this.sort,
            status = this.status,
            venue = venue.toLambdaVenue()
        )

    private fun Courier.toLambdaCourier() =
        LambdaCourier(
            uuid = this.uuid!!,
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