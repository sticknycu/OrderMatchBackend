package ro.bagatictac.itfest2023be.domain.service

import org.springframework.stereotype.Service
import ro.bagatictac.itfest2023be.domain.model.Venue
import ro.bagatictac.itfest2023be.domain.repository.VenuesRepository
import ro.bagatictac.itfest2023be.domain.web.AllVenuesResponse
import ro.bagatictac.itfest2023be.domain.web.AllVenuesWithoutDonating

@Service
class VenuesService(
    private val venuesRepository: VenuesRepository
) {

    fun getAllVenuesWithoutDonating() = venuesRepository.findAllByDonating(false)
        .map { AllVenuesWithoutDonating(it.uuid!!, it.name) }

    fun getVenues() = venuesRepository.findAll()
        .map { AllVenuesResponse(it.uuid!!, it.name, it.isDonating, it.typeOfVenue) }

    fun saveVenue(venue: Venue) = venuesRepository.save(venue)
}