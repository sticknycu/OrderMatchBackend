package ro.bagatictac.itfest2023be.domain.repository

import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import ro.bagatictac.itfest2023be.domain.model.Venue
import java.util.UUID

interface VenuesRepository : R2dbcRepository<Venue, UUID> {

    fun findByUuidIn(uuids: List<UUID>): Flux<Venue>

    fun findAllByDonating(isDonating: Boolean): Flux<Venue>
}