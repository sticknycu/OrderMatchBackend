package ro.bagatictac.itfest2023be.domain.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ro.bagatictac.itfest2023be.domain.gateway.LambdaRequest
import ro.bagatictac.itfest2023be.domain.gateway.LambdaResponse
import ro.bagatictac.itfest2023be.domain.repository.CourierRepository
import ro.bagatictac.itfest2023be.domain.repository.OrderRepository
import ro.bagatictac.itfest2023be.domain.repository.VenueRepository
import ro.bagatictac.itfest2023be.domain.web.VenueRequestBody
import java.util.*

@Service
class OrderTransformerService(
    private val orderRepository: OrderRepository,
    private val venueRepository: VenueRepository,
    private val courierRepository: CourierRepository
) {

    fun computeTransformation(venueRequestBody: VenueRequestBody): Flux<LambdaResponse> {
        // SELECT * FROM couriers
        //WHERE status IN ('FREE', 'DELIVERY');
        return venueRepository.findByUuidIn(listOf(venueRequestBody.deliveryVenueId, venueRequestBody.pickUpVenueId))
            .flatMap { venue ->
                courierRepository.findAllByStatusIn(listOf("FREE", "DELIVERY")).collectList()
                    .flatMap { courier ->
                        orderRepository.findAllByAssignedCourierIdIsNull().collectList()
                            .map { order ->
                                LambdaResponse("Response")
                            }.defaultIfEmpty(LambdaResponse("No orders"))
                    }.defaultIfEmpty(LambdaResponse("Empty Couriers"))
            }.defaultIfEmpty(LambdaResponse("Empty venue"))
    }
}