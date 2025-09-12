package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.store.mapper

import com.gijun.mainserver.application.dto.result.store.StoreResult
import com.gijun.mainserver.domain.common.vo.Address
import com.gijun.mainserver.domain.common.vo.Email
import com.gijun.mainserver.domain.common.vo.PhoneNumber
import com.gijun.mainserver.domain.organization.store.model.Store
import com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.store.entity.StoreJpaEntity
import java.time.ZoneOffset

object StorePersistenceMapper {

    fun toEntity(domain: Store): StoreJpaEntity {
        return StoreJpaEntity(
            id = domain.id ?: 0L,
            hqId = domain.hqId,
            name = domain.name,
            representative = domain.representative,
            street = domain.address.street,
            city = domain.address.city,
            zipCode = domain.address.zipCode,
            email = domain.email.value,
            phoneNumber = domain.phoneNumber.value
        )
    }

    fun toDomain(entity: StoreJpaEntity): Store {
        return Store(
            id = if (entity.id == 0L) null else entity.id,
            hqId = entity.hqId,
            name = entity.name,
            representative = entity.representative,
            address = Address(
                street = entity.street,
                city = entity.city,
                zipCode = entity.zipCode
            ),
            email = entity.email?.let { Email(it) } ?: Email("default@store.com"),
            phoneNumber = entity.phoneNumber?.let { PhoneNumber(it) } ?: PhoneNumber("000-0000-0000")
        )
    }

    fun updateEntity(existingEntity: StoreJpaEntity, domain: Store): StoreJpaEntity {
        return StoreJpaEntity(
            id = existingEntity.id,
            hqId = domain.hqId,
            name = domain.name,
            representative = domain.representative,
            street = domain.address.street,
            city = domain.address.city,
            zipCode = domain.address.zipCode,
            email = domain.email.value,
            phoneNumber = domain.phoneNumber.value
        )
    }

    fun toStoreResult(entity: StoreJpaEntity): StoreResult {
        return StoreResult(
            storeId = entity.id,
            hqId = entity.hqId,
            storeName = entity.name,
            managerName = entity.representative,
            street = entity.street,
            city = entity.city,
            zipCode = entity.zipCode,
            email = entity.email ?: "",
            phoneNumber = entity.phoneNumber ?: "",
            businessNumber = "", // TODO: Add to entity when needed
            createdAt = entity.createdAt.atOffset(ZoneOffset.UTC).toInstant(),
            updatedAt = entity.updatedAt.atOffset(ZoneOffset.UTC).toInstant()
        )
    }
}