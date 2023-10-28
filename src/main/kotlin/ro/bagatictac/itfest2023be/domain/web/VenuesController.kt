package ro.bagatictac.itfest2023be.domain.web

import org.springframework.web.bind.annotation.*
import ro.bagatictac.itfest2023be.domain.model.Venue
import ro.bagatictac.itfest2023be.domain.service.VenuesService

@RestController
@RequestMapping("/venues")
@CrossOrigin("*")
class VenuesController(
    private val venuesService: VenuesService
) {

    @PostMapping
    fun saveVenue(@RequestBody venue: Venue) = venuesService.saveVenue(venue)
}