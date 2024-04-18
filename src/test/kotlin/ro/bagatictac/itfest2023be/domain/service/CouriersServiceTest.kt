package ro.bagatictac.itfest2023be.domain.service

import kotlinx.coroutines.reactor.mono
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
import reactor.test.StepVerifier
import ro.bagatictac.itfest2023be.domain.model.*
import ro.bagatictac.itfest2023be.domain.repository.CourierOrderSortRepository
import ro.bagatictac.itfest2023be.domain.repository.CouriersRepository
import ro.bagatictac.itfest2023be.domain.repository.OrdersRepository
import ro.bagatictac.itfest2023be.domain.repository.VenuesRepository
import ro.bagatictac.itfest2023be.domain.service.CouriersService
import ro.bagatictac.itfest2023be.domain.web.AllCouriersResponse
import ro.bagatictac.itfest2023be.domain.web.CourierActionsResponse
import ro.bagatictac.itfest2023be.domain.web.CourierCoordsRequest
import ro.bagatictac.itfest2023be.domain.web.CourierStatusRequest
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class CouriersServiceTest {

    @Mock
    private lateinit var couriersRepository: CouriersRepository

    @Mock
    private lateinit var venuesRepository: VenuesRepository

    @Mock
    private lateinit var courierOrderSortRepository: CourierOrderSortRepository

    @InjectMocks
    private lateinit var couriersService: CouriersService

    @Test
    fun `getCourierActions returns empty list when no actions found`() = runBlocking<Unit> {
        runBlocking {
            val courierId = UUID.randomUUID()
            `when`(courierOrderSortRepository.findAllByCourierId(courierId)).thenReturn(Flux.empty())

            val result = couriersService.getCourierActions(courierId).collectList().block()

            assertThat(result).isEmpty()
        }
    }

    @Test
    fun `getCourierActions returns actions when available`() = runBlocking<Unit> {
        runBlocking {
            val courierId = UUID.randomUUID()
            val venueId = UUID.randomUUID()
            val venue = Venue(venueId, "Venue 1", VenueType.SHELTER, 0.0, 0.0, false)
            val courierOrderSort = CourierOrderSort(null, courierId, UUID.randomUUID(), CourierOrderSortActionType.PICKUP, 1, CourierOrderSortStatus.IN_PROGRESS, venueId)

            `when`(courierOrderSortRepository.findAllByCourierId(courierId)).thenReturn(Flux.just(courierOrderSort))
            `when`(venuesRepository.findByUuid(venueId)).thenReturn(Mono.just(venue))

            val result = couriersService.getCourierActions(courierId).collectList().block()

            assertThat(result).containsExactly(CourierActionsResponse(CourierOrderSortActionType.PICKUP, venue))
        }
    }

    @Test
    fun `getAllCouriersIdAndName returns list of courier IDs and names`() = runBlocking<Unit> {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()

        val mockCouriers = listOf(
            Courier(uuid1, "Courier 1", "1234567890", VehicleType.ELECTRIC, 100.0, 0.0, 10.5, 10, CourierStatus.FREE),
            Courier(uuid2, "Courier 2", "0987654321", VehicleType.ELECTRIC, 200.0, 0.1, 45.5, 10, CourierStatus.FREE)
        )

        `when`(couriersRepository.findAll()).thenReturn(Flux.fromIterable(mockCouriers))

        val result = couriersService.getAllCouriersIdAndName().collectList().block()

        val expectedResponses = listOf(
            AllCouriersResponse(uuid1, "Courier 1"),
            AllCouriersResponse(uuid2, "Courier 2")
        )
        assertThat(result).containsExactlyElementsOf(expectedResponses)
    }

    @Test
    fun `getAllCouriers returns all couriers`() = runBlocking<Unit> {
        // Explicitly set UUIDs for deterministic testing
        val uuid1 = UUID.fromString("4a9b59da-b7ab-4a5c-a2e2-8e875a7efc76")
        val uuid2 = UUID.fromString("79cef50e-ff00-4e23-9beb-953327d16836")
        val mockCouriers = listOf(
            Courier(uuid1, "Courier 1", "1234567890", VehicleType.ELECTRIC, 100.0, 0.0, 10.5, 10, CourierStatus.FREE),
            Courier(uuid2, "Courier 2", "0987654321", VehicleType.ELECTRIC, 200.0, 0.1, 45.5, 10, CourierStatus.FREE)
        )
        `when`(couriersRepository.findAll()).thenReturn(Flux.fromIterable(mockCouriers))

        val result = couriersService.getAllCouriers().collectList().block()

        assertThat(result).containsExactlyElementsOf(mockCouriers)
    }

    @Test
    fun `saveCourier saves and returns the courier`() = runBlocking<Unit> {
        // Create a courier instance
        val courier = Courier(UUID.randomUUID(), "Courier Name", "1234567890", VehicleType.NORMAL, 0.1, 52.1, -0.1, 300, CourierStatus.FREE)

        // Mock the behavior of the couriersRepository.save method
        `when`(couriersRepository.save(courier)).thenReturn(Mono.just(courier))

        // Call the method under test
        val result = couriersService.saveCourier(courier).block()

        // Assert the result
        assertThat(result).isEqualTo(courier)
    }

    @Test
    fun `changeCourierStatus updates and returns update count`() = runBlocking<Unit> {
        val courierId = UUID.randomUUID()
        val statusRequest = CourierStatusRequest(courierId, CourierStatus.DELIVERY)
        `when`(couriersRepository.updateCourierStatusByUuid(statusRequest.status, statusRequest.uuid)).thenReturn(Mono.just(1L))

        StepVerifier.create(couriersService.changeCourierStatus(statusRequest))
            .expectNext(1L)
            .verifyComplete()
    }

    @Test
    fun `updateCourierCoords updates coordinates and returns update count`() = runBlocking<Unit> {
        val courierId = UUID.randomUUID()
        val coordsRequest = CourierCoordsRequest(courierId, 52.5200, 13.4050)

        `when`(couriersRepository.updateCourierLongLatByUuid(coordsRequest.lat, coordsRequest.long, coordsRequest.uuid)).thenReturn(Mono.just(1L))

        StepVerifier.create(couriersService.updateCourierCoords(coordsRequest))
            .expectNext(1L)
            .verifyComplete()
    }
}
