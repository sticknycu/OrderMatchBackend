package ro.bagatictac.itfest2023be.domain.gateway

import jakarta.persistence.Column
import jakarta.persistence.Id
import org.springframework.data.annotation.CreatedDate
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import ro.bagatictac.itfest2023be.domain.model.Courier
import ro.bagatictac.itfest2023be.domain.model.Order
import java.util.*

@Component
class LambdaWebClient(private val lambdaWebClient: WebClient) {

    // status=free, delivery couries and list of orders with couriers id = null
    // requestbody
    // unasignedOrders: []
    // unavailableCouriers: []

    // response:
    fun getCouriersAssigned(unasignedOrders: List<LambdaOrder>) =
        lambdaWebClient.post()
            .body(BodyInserters.fromValue(LambdaRequest(unasignedOrders)))
            .accept(MediaType.APPLICATION_JSON)
            .exchange().flatMap { response -> response.bodyToMono(LambdaResponse::class.java) }

}

data class LambdaRequest(
    private val unasignedOrders: List<LambdaOrder>
)

data class LambdaVanue(
    val uuid: UUID,
    val name: String,
    val typeOfVenue: String,
    val long: Double,
    val lat: Double,
    val isDonating: Boolean
)

data class LambdaOrder(
    val uuid: UUID,
    val assignedCourierId: UUID,
    val pickupVenue: LambdaVanue,
    val deliveryVenue: LambdaVanue,
    val rating: Int,
    val pickupTime: Date,
    val deliveryTime: Date,
    val pickupDistance: Double,
    val deliveryDistance: Double,
    val status: String,
    val capacity: Int,
    val createdAt: Date
)

data class LambdaResponse(
    val response: String
)