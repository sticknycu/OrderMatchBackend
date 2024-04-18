package ro.bagatictac.itfest2023be.domain.web

import junit.framework.TestCase.assertEquals
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import ro.bagatictac.itfest2023be.domain.model.*
import ro.bagatictac.itfest2023be.domain.service.CouriersService
import java.util.UUID
import kotlin.test.assertSame

@ExtendWith(MockitoExtension::class)
internal class CouriersControllerTest {
    
    private val couriersService: CouriersService = mock(CouriersService::class.java)
    private val couriersController = CouriersController(couriersService)

    @Test
    fun `getAllCouriersIdAndName should return expected result`() {
        val expected = AllCouriersResponse(UUID.randomUUID(), "Nick Name")
        whenever(couriersService.getAllCouriersIdAndName()).thenReturn(Flux.just(expected))

        val result = couriersController.getAllCouriersIdAndName().collectList().block()

        assertThat(listOf(expected)).isEqualTo(result)
        verify(couriersService).getAllCouriersIdAndName()
        verifyNoMoreInteractions(couriersService)
    }

    @Test
    fun `getAllCouriers should return all couriers`() {
        val expected = Courier(UUID.randomUUID(), "Courier 1", "1234567890", VehicleType.ELECTRIC, 100.0, 0.0, 10.5, 10, CourierStatus.FREE)
        whenever(couriersService.getAllCouriers()).thenReturn(Flux.just(expected))

        val result = couriersController.getAllCouriers().collectList().block()

        assertThat(listOf(expected)).isEqualTo(result)
        verify(couriersService).getAllCouriers()
    }

    @Test
    fun `getCourierActions should return actions for provided courier ID`() {
        val courierId = UUID.randomUUID()
        val venue = Venue(UUID.randomUUID(), "Venue1", VenueType.RESTAURANT, 10.0, 10.0, true)

        val expected = CourierActionsResponse(CourierOrderSortActionType.PICKUP, venue)
        whenever(couriersService.getCourierActions(courierId)).thenReturn(Flux.just(expected))

        val result = couriersController.getCourierActions(courierId).collectList().block()

        assertThat(listOf(expected)).isEqualTo(result)
        verify(couriersService).getCourierActions(courierId)
    }

    @Test
    fun `saveCourier should save and return the courier`() {
        val courierDto = CourierDto("Courier 1", "1234567890", VehicleType.ELECTRIC, 100.0, 0.0, 10.5, 10, CourierStatus.FREE)
        val courier = Courier(UUID.randomUUID(), "Courier 1", "1234567890", VehicleType.ELECTRIC, 100.0, 0.0, 10.5, 10, CourierStatus.FREE)

        whenever(couriersService.saveCourier(any())).thenReturn(Mono.just(courier))

        val result = couriersController.saveCourier(courierDto).block()

        assertThat(courier).isEqualTo(result)
    }

    @Test
    fun `changeCourierStatus should update status and return appropriate response`() {
        val statusRequest = CourierStatusRequest(UUID.randomUUID(), CourierStatus.DEACTIVATED)
        whenever(couriersService.changeCourierStatus(statusRequest)).thenReturn(Mono.just(1L))

        val result = couriersController.changeCourierStatus(statusRequest).block()

        assertThat(result).isEqualTo(1L)
        verify(couriersService).changeCourierStatus(statusRequest)
    }

    @Test
    fun `updateCourierCoords should update coordinates and return appropriate response`() {
        val coordsRequest = CourierCoordsRequest(UUID.randomUUID(), 10.0, 20.0)
        whenever(couriersService.updateCourierCoords(coordsRequest)).thenReturn(Mono.just(1L))

        val result = couriersController.updateCourierCoords(coordsRequest).block()

        assertThat(result).isEqualTo(1L)
        verify(couriersService).updateCourierCoords(coordsRequest)
    }
}