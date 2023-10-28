package ro.bagatictac.itfest2023be.infrastructure.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.DefaultUriBuilderFactory
import reactor.core.publisher.Mono

@Configuration
class WebClientConfiguration {

    @Bean
    fun lambdaWebClient(): WebClient =
        WebClient.create("http://192.168.1.135:4009/dev/dispatch")
}