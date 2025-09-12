package com.gijun.mainserver.application.mapper

import com.gijun.mainserver.application.dto.command.pos.CreatePosCommand
import com.gijun.mainserver.application.dto.result.pos.CreatePosResult
import com.gijun.mainserver.application.dto.result.pos.PosResult
import com.gijun.mainserver.domain.common.exception.NullIdException
import com.gijun.mainserver.domain.organization.pos.model.Pos
import com.gijun.mainserver.domain.organization.pos.vo.PosStatus
import java.time.Instant

object PosApplicationMapper {

    fun toDomainFromCreatePosCommand(command: CreatePosCommand): Pos {
        return Pos(
            storeId = command.storeId,
            posNumber = command.posNumber,
            deviceType = command.deviceType,
            status = PosStatus.valueOf(command.status),
            id = 0L
        )
    }

    fun toCreatePosResultFromDomain(domain: Pos): CreatePosResult {
        return domain.id?.let {
            CreatePosResult(
                posId = it,
                storeId = domain.storeId,
                posNumber = domain.posNumber,
                status = domain.status.name
            )
        }
            ?: throw NullIdException("POS")
    }

    fun toPosResultFromDomain(domain: Pos): PosResult {
        return PosResult(
            posId = domain.id ?: 0L,
            storeId = domain.storeId,
            hqId = 0L, // TODO: Get HqId from store relationship when available
            posName = domain.posNumber,
            location = domain.deviceType,
            maxOrderCount = 100, // TODO: Add to domain model when needed
            createdAt = Instant.now(), // TODO: Get from entity when available
            updatedAt = Instant.now()  // TODO: Get from entity when available
        )
    }
}