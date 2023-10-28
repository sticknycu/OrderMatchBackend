package ro.bagatictac.itfest2023be.domain.web

import org.springframework.web.bind.annotation.*
import ro.bagatictac.itfest2023be.domain.model.Courier
import ro.bagatictac.itfest2023be.domain.model.CourierStatus
import ro.bagatictac.itfest2023be.domain.model.VehicleType
import ro.bagatictac.itfest2023be.domain.service.CouriersService

@RestController
@RequestMapping("/couriers")
@CrossOrigin("*")
class CouriersController(
    private val couriersService: CouriersService
) {

    @PostMapping
    fun saveCourier(@RequestBody courierDto: CourierDto) = couriersService.saveCourier(courierDto.toCourier())
}

private fun CourierDto.toCourier() =
    Courier(
        name = this.name,
        phoneNumber = this.phoneNumber,
        vehicleType = this.vehicleType,
        vehicleEmission = this.vehicleEmission,
        lastLong = this.lastLong,
        lastLat = this.lastLat,
        maxCapacity = this.maxCapacity,
        status = this.status
    )

data class CourierDto(
    val name: String,
    val phoneNumber: String,
    val vehicleType: VehicleType,
    val vehicleEmission: Double,
    val lastLong: Double,
    val lastLat: Double,
    val maxCapacity: Int,
    val status: CourierStatus
)