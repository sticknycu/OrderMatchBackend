package ro.bagatictac.itfest2023be.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Entity
@Table("couriers")
class Courier(
    @Id
    @Column(name = "uuid")
    val uuid: UUID,

    val name: String,

    @Column(name = "phone_number")
    val phoneNumber: String,

    @Column(name = "vehicle_type")
    val vehicleType: String,

    @Column(name = "vehicle_emission")
    val vehicleEmission: Double,

    @Column(name = "last_long")
    val lastLong: Double,

    @Column(name = "last_lat")
    val lastLat: Double,

    @Column(name = "max_capacity")
    val maxCapacity: Int,

    val status: String
)
