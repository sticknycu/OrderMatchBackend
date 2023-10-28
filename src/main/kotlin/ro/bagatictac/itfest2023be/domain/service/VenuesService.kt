package ro.bagatictac.itfest2023be.domain.service

import org.springframework.stereotype.Service
import ro.bagatictac.itfest2023be.domain.model.Venue
import ro.bagatictac.itfest2023be.domain.repository.VenuesRepository

@Service
class VenuesService(
    private val venuesRepository: VenuesRepository
) {

    fun saveVenue(venue: Venue) = venuesRepository.save(venue)
}