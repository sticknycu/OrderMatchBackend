package ro.bagatictac.itfest2023be

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootApplication
@EnableR2dbcRepositories
@EnableWebFlux
class Itfest2023BeApplication

fun main(args: Array<String>) {
    runApplication<Itfest2023BeApplication>(*args)
}
