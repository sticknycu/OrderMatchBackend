package ro.bagatictac.itfest2023be.domain.web

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ro.bagatictac.itfest2023be.domain.gateway.LambdaResponse
import ro.bagatictac.itfest2023be.domain.service.OrderTransformerService
import java.util.*

@RestController
@RequestMapping("/pickup")
class PickUpController(
    private val orderTransformerService: OrderTransformerService
) {

    @PostMapping("/assign")
    fun assignCourier(@RequestBody venueRequestBody: VenueRequestBody): Flux<LambdaResponse> =
        orderTransformerService.computeTransformation(venueRequestBody)
}

data class VenueRequestBody(
    val pickUpVenueId: UUID,
    val deliveryVenueId: UUID
)