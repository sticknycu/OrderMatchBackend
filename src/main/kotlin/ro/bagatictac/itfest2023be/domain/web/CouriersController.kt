package ro.bagatictac.itfest2023be.domain.web

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ro.bagatictac.itfest2023be.domain.model.Courier
import ro.bagatictac.itfest2023be.domain.service.CouriersService

@RestController
@RequestMapping("/couriers")
class CouriersController(
    private val couriersService: CouriersService
) {

    @PostMapping
    fun saveCourier(@RequestBody courier: Courier) = couriersService.saveCourier(courier)
}