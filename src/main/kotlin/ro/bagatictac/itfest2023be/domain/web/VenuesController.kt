package ro.bagatictac.itfest2023be.domain.web

import org.springframework.web.bind.annotation.*
import ro.bagatictac.itfest2023be.domain.model.Venue
import ro.bagatictac.itfest2023be.domain.model.VenueType
import ro.bagatictac.itfest2023be.domain.service.VenuesService

@RestController
@RequestMapping("/venues")
@CrossOrigin("*")
class VenuesController(
    private val venuesService: VenuesService
) {

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

data class VenueDto(
    val name: String,
    val typeOfVenue: VenueType,
    val long: Double,
    val lat: Double,
    val isDonating: Boolean
)