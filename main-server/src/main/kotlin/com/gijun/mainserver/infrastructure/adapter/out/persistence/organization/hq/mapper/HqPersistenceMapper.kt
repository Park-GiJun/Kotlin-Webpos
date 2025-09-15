package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.hq.mapper

import com.gijun.mainserver.domain.organization.hq.model.Hq
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.hq.entity.HqJpaEntity

object HqPersistenceMapper {

    fun toEntity(domain: Hq): HqJpaEntity {
        return HqJpaEntity(
            id = domain.id ?: 0L,
            name = domain.name,
            representative = domain.representative,
            street = domain.address,
            city = "",
            zipCode = "",
            email = domain.email,
            phoneNumber = domain.phoneNumber
        )
    }

    fun toDomain(entity: HqJpaEntity): Hq {
        return Hq(
            id = if (entity.id == 0L) null else entity.id,
            name = entity.name,
            representative = entity.representative,
            address = "${entity.street} ${entity.city} ${entity.zipCode}".trim(),
            email = entity.email ?: "",
            phoneNumber = entity.phoneNumber ?: ""
        )
    }

    fun updateEntity(existingEntity: HqJpaEntity, domain: Hq): HqJpaEntity {
        return HqJpaEntity(
            id = existingEntity.id,
            name = domain.name,
            representative = domain.representative,
            street = domain.address,
            city = "",
            zipCode = "",
            email = domain.email,
            phoneNumber = domain.phoneNumber
        )
    }
}