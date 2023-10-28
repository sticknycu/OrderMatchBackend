package ro.bagatictac.itfest2023be.domain.repository

import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ro.bagatictac.itfest2023be.domain.model.Order
import ro.bagatictac.itfest2023be.domain.model.OrderStatus
import java.util.*

interface OrdersRepository : R2dbcRepository<Order, UUID> {

    @Query("SELECT * FROM orders WHERE assigned_courier_id IS NULL")
    fun findAllByAssignedCourierIdIsNull(): Flux<Order>

    @Query("SELECT * FROM orders WHERE assigned_courier_id IS NOT NULL")
    fun findAllByAssignedCouriers(): Flux<Order>

    @Modifying
    @Query("UPDATE orders SET assigned_courier_id = :assignedCourierId, status = 'PICKING_UP' WHERE uuid = :orderId")
    fun updateAssignedCourierId(assignedCourierId: UUID, orderId: UUID): Mono<Long>

    @Modifying
    @Query("UPDATE orders SET status = :orderStatus WHERE uuid = :orderId")
    fun updateStatus(orderStatus: OrderStatus, orderId: UUID): Mono<Long>
}