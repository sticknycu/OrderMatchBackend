package ro.bagatictac.itfest2023be.domain.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import ro.bagatictac.itfest2023be.domain.model.Courier
import ro.bagatictac.itfest2023be.domain.model.CourierStatus
import java.util.UUID

interface CouriersRepository : R2dbcRepository<Courier, UUID> {

    fun findAllByStatusIn(courierStatus: List<String>): Flux<Courier>

    @Query("UPDATE couriers SET last_long = :lastLong, last_lat = :lastLat WHERE uuid = :uuid")
    fun updateCourierLongLatByUuid(lastLat: Double, lastLong: Double, uuid: UUID)

    @Query("UPDATE couriers SET status = :status WHERE uuid = :uuid")
    fun updateCourierStatusByUuid(status: CourierStatus, uuid: UUID)
}
