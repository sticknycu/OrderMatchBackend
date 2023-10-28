package ro.bagatictac.itfest2023be.domain.service

import org.springframework.stereotype.Service
import ro.bagatictac.itfest2023be.domain.model.Courier
import ro.bagatictac.itfest2023be.domain.repository.CouriersRepository

@Service
class CouriersService(
    private val couriersRepository: CouriersRepository
) {

    fun saveCourier(courier: Courier) = couriersRepository.save(courier)
}