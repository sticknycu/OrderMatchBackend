package ro.bagatictac.itfest2023be.domain.repository

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ro.bagatictac.itfest2023be.domain.model.Venue
import java.util.UUID

interface VenuesRepository : R2dbcRepository<Venue, UUID> {

    fun findByUuid(uuid: UUID): Mono<Venue>

    fun findByUuidIn(uuids: List<UUID>): Flux<Venue>

    @Query("SELECT * FROM venues WHERE is_donating = :isDonating")
    fun findAllByDonating(isDonating: Boolean): Flux<Venue>
}