package com.gijun.mainserver.application.mapper

import com.gijun.mainserver.application.dto.command.store.CreateStoreCommand
import com.gijun.mainserver.application.dto.result.store.CreateStoreResult
import com.gijun.mainserver.application.dto.result.store.StoreResult
import com.gijun.mainserver.domain.common.exception.NullIdException
import com.gijun.mainserver.domain.common.vo.Address
import com.gijun.mainserver.domain.common.vo.Email
import com.gijun.mainserver.domain.common.vo.PhoneNumber
import com.gijun.mainserver.domain.organization.store.model.Store
import java.time.Instant

object StoreApplicationMapper {

    fun toDomainFromCreateStoreCommand(command: CreateStoreCommand): Store {
        return Store(
            hqId = command.hqId,
            name = command.name,
            representative = command.manager,
            address = Address(command.street, command.city, command.zipCode),
            phoneNumber = PhoneNumber(command.phoneNumber),
            email = Email(command.email),
            id = 0L
        )
    }

    fun toCreateStoreResultFromDomain(domain: Store): CreateStoreResult {
        return domain.id?.let {
            CreateStoreResult(
                storeId = it,
                hqId = domain.hqId,
                name = domain.name
            )
        }
            ?: throw NullIdException("Store")
    }

    fun toStoreResultFromDomain(domain: Store): StoreResult {
        return StoreResult(
            storeId = domain.id ?: 0L,
            hqId = domain.hqId,
            storeName = domain.name,
            managerName = domain.representative,
            street = domain.address.street,
            city = domain.address.city,
            zipCode = domain.address.zipCode,
            email = domain.email.value,
            phoneNumber = domain.phoneNumber.value,
            businessNumber = "", // TODO: Add business number to domain model
            createdAt = Instant.now(), // TODO: Get from entity when available
            updatedAt = Instant.now()  // TODO: Get from entity when available
        )
    }
}