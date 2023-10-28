package ro.bagatictac.itfest2023be.domain.gateway

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import ro.bagatictac.itfest2023be.domain.model.Courier
import ro.bagatictac.itfest2023be.domain.model.Order

@Component
class LambdaWebClient(private val lambdaWebClient: WebClient) {

    // status=free, delivery couries and list of orders with couriers id = null
    // requestbody
    // unasignedOrders: []
    // unavailableCouriers: []

    // response:
    fun getCouriersAssigned(unasignedOrders: List<Order>, unavailableCouriers: List<Courier>) =
        lambdaWebClient.post()
            .body(BodyInserters.fromValue(LambdaRequest(unasignedOrders, unavailableCouriers)))
            .accept(MediaType.APPLICATION_JSON)
            .exchange().flatMap { response -> response.bodyToMono(LambdaResponse::class.java) }

}

data class LambdaRequest(
    private val unasignedOrders: List<Order>,
    private val unavailableCouriers: List<Courier>
)

data class LambdaResponse(
    val response: String
)