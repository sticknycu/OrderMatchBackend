package ro.bagatictac.itfest2023be.domain.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import ro.bagatictac.itfest2023be.domain.model.Order
import java.util.*

interface OrderRepository : R2dbcRepository<Order, UUID> {

//    @Query("SELECT * FROM orders WHERE assignedCourierId IS NULL")
    fun findAllByAssignedCourierIdIsNull(): Flux<Order>
}