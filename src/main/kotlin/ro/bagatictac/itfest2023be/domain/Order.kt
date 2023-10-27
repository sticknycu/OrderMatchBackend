package ro.bagatictac.itfest2023be.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("orders")
@Entity
class Order(
    @Id
    @Column(name = "uuid")
    private val uuid: UUID,

    @Column(name = "assigned_courier_id")
    val assignedCourierId: UUID,

    @Column(name = "pickup_venue_id")
    val pickupVenueId: UUID,

    @Column(name = "delivery_venue_id")
    val deliveryVenueId: UUID,

    val rating: Int,

    @Column(name = "pickup_time")
    val pickupTime: Date,

    @Column(name = "delivery_time")
    val deliveryTime: Date,

    @Column(name = "pickup_distance")
    val pickupDistance: Double,

    @Column(name = "delivery_distance")
    val deliveryDistance: Double,

    val status: String,

    val capacity: Int,

    @CreatedDate
    @Column(name = "created_at")
    val createdAt: Date
)