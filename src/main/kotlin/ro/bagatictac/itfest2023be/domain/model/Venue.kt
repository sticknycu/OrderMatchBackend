package ro.bagatictac.itfest2023be.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.UuidGenerator
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("venues")
@Entity
class Venue(
    @Id
    @Column(name = "uuid")
    @UuidGenerator
    val uuid: UUID? = UUID.randomUUID(),

    val name: String,

    @Column(name = "type_of_venue")
    val typeOfVenue: VenueType,

    val long: Double,

    val lat: Double,

    @Column(name = "is_donating")
    val isDonating: Boolean
)

enum class VenueType {
    RESTAURANT, MARKETPLACE, FOOD_BANK, SHELTER
}