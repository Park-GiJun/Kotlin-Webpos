package com.gijun.mainserver.application.handler.command.organization

import com.gijun.mainserver.application.dto.command.organization.hq.CreateHqCommand
import com.gijun.mainserver.application.dto.command.organization.hq.DeleteHqCommand
import com.gijun.mainserver.application.dto.command.organization.hq.UpdateHqCommand
import com.gijun.mainserver.application.dto.result.organization.hq.CreateHqResult
import com.gijun.mainserver.application.dto.result.organization.hq.DeleteHqResult
import com.gijun.mainserver.application.dto.result.organization.hq.UpdateHqResult
import com.gijun.mainserver.application.mapper.HqApplicationMapper
import com.gijun.mainserver.application.port.`in`.organziation.hq.CreateHqUseCase
import com.gijun.mainserver.application.port.`in`.organziation.hq.DeleteHqUseCase
import com.gijun.mainserver.application.port.`in`.organziation.hq.UpdateHqUseCase
import com.gijun.mainserver.application.port.out.organization.hq.HqCommandRepository
import com.gijun.mainserver.application.port.out.organization.hq.HqQueryRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class HqCommandHandler(
    private val hqCommandRepository: HqCommandRepository,
    private val hqQueryRepository: HqQueryRepository,
) : CreateHqUseCase, UpdateHqUseCase, DeleteHqUseCase {

    @Transactional
    override fun createHqExecute(command: CreateHqCommand): CreateHqResult {
        if (hqQueryRepository.existsByName(command.name)) {
            throw DuplicateKeyException("Hq name already exists : {$command.name}")
        }
        return command
            .let { HqApplicationMapper.toDomainFromCreateHqCommand(it) }
            .let { hqCommandRepository.save(it, command.requestId) }
            .let { HqApplicationMapper.toCreateHqResultFromDomain(it) }
    }

    @Transactional
    override fun updateHqExecute(command: UpdateHqCommand): UpdateHqResult {
        val existingHq = hqQueryRepository.findById(command.hqId)
            ?: throw NoSuchElementException("HQ not found with id: ${command.hqId}")

        val updatedFields = mutableListOf<String>()

        command.name?.let {
            if (it != existingHq.name && hqQueryRepository.existsByName(it)) {
                throw DuplicateKeyException("Hq name already exists: $it")
            }
            updatedFields.add("name")
        }
        command.representation?.let { updatedFields.add("representation") }
        command.street?.let { updatedFields.add("street") }
        command.city?.let { updatedFields.add("city") }
        command.zipCode?.let { updatedFields.add("zipCode") }
        command.email?.let { updatedFields.add("email") }
        command.phoneNumber?.let { updatedFields.add("phoneNumber") }

        return command
            .let { HqApplicationMapper.toDomainFromUpdateHqCommand(it, existingHq) }
            .let { hqCommandRepository.update(it, command.requestId) }
            .let { HqApplicationMapper.toUpdateHqResultFromDomain(it, updatedFields) }
    }

    @Transactional
    override fun deleteHqExecute(command: DeleteHqCommand): DeleteHqResult {
        if (!hqQueryRepository.existsById(command.hqId)) {
            throw NoSuchElementException("HQ not found with id: ${command.hqId}")
        }

        hqCommandRepository.deleteById(command.hqId, command.requestId)

        return DeleteHqResult(
            hqId = command.hqId,
            deleted = true
        )
    }
}