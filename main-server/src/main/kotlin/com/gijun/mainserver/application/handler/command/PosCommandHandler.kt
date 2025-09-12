package com.gijun.mainserver.application.handler.command

import com.gijun.mainserver.application.dto.command.pos.CreatePosCommand
import com.gijun.mainserver.application.dto.result.pos.CreatePosResult
import com.gijun.mainserver.application.mapper.PosApplicationMapper
import com.gijun.mainserver.application.port.`in`.organziation.pos.CreatePosUseCase
import com.gijun.mainserver.application.port.out.organization.pos.PosCommandRepository
import com.gijun.mainserver.application.port.out.organization.pos.PosQueryRepository
import com.gijun.mainserver.application.port.out.organization.store.StoreQueryRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PosCommandHandler(
    private val posCommandRepository: PosCommandRepository,
    private val posQueryRepository: PosQueryRepository,
    private val storeQueryRepository: StoreQueryRepository
) : CreatePosUseCase {

    @Transactional
    override fun createPosExecute(command: CreatePosCommand): CreatePosResult {
        // Check if Store exists
        if (!storeQueryRepository.existsById(command.storeId)) {
            throw NoSuchElementException("Store not found with id: ${command.storeId}")
        }

        // Check for duplicate POS number
        if (posQueryRepository.existsByPosNumber(command.posNumber)) {
            throw DuplicateKeyException("POS number already exists: ${command.posNumber}")
        }

        return command
            .let { PosApplicationMapper.toDomainFromCreatePosCommand(it) }
            .let { posCommandRepository.save(it, command.requestId) }
            .let { PosApplicationMapper.toCreatePosResultFromDomain(it) }
    }
}