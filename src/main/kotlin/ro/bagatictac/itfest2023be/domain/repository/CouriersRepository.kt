package ro.bagatictac.itfest2023be.domain.repository

import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import ro.bagatictac.itfest2023be.domain.model.Courier
import java.util.UUID

interface CouriersRepository : R2dbcRepository<Courier, UUID> {

    fun findAllByStatusIn(courierStatus: List<String>): Flux<Courier>
}
