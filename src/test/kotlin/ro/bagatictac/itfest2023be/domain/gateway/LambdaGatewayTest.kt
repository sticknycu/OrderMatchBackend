package ro.bagatictac.itfest2023be.domain.gateway

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import ro.bagatictac.itfest2023be.domain.model.*
import ro.bagatictac.itfest2023be.domain.repository.CourierOrderSortRepository
import ro.bagatictac.itfest2023be.domain.repository.OrdersRepository
import java.time.LocalDateTime
import java.util.*


@ExtendWith(MockitoExtension::class)
internal class LambdaGatewayTest {

    private val webClientMock = mock<WebClient>()
    private val requestBodyUriSpecMock = mock<WebClient.RequestBodyUriSpec>()
    private val requestBodySpecMock = mock<WebClient.RequestBodySpec>()
    private val responseSpecMock = mock<WebClient.ResponseSpec>()

    private val ordersRepository = mock<OrdersRepository>()
    private val courierOrderSortRepository = mock<CourierOrderSortRepository>()

    private val lambdaGateway = LambdaGateway(webClientMock, ordersRepository, courierOrderSortRepository)

    @Test
    fun getCouriersAssigned_thenReturnResponse() {
        val now = LocalDateTime.now()
        val courierId = UUID.randomUUID()
        val orderId = UUID.randomUUID()
        val lambdaVenue = LambdaVenue(UUID.randomUUID(), "Venue 1", VenueType.RESTAURANT, 10.0, 10.0, true)
        val unassignedOrders = listOf(
            LambdaOrder(UUID.randomUUID(), lambdaVenue, lambdaVenue, 10, now, now, 10.0, 10.0, OrderStatus.PICKING_UP, 10, now)
        )
        val availableCouriers = listOf(
            LambdaCourier(UUID.randomUUID(), "Courier 1", "phone 1", VehicleType.NORMAL, 10.0, 10.0, 10.0, 10, CourierStatus.PICKUP)
        )
        val courierOrderSort = CourierOrderSort(UUID.randomUUID(), courierId, orderId, CourierOrderSortActionType.PICKUP, 10, CourierOrderSortStatus.IN_PROGRESS, UUID.randomUUID())

        val responseLambda = ResponseLambda(listOf(AssignmentResult(UUID.randomUUID(), listOf(OrderActionsResponse(UUID.randomUUID(), UUID.randomUUID(), 10.0, now, CourierOrderSortActionType.PICKUP)))))

        whenever(webClientMock.post()).thenReturn(requestBodyUriSpecMock)
        whenever(requestBodyUriSpecMock.body(any())).thenReturn(requestBodySpecMock)
        whenever(requestBodySpecMock.accept(any())).thenReturn(requestBodySpecMock)
        whenever(requestBodySpecMock.retrieve()).thenReturn(responseSpecMock)
        whenever(responseSpecMock.bodyToMono(ResponseLambda::class.java)).thenReturn(Mono.just(responseLambda))
        whenever(courierOrderSortRepository.findAllByCourierIdOrderBySortDesc(any())).thenReturn(Flux.just(courierOrderSort))
        whenever(ordersRepository.updateAssignedCourierId(any(), any())).thenReturn(Mono.just(1L))
        whenever(courierOrderSortRepository.save(any())).thenReturn(Mono.just(courierOrderSort))

        val result = lambdaGateway.getCouriersAssigned(unassignedOrders, availableCouriers)

        StepVerifier.create(result)
            .expectNextMatches { it == responseLambda }
            .verifyComplete()

        verify(webClientMock).post()
        verify(courierOrderSortRepository).findAllByCourierIdOrderBySortDesc(any())
        verify(ordersRepository).updateAssignedCourierId(any(), any())
        verify(courierOrderSortRepository).save(any())
    }

    @Test
    fun getCouriersAssignedAndNoCouriers_thenReturnResponse() {
        val now = LocalDateTime.now()
        val courierId = UUID.randomUUID()
        val orderId = UUID.randomUUID()
        val lambdaVenue = LambdaVenue(UUID.randomUUID(), "Venue 1", VenueType.RESTAURANT, 10.0, 10.0, true)
        val unassignedOrders = listOf(
            LambdaOrder(UUID.randomUUID(), lambdaVenue, lambdaVenue, 10, now, now, 10.0, 10.0, OrderStatus.PICKING_UP, 10, now)
        )
        val availableCouriers = listOf(
            LambdaCourier(UUID.randomUUID(), "Courier 1", "phone 1", VehicleType.NORMAL, 10.0, 10.0, 10.0, 10, CourierStatus.PICKUP)
        )
        val courierOrderSort = CourierOrderSort(UUID.randomUUID(), courierId, orderId, CourierOrderSortActionType.PICKUP, 10, CourierOrderSortStatus.IN_PROGRESS, UUID.randomUUID())

        val responseLambda = ResponseLambda(listOf(AssignmentResult(UUID.randomUUID(), listOf(OrderActionsResponse(UUID.randomUUID(), UUID.randomUUID(), 10.0, now, CourierOrderSortActionType.PICKUP)))))

        whenever(webClientMock.post()).thenReturn(requestBodyUriSpecMock)
        whenever(requestBodyUriSpecMock.body(any())).thenReturn(requestBodySpecMock)
        whenever(requestBodySpecMock.accept(any())).thenReturn(requestBodySpecMock)
        whenever(requestBodySpecMock.retrieve()).thenReturn(responseSpecMock)
        whenever(responseSpecMock.bodyToMono(ResponseLambda::class.java)).thenReturn(Mono.just(responseLambda))
        whenever(courierOrderSortRepository.findAllByCourierIdOrderBySortDesc(any())).thenReturn(Flux.empty())
        whenever(ordersRepository.updateAssignedCourierId(any(), any())).thenReturn(Mono.just(1L))
        whenever(courierOrderSortRepository.save(any())).thenReturn(Mono.just(courierOrderSort))

        val result = lambdaGateway.getCouriersAssigned(unassignedOrders, availableCouriers)

        StepVerifier.create(result)
            .expectNextMatches { it == responseLambda }
            .verifyComplete()

        verify(webClientMock).post()
        verify(courierOrderSortRepository).findAllByCourierIdOrderBySortDesc(any())
        verify(ordersRepository).updateAssignedCourierId(any(), any())
        verify(courierOrderSortRepository).save(any())
    }
}