package ro.bagatictac.itfest2023be.domain.gateway

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.kotlin.core.publisher.toMono
import ro.bagatictac.itfest2023be.domain.model.*
import java.time.LocalDateTime
import java.util.*

@Component
class LambdaGateway(private val lambdaWebClient: WebClient) {

    // status=free, delivery couries and list of orders with couriers id = null
    // requestbody
    // unasignedOrders: []
    // unavailableCouriers: []

    // response:
    fun getCouriersAssigned(unasignedOrders: List<LambdaOrder>, availableCouriers: List<LambdaCourier>) =
        lambdaWebClient.post()
            .body(BodyInserters.fromValue(LambdaRequest(unasignedOrders, availableCouriers)))
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toMono()
            .flatMap { response -> response.bodyToMono(ResponseLambda::class.java) }
}

data class LambdaRequest(
    val unasignedOrders: List<LambdaOrder>,
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
    val name: String,
    val typeOfVenue: VenueType,
    val long: Double,
    val lat: Double,
    val isDonating: Boolean
)

data class LambdaOrder(
    val pickupVenue: LambdaVenue,
    val deliveryVenue: LambdaVenue,
    val rating: Int,
    val pickupTime: Date,
    val deliveryTime: Date,
    val pickupDistance: Double,
    val deliveryDistance: Double,
    val status: OrderStatus,
    val capacity: Int,
    val createdAt: Date
)

data class LambdaCourierOrderSort(
    val actionType: CourierOrderSortActionType,
    val sort: Int,
    val status: CourierOrderSortStatus,
    val venue: LambdaVenue
)

data class ResponseLambda(
    val assignmentResults: List<OrderActionsResponse>
)

data class OrderActionsResponse(
    val orderId: UUID,
    val venueId: UUID,
    val estimatedDistance: Double,
    val estimatedTime: LocalDateTime,
    val actionType: String
)