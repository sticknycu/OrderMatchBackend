package ro.bagatictac.itfest2023be.domain.web

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ro.bagatictac.itfest2023be.domain.model.Venue
import ro.bagatictac.itfest2023be.domain.model.VenueType
import ro.bagatictac.itfest2023be.domain.service.VenuesService
import java.util.*

@ExtendWith(MockitoExtension::class)
class VenuesControllerTest {

    @Mock
    private lateinit var venuesService: VenuesService

    @InjectMocks
    private lateinit var venuesController: VenuesController

    @Test
    fun getAllVenuesWithoutDonating_thenReturnResponse() {
        runBlocking {
            val mockVenues = listOf(AllVenuesWithoutDonating(UUID.randomUUID(), "Venue 1"))
            `when`(venuesService.getAllVenuesWithoutDonating()).thenReturn(Flux.fromIterable(mockVenues))

            val result = venuesController.getAllVenuesWithoutDonating().collectList().block()

            assertThat(result).isEqualTo(mockVenues)
        }
    }

    @Test
    fun getAllVenues_thenReturnResponse() {
        runBlocking {
            val mockAllVenueResponse = listOf(
                AllVenuesResponse(UUID.randomUUID(), "Venue 1", true, VenueType.SHELTER)
            )

            `when`(venuesService.getVenues()).thenReturn(Flux.fromIterable(mockAllVenueResponse))

            val result = venuesController.getAllVenues().collectList().block()

            assertThat(result).isEqualTo(mockAllVenueResponse)
        }
    }

//    @Test
//    fun saveVenue_thenReturnResponse() {
//        runBlocking {
//            val venueDto = VenueDto("Venue 1", VenueType.SHELTER, 0.0, 0.0, false)
//            val venue = venueDto.toVenue()
//
//            `when`(venuesService.saveVenue(venue)).thenReturn(Mono.just(venue))
//
//            val result = venuesController.saveVenue(venueDto)
//
//            assertThat(result).isEqualTo(venue)
//        }
//    }
}