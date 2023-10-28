package ro.bagatictac.itfest2023be.domain.web

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ro.bagatictac.itfest2023be.domain.model.Venue
import ro.bagatictac.itfest2023be.domain.service.VenuesService

@RestController
@RequestMapping("/venues")
class VenuesController(
    private val venuesService: VenuesService
) {

    @PostMapping
    fun saveVenue(@RequestBody venue: Venue) = venuesService.saveVenue(venue)
}