package ro.bagatictac.itfest2023be.domain.gateway

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import ro.bagatictac.itfest2023be.domain.model.*
import ro.bagatictac.itfest2023be.domain.repository.CourierOrderSortRepository
import ro.bagatictac.itfest2023be.domain.repository.OrdersRepository
import ro.bagatictac.itfest2023be.infrastructure.configuration.LocalDateTimeDeserializer
import java.time.LocalDateTime
import java.util.*

@Component
class LambdaGateway(private val lambdaWebClient: WebClient,
    private val ordersRepository: OrdersRepository,
    private val courierOrderSortRepository: CourierOrderSortRepository) {

    fun getCouriersAssigned(
        unassignedOrders: List<LambdaOrder>,
        availableCouriers: List<LambdaCourier>
    ): Mono<ResponseLambda> =
        lambdaWebClient.post()
            .body(BodyInserters.fromValue(LambdaRequest(unassignedOrders, availableCouriers)))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toMono()
            .flatMap { response ->
                response
                    .bodyToMono(ResponseLambda::class.java)
            }
            .flatMap { responseLambda -> update(responseLambda).then(Mono.just(responseLambda)) }

    private fun update(responseLambda: ResponseLambda): Flux<CourierOrderSort> {
        return Flux.fromIterable(responseLambda.assignmentResults)
            .concatMap { assignmentResult ->
                val courierId = assignmentResult.courierId

                courierOrderSortRepository.findAllByCourierIdOrderBySortDesc(courierId)
                    .take(1)
                    .flatMap { courierOrderSort ->
                        Flux.fromIterable(assignmentResult.orderActions)
                            .concatMap { action ->
                                ordersRepository.updateAssignedCourierId(courierId, action.orderId)
                                    .flatMap {
                                        courierOrderSortRepository.save(
                                            CourierOrderSort(
                                                courierId = courierId,
                                                orderId = action.orderId,
                                                venueId = action.venueId,
                                                sort = courierOrderSort.sort + 1,
                                                actionType = action.actionType,
                                                status = CourierOrderSortStatus.IN_PROGRESS
                                            )
                                        )
                                    }
                            }
                    }.switchIfEmpty(Flux.fromIterable(assignmentResult.orderActions)
                        .flatMap { action ->
                            ordersRepository.updateAssignedCourierId(courierId, action.orderId)
                                .flatMap {
                                    courierOrderSortRepository.save(
                                        CourierOrderSort(
                                            courierId = courierId,
                                            orderId = action.orderId,
                                            venueId = action.venueId,
                                            sort = 1,
                                            actionType = action.actionType,
                                            status = CourierOrderSortStatus.IN_PROGRESS
                                        )
                                    )
                                }
                        })
            }
    }
}

data class LambdaRequest(
    val unassignedOrders: List<LambdaOrder>,
    val availableCouriers: List<LambdaCourier>
)

data class LambdaCourier(
    val uuid: UUID,
    val name: String,
    val phoneNumber: String,
    val vehicleType: VehicleType,
    val vehicleEmission: Double,
    val long: Double,
    val lat: Double,
    val maxCapacity: Int,
    val status: CourierStatus,
    val actions: List<LambdaCourierOrderSort>? = listOf()
)

data class LambdaVenue(
    val uuid: UUID,
    val name: String,
    val typeOfVenue: VenueType,
    val long: Double,
    val lat: Double,
    val isDonating: Boolean
)

data class LambdaOrder(
    val uuid: UUID,
    val pickupVenue: LambdaVenue,
    val deliveryVenue: LambdaVenue,
    val rating: Int,
    val pickupTime: LocalDateTime,
    val deliveryTime: LocalDateTime,
    val pickupDistance: Double,
    val deliveryDistance: Double,
    val status: OrderStatus,
    val capacity: Int,
    val createdAt: LocalDateTime
)

data class LambdaCourierOrderSort(
    val uuid: UUID,
    val actionType: CourierOrderSortActionType,
    val sort: Int,
    val status: CourierOrderSortStatus,
    val venue: LambdaVenue
)

data class ResponseLambda(
    val assignmentResults: List<AssignmentResult>
)

data class AssignmentResult(
    val courierId: UUID,
    val orderActions: List<OrderActionsResponse>
)

data class OrderActionsResponse(
    val orderId: UUID,
    val venueId: UUID,
    val estimatedDistance: Double,
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val estimatedTime: LocalDateTime,
    val actionType: CourierOrderSortActionType
)