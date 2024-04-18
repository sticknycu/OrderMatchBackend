package ro.bagatictac.itfest2023be.domain.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import ro.bagatictac.itfest2023be.domain.model.Venue
import ro.bagatictac.itfest2023be.domain.model.VenueType
import ro.bagatictac.itfest2023be.domain.repository.VenuesRepository
import ro.bagatictac.itfest2023be.domain.service.VenuesService
import ro.bagatictac.itfest2023be.domain.web.AllVenuesResponse
import ro.bagatictac.itfest2023be.domain.web.AllVenuesWithoutDonating
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class VenuesServiceTest {

    @Mock
    private lateinit var venuesRepository: VenuesRepository

    @InjectMocks
    private lateinit var venuesService: VenuesService

    @Test
    fun `getAllVenuesWithoutDonating returns all venues not donating`() {
        val venue = Venue(UUID.randomUUID(), "Venue1", VenueType.RESTAURANT, 10.0, 10.0, false)
        `when`(venuesRepository.findAllByDonating(false)).thenReturn(Flux.just(venue))

        StepVerifier.create(venuesService.getAllVenuesWithoutDonating())
            .expectNext(AllVenuesWithoutDonating(venue.uuid!!, venue.name))
            .verifyComplete()
    }

    @Test
    fun `getVenues returns all venues with details`() {
        val venue = Venue(UUID.randomUUID(), "Venue1", VenueType.RESTAURANT, 10.0, 10.0, true)
        `when`(venuesRepository.findAll()).thenReturn(Flux.just(venue))

        StepVerifier.create(venuesService.getVenues())
            .expectNext(AllVenuesResponse(venue.uuid!!, venue.name, venue.isDonating, venue.typeOfVenue))
            .verifyComplete()
    }

    @Test
    fun `saveVenue saves and returns the venue`() {
        val venue = Venue(UUID.randomUUID(), "Venue1", VenueType.RESTAURANT, 10.0, 10.0, true)
        `when`(venuesRepository.save(venue)).thenReturn(Mono.just(venue))

        StepVerifier.create(venuesService.saveVenue(venue))
            .expectNext(venue)
            .verifyComplete()
    }
}
