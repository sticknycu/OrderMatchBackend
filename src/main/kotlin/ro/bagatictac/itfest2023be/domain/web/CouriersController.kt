package ro.bagatictac.itfest2023be.domain.web

import org.springframework.web.bind.annotation.*
import ro.bagatictac.itfest2023be.domain.model.Courier
import ro.bagatictac.itfest2023be.domain.model.CourierStatus
import ro.bagatictac.itfest2023be.domain.model.VehicleType
import ro.bagatictac.itfest2023be.domain.service.CouriersService
import java.util.*

@RestController
@RequestMapping("/couriers")
@CrossOrigin("*")
class CouriersController(
    private val couriersService: CouriersService
) {

    @GetMapping("/all-id-name")
    fun getAllCouriersIdAndName() =
        couriersService.getAllCouriersIdAndName()

    @PostMapping
    fun saveCourier(@RequestBody courierDto: CourierDto) =
        couriersService.saveCourier(courierDto.toCourier())

    @PatchMapping("/status")
    fun changeCourierStatus(@RequestBody courierStatusRequest: CourierStatusRequest) =
        couriersService.changeCourierStatus(courierStatusRequest)

    @PatchMapping("/coords")
    fun updateCourierCoords(@RequestBody courierCoordsRequest: CourierCoordsRequest) =
        couriersService.updateCourierCoords(courierCoordsRequest)
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

data class AllCouriersResponse(
    val uuid: UUID,
    val name: String
)

data class CourierStatusRequest(
    val uuid: UUID,
    val status: CourierStatus
)

data class CourierCoordsRequest(
    val uuid: UUID,
    val lat: Double,
    val long: Double
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