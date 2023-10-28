package ro.bagatictac.itfest2023be.domain.service

import org.springframework.stereotype.Service
import ro.bagatictac.itfest2023be.domain.model.Courier
import ro.bagatictac.itfest2023be.domain.repository.CouriersRepository
import ro.bagatictac.itfest2023be.domain.web.CourierCoordsRequest
import ro.bagatictac.itfest2023be.domain.web.CourierStatusRequest
import ro.bagatictac.itfest2023be.domain.web.AllCouriersResponse

@Service
class CouriersService(
    private val couriersRepository: CouriersRepository
) {

    fun getAllCouriersIdAndName() = couriersRepository.findAll()
        .map { AllCouriersResponse(it.uuid!!, it.name) }

    fun saveCourier(courier: Courier) = couriersRepository.save(courier)

    fun changeCourierStatus(courierStatusRequest: CourierStatusRequest) =
        couriersRepository.updateCourierStatusByUuid(courierStatusRequest.status, courierStatusRequest.uuid)


    fun updateCourierCoords(courierCoordsRequest: CourierCoordsRequest) =
        couriersRepository.updateCourierLongLatByUuid(courierCoordsRequest.lat, courierCoordsRequest.long, courierCoordsRequest.uuid)
}