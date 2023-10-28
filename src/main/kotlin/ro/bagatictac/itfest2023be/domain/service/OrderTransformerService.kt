package ro.bagatictac.itfest2023be.domain.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import ro.bagatictac.itfest2023be.domain.gateway.LambdaResponse
import ro.bagatictac.itfest2023be.domain.repository.CouriersRepository
import ro.bagatictac.itfest2023be.domain.repository.OrdersRepository
import ro.bagatictac.itfest2023be.domain.repository.VenuesRepository
import ro.bagatictac.itfest2023be.domain.web.VenueRequestBody

@Service
class OrderTransformerService(
    private val ordersRepository: OrdersRepository,
    private val venuesRepository: VenuesRepository,
    private val couriersRepository: CouriersRepository
) {

    fun computeTransformation(venueRequestBody: VenueRequestBody): Flux<LambdaResponse> {
        // SELECT * FROM couriers
        //WHERE status IN ('FREE', 'DELIVERY');
        return venuesRepository.findByUuidIn(listOf(venueRequestBody.deliveryVenueId, venueRequestBody.pickUpVenueId))
            .flatMap { venue ->
                couriersRepository.findAllByStatusIn(listOf("FREE", "DELIVERY")).collectList()
                    .flatMap { courier ->
                        ordersRepository.findAllByAssignedCourierIdIsNull().collectList()
                            .map { order ->
                                LambdaResponse("Response")
                            }.defaultIfEmpty(LambdaResponse("No orders"))
                    }.defaultIfEmpty(LambdaResponse("Empty Couriers"))
            }.defaultIfEmpty(LambdaResponse("Empty venue"))
    }
}