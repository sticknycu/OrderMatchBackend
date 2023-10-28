package ro.bagatictac.itfest2023be.domain.model

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Entity
@Table("couriers")
class Courier(
    @Id
    @Column(name = "uuid")
    @UuidGenerator
    val uuid: UUID? = null,

    val name: String,

    @Column(name = "phone_number")
    val phoneNumber: String,

    @Column(name = "vehicle_type")
    val vehicleType: VehicleType,

    @Column(name = "vehicle_emission")
    val vehicleEmission: Double,

    @Column(name = "last_long")
    val lastLong: Double,

    @Column(name = "last_lat")
    val lastLat: Double,

    @Column(name = "max_capacity")
    val maxCapacity: Int,

    val status: CourierStatus
)

enum class CourierStatus {
    FREE, PICKUP, DELIVERY, DEACTIVATED
}

enum class VehicleType {
    ELECTRIC, NORMAL
}
