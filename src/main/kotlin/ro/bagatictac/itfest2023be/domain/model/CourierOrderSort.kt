package ro.bagatictac.itfest2023be.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("courier_order_sort")
@Entity
class CourierOrderSort(
    @Id
    val id: Long,

    @Column(name = "courier_id")
    val courierId: UUID,

    @Column(name = "order_id")
    val orderId: UUID,

    @Column(name = "action_type")
    val actionType: CourierOrderSortActionType,

    val sort: Int,

    val status: CourierOrderSortStatus,

    @Column(name = "venue_id")
    val venueId: UUID
)

enum class CourierOrderSortActionType {
    PICKUP, DROPOFF
}

enum class CourierOrderSortStatus {
    IN_PROGRESS, FINISHED
}