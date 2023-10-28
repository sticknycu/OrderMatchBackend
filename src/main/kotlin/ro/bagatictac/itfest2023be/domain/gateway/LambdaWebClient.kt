package ro.bagatictac.itfest2023be.domain.gateway

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Component
class LambdaWebClient(private val lambdaWebClient: WebClient) {

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
            .flatMap { response -> response.bodyToMono(LambdaResponse::class.java) }

}

data class LambdaRequest(
    val unasignedOrders: List<LambdaOrder>,
    val availableCouriers: List<LambdaCourier>
)

class LambdaCourier(
    val name: String,
    val phoneNumber: String,
    val vehicleType: String,
    val vehicleEmission: Double,
    val long: Double,
    val lat: Double,
    val maxCapacity: Int,
    val status: String,
    val actions: List<LambdaCourierOrderSort>
)

data class LambdaVenue(
    val name: String,
    val typeOfVenue: String,
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
    val status: String,
    val capacity: Int,
    val createdAt: Date
)

class LambdaCourierOrderSort(
    val actionType: String,
    val sort: Int,
    val status: String,
    val venueId: LambdaVenue
)

data class LambdaResponse(
    val response: String
)