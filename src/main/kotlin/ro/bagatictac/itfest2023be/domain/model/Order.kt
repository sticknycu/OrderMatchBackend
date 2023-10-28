package ro.bagatictac.itfest2023be.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UuidGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table("orders")
@Entity
class Order(
    @Id
    @Column(name = "uuid")
    @UuidGenerator
    val uuid: UUID? = UUID.randomUUID(),

    @Column(name = "assigned_courier_id")
    val assignedCourierId: UUID? = null,

    @Column(name = "pickup_venue_id")
    val pickupVenueId: UUID,

    @Column(name = "delivery_venue_id")
    val deliveryVenueId: UUID,

    val rating: Int,

    @Column(name = "pickup_time")
    val pickupTime: LocalDateTime,

    @Column(name = "delivery_time")
    val deliveryTime: LocalDateTime,

    @Column(name = "pickup_distance")
    val pickupDistance: Double,

    @Column(name = "delivery_distance")
    val deliveryDistance: Double,

    val status: OrderStatus,

    val capacity: Int,

    @CreatedDate
    @Column(name = "created_at")
    val createdAt: LocalDateTime
)

enum class OrderStatus {
    IN_PROGRESS, FINISHED, PICKING_UP
}