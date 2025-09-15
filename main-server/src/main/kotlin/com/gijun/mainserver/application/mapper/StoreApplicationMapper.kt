package com.gijun.mainserver.application.mapper

import com.gijun.mainserver.application.dto.command.organization.store.CreateStoreCommand
import com.gijun.mainserver.application.dto.result.organization.store.CreateStoreResult
import com.gijun.mainserver.application.dto.result.organization.store.StoreResult
import com.gijun.mainserver.domain.common.exception.NullIdException
import com.gijun.mainserver.domain.organization.store.model.Store
import java.time.Instant

object StoreApplicationMapper {

    fun toDomainFromCreateStoreCommand(command: CreateStoreCommand): Store {
        return Store(
            id = null,
            hqId = command.hqId,
            name = command.name,
            representative = command.manager,
            address = "${command.street} ${command.city} ${command.zipCode}".trim(),
            phoneNumber = command.phoneNumber,
            email = command.email
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
        val addressParts = domain.address.split(" ")
        return StoreResult(
            storeId = domain.id ?: 0L,
            hqId = domain.hqId,
            storeName = domain.name,
            managerName = domain.representative,
            street = if (addressParts.isNotEmpty()) addressParts[0] else "",
            city = if (addressParts.size > 1) addressParts[1] else "",
            zipCode = if (addressParts.size > 2) addressParts[2] else "",
            email = domain.email,
            phoneNumber = domain.phoneNumber,
            businessNumber = "", // TODO: Add business number to domain model
            createdAt = Instant.now(), // TODO: Get from entity when available
            updatedAt = Instant.now()  // TODO: Get from entity when available
        )
    }
}