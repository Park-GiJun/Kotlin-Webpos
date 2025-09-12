package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.hq.mapper

import com.gijun.mainserver.domain.common.vo.Address
import com.gijun.mainserver.domain.common.vo.Email
import com.gijun.mainserver.domain.common.vo.PhoneNumber
import com.gijun.mainserver.domain.organization.hq.model.Hq
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.hq.entity.HqJpaEntity

object HqPersistenceMapper {

    fun toEntity(domain: Hq): HqJpaEntity {
        return HqJpaEntity(
            id = domain.id ?: 0L,
            name = domain.name,
            representative = domain.representative,
            street = domain.address.street,
            city = domain.address.city,
            zipCode = domain.address.zipCode,
            email = domain.email.value,
            phoneNumber = domain.phoneNumber.value
        )
    }

    fun toDomain(entity: HqJpaEntity): Hq {
        return Hq(
            id = if (entity.id == 0L) null else entity.id,
            name = entity.name,
            representative = entity.representative,
            address = Address(
                street = entity.street,
                city = entity.city,
                zipCode = entity.zipCode
            ),
            email = entity.email?.let { Email(it) } ?: Email(""),
            phoneNumber = entity.phoneNumber?.let { PhoneNumber(it) } ?: PhoneNumber(""),
        )
    }

    fun updateEntity(existingEntity: HqJpaEntity, domain: Hq): HqJpaEntity {
        return HqJpaEntity(
            id = existingEntity.id,
            name = domain.name,
            representative = domain.representative,
            street = domain.address.street,
            city = domain.address.city,
            zipCode = domain.address.zipCode,
            email = domain.email.value,
            phoneNumber = domain.phoneNumber.value
        )
    }
}