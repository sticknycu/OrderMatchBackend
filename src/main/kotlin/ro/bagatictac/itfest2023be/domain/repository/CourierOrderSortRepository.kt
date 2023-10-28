package ro.bagatictac.itfest2023be.domain.repository

import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import ro.bagatictac.itfest2023be.domain.model.CourierOrderSort
import ro.bagatictac.itfest2023be.domain.model.CourierOrderSortStatus
import java.util.UUID

interface CourierOrderSortRepository : R2dbcRepository<CourierOrderSort, Long> {

    fun findAllByCourierId(courierId: UUID): Flux<CourierOrderSort>

    fun findAllByStatusAndCourierIdOrderBySortDesc(status: CourierOrderSortStatus, courierId: UUID): Flux<CourierOrderSort>
}