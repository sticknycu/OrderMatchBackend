package ro.bagatictac.itfest2023be.domain.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import ro.bagatictac.itfest2023be.domain.model.Courier
import ro.bagatictac.itfest2023be.domain.model.CourierOrderSort
import ro.bagatictac.itfest2023be.domain.repository.CourierOrderSortRepository
import ro.bagatictac.itfest2023be.domain.repository.CouriersRepository
import ro.bagatictac.itfest2023be.domain.repository.VenuesRepository
import ro.bagatictac.itfest2023be.domain.web.CourierCoordsRequest
import ro.bagatictac.itfest2023be.domain.web.CourierStatusRequest
import ro.bagatictac.itfest2023be.domain.web.AllCouriersResponse
import ro.bagatictac.itfest2023be.domain.web.CourierActionsResponse
import java.util.*

@Service
class CouriersService(
    private val couriersRepository: CouriersRepository,
    private val venuesRepository: VenuesRepository,
    private val courierOrderSortRepository: CourierOrderSortRepository
) {

    fun getCourierActions(courierId: UUID) = courierOrderSortRepository.findAllByCourierId(courierId)
        .flatMap { courierOrderSort ->
            venuesRepository.findByUuid(courierOrderSort.venueId).map {
                CourierActionsResponse(courierOrderSort.actionType, it)
            }
        }

    fun getAllCouriersIdAndName() = couriersRepository.findAll()
        .map { AllCouriersResponse(it.uuid!!, it.name) }

    fun getAllCouriers() = couriersRepository.findAll()

    fun saveCourier(courier: Courier) = couriersRepository.save(courier)

    fun changeCourierStatus(courierStatusRequest: CourierStatusRequest) =
        couriersRepository.updateCourierStatusByUuid(courierStatusRequest.status, courierStatusRequest.uuid)


    fun updateCourierCoords(courierCoordsRequest: CourierCoordsRequest) =
        couriersRepository.updateCourierLongLatByUuid(courierCoordsRequest.lat, courierCoordsRequest.long, courierCoordsRequest.uuid)
}