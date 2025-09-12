package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.pos.mapper

import com.gijun.mainserver.application.dto.result.pos.PosResult
import com.gijun.mainserver.domain.organization.pos.model.Pos
import com.gijun.mainserver.domain.organization.pos.vo.PosStatus
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.pos.entity.PosJpaEntity
import java.time.ZoneOffset

object PosPersistenceMapper {

    fun toEntity(domain: Pos, hqId: Long): PosJpaEntity {
        return PosJpaEntity(
            id = domain.id ?: 0L,
            hqId = hqId,
            storeId = domain.storeId,
            posNumber = domain.posNumber,
            deviceType = domain.deviceType,
            status = domain.status.name
        )
    }

    fun toDomain(entity: PosJpaEntity): Pos {
        return Pos(
            id = if (entity.id == 0L) null else entity.id,
            storeId = entity.storeId,
            posNumber = entity.posNumber,
            deviceType = entity.deviceType,
            status = PosStatus.valueOf(entity.status)
        )
    }

    fun updateEntity(existingEntity: PosJpaEntity, domain: Pos, hqId: Long): PosJpaEntity {
        return PosJpaEntity(
            id = existingEntity.id,
            hqId = hqId,
            storeId = domain.storeId,
            posNumber = domain.posNumber,
            deviceType = domain.deviceType,
            status = domain.status.name
        )
    }

    fun toPosResult(entity: PosJpaEntity): PosResult {
        return PosResult(
            posId = entity.id,
            storeId = entity.storeId,
            hqId = entity.hqId,
            posName = entity.posNumber,
            location = entity.deviceType,
            maxOrderCount = 100, // TODO: Add to entity when needed
            createdAt = entity.createdAt.atOffset(ZoneOffset.UTC).toInstant(),
            updatedAt = entity.updatedAt.atOffset(ZoneOffset.UTC).toInstant()
        )
    }
}