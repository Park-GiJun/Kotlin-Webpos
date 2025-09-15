package com.gijun.mainserver.application.mapper

import com.gijun.mainserver.application.dto.command.organization.hq.CreateHqCommand
import com.gijun.mainserver.application.dto.command.organization.hq.UpdateHqCommand
import com.gijun.mainserver.application.dto.result.organization.hq.CreateHqResult
import com.gijun.mainserver.application.dto.result.organization.hq.HqResult
import com.gijun.mainserver.application.dto.result.organization.hq.UpdateHqResult
import com.gijun.mainserver.domain.common.exception.NullIdException
import com.gijun.mainserver.domain.organization.hq.model.Hq
import java.time.Instant
import java.util.UUID

object HqApplicationMapper {

    fun toDomainFromCreateHqCommand(command: CreateHqCommand): Hq {
        return Hq(
            id = null,
            name = command.name,
            representative = command.representation,
            address = "${command.street} ${command.city} ${command.zipCode}".trim(),
            phoneNumber = command.phoneNumber,
            email = command.email
        )
    }

    fun toCreateHqResultFromDomain(domain: Hq): CreateHqResult {
        return domain.id?.let {
            CreateHqResult(
                hqId = it,
                name = domain.name
            )
        }
            ?: throw NullIdException("HQ")
    }

    fun toDomainFromUpdateHqCommand(command: UpdateHqCommand, existingHq: Hq): Hq {
        val newAddress = if (command.street != null || command.city != null || command.zipCode != null) {
            val addressParts = existingHq.address.split(" ")
            val street = command.street ?: (if (addressParts.isNotEmpty()) addressParts[0] else "")
            val city = command.city ?: (if (addressParts.size > 1) addressParts[1] else "")
            val zipCode = command.zipCode ?: (if (addressParts.size > 2) addressParts[2] else "")
            "$street $city $zipCode".trim()
        } else {
            existingHq.address
        }

        return Hq(
            id = command.hqId,
            name = command.name ?: existingHq.name,
            representative = command.representation ?: existingHq.representative,
            address = newAddress,
            phoneNumber = command.phoneNumber ?: existingHq.phoneNumber,
            email = command.email ?: existingHq.email
        )
    }

    fun toUpdateHqResultFromDomain(domain: Hq, updatedFields: List<String>): UpdateHqResult {
        return domain.id?.let {
            UpdateHqResult(
                hqId = it,
                name = domain.name,
                updatedFields = updatedFields
            )
        }
            ?: throw NullIdException("HQ")
    }

    // This method should not be used - use HqPersistenceMapper.toHqResult instead
    // to get proper audit data from JPA entities
    @Deprecated("Use HqPersistenceMapper.toHqResult to include audit data from entities")
    fun toHqResultFromDomain(domain: Hq): HqResult {
        val addressParts = domain.address.split(" ")
        return HqResult(
            hqId = UUID.randomUUID(),
            name = domain.name,
            representation = domain.representative,
            street = if (addressParts.isNotEmpty()) addressParts[0] else "",
            city = if (addressParts.size > 1) addressParts[1] else "",
            zipCode = if (addressParts.size > 2) addressParts[2] else "",
            email = domain.email,
            phoneNumber = domain.phoneNumber,
            createdAt = Instant.now(), // TODO: Remove - should come from entity
            updatedAt = Instant.now()  // TODO: Remove - should come from entity
        )
    }
}