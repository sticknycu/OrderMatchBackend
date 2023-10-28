package ro.bagatictac.itfest2023be.domain.scheduler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ro.bagatictac.itfest2023be.domain.repository.OrdersRepository
import ro.bagatictac.itfest2023be.domain.service.OrderTransformerService
import ro.bagatictac.itfest2023be.domain.web.VenueRequestBody

@Component
class UpdateOrdersScheduler(
    private val ordersRepository: OrdersRepository,
    private val orderTransformerService: OrderTransformerService
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    @Scheduled(fixedRate = 1000 * 60) // EVERY MINUTE
    fun runCronJob() {
        log.info("Start cronjob for assigning courier order id which are null")
        ordersRepository.findAllByAssignedCourierIdIsNull()
            .flatMap {
                log.info("Assign couriers!")
                orderTransformerService.getCouriersAssigned(VenueRequestBody(it.pickupVenueId, it.deliveryVenueId))
            }.subscribe()
    }
}