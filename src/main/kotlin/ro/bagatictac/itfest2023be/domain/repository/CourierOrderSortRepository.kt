package ro.bagatictac.itfest2023be.domain.repository

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ro.bagatictac.itfest2023be.domain.model.CourierOrderSort
import ro.bagatictac.itfest2023be.domain.model.CourierOrderSortStatus
import ro.bagatictac.itfest2023be.domain.model.CourierStatus
import java.util.UUID

interface CourierOrderSortRepository : R2dbcRepository<CourierOrderSort, Long> {

    fun findAllByCourierId(courierId: UUID): Flux<CourierOrderSort>

    fun findAllByCourierIdAndStatus(courierId: UUID, status: CourierOrderSortStatus): Flux<CourierOrderSort>

    fun findAllByCourierIdOrderBySortDesc(courierId: UUID): Flux<CourierOrderSort>

    fun findAllByStatusAndCourierIdOrderBySortDesc(status: CourierOrderSortStatus, courierId: UUID): Flux<CourierOrderSort>

    @Modifying
    @Query("UPDATE courier_order_sort SET status = :status WHERE uuid = :uuid")
    fun updateStatusById(status: CourierOrderSortStatus, uuid: UUID): Mono<Long>
}