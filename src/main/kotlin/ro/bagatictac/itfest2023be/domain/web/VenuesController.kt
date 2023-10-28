package ro.bagatictac.itfest2023be.domain.web

import org.springframework.web.bind.annotation.*
import ro.bagatictac.itfest2023be.domain.model.Venue
import ro.bagatictac.itfest2023be.domain.model.VenueType
import ro.bagatictac.itfest2023be.domain.service.VenuesService
import java.util.*

@RestController
@RequestMapping("/venues")
@CrossOrigin("*")
class VenuesController(
    private val venuesService: VenuesService
) {

    @GetMapping("/without-donating")
    fun getAllVenuesWithoutDonating() = venuesService.getAllVenuesWithoutDonating()

    @GetMapping
    fun getAllVenues() = venuesService.getVenues()

    @PostMapping
    fun saveVenue(@RequestBody venueDto: VenueDto) = venuesService.saveVenue(venueDto.toVenue())
}

private fun VenueDto.toVenue() =
    Venue(
        name = this.name,
        typeOfVenue = this.typeOfVenue,
        long = this.long,
        lat = this.lat,
        isDonating = this.isDonating
    )

data class AllVenuesWithoutDonating(
    val id: UUID,
    val name: String
)

data class AllVenuesResponse(
    val venueId: UUID,
    val venueName: String,
    val isDonating: Boolean,
    val venueType: VenueType
)

data class VenueDto(
    val name: String,
    val typeOfVenue: VenueType,
    val long: Double,
    val lat: Double,
    val isDonating: Boolean
)