package com.gijun.mainserver.application.handler.command

import com.gijun.mainserver.application.dto.command.store.CreateStoreCommand
import com.gijun.mainserver.application.dto.result.store.CreateStoreResult
import com.gijun.mainserver.application.mapper.StoreApplicationMapper
import com.gijun.mainserver.application.port.`in`.organziation.store.CreateStoreUseCase
import com.gijun.mainserver.application.port.out.organization.hq.HqQueryRepository
import com.gijun.mainserver.application.port.out.organization.store.StoreCommandRepository
import com.gijun.mainserver.application.port.out.organization.store.StoreQueryRepository
import com.gijun.mainserver.infrastructure.config.WriteTransaction
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Component

@Component
class StoreCommandHandler(
    private val storeCommandRepository: StoreCommandRepository,
    private val storeQueryRepository: StoreQueryRepository,
    private val hqQueryRepository: HqQueryRepository
) : CreateStoreUseCase {

    @WriteTransaction
    override fun createStoreExecute(command: CreateStoreCommand): CreateStoreResult {
        // Check if HQ exists
        if (!hqQueryRepository.existsById(command.hqId)) {
            throw NoSuchElementException("HQ not found with id: ${command.hqId}")
        }

        // Check for duplicate store name
        if (storeQueryRepository.existsByName(command.name)) {
            throw DuplicateKeyException("Store name already exists: ${command.name}")
        }

        return command
            .let { StoreApplicationMapper.toDomainFromCreateStoreCommand(it) }
            .let { storeCommandRepository.save(it, command.requestId) }
            .let { StoreApplicationMapper.toCreateStoreResultFromDomain(it) }
    }
}