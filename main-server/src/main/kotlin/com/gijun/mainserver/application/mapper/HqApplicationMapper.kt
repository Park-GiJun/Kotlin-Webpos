package com.gijun.mainserver.application.mapper

import com.gijun.mainserver.application.dto.command.organization.hq.CreateHqCommand
import com.gijun.mainserver.application.dto.command.organization.hq.UpdateHqCommand
import com.gijun.mainserver.application.dto.result.organization.hq.CreateHqResult
import com.gijun.mainserver.application.dto.result.organization.hq.HqResult
import com.gijun.mainserver.application.dto.result.organization.hq.UpdateHqResult
import com.gijun.mainserver.domain.common.exception.NullIdException
import com.gijun.mainserver.domain.common.vo.Address
import com.gijun.mainserver.domain.common.vo.Email
import com.gijun.mainserver.domain.common.vo.PhoneNumber
import com.gijun.mainserver.domain.organization.hq.model.Hq
import java.time.Instant
import java.util.UUID

object HqApplicationMapper {

    fun toDomainFromCreateHqCommand(command: CreateHqCommand): Hq {
        return Hq(
            name = command.name,
            representative = command.representation,
            address = Address(command.street, command.city, command.zipCode),
            phoneNumber = PhoneNumber(command.phoneNumber),
            email = Email(command.email),
            id = 0L
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
        return Hq(
            id = command.hqId,
            name = command.name ?: existingHq.name,
            representative = command.representation ?: existingHq.representative,
            address = if (command.street != null || command.city != null || command.zipCode != null) {
                Address(
                    street = command.street ?: existingHq.address.street,
                    city = command.city ?: existingHq.address.city,
                    zipCode = command.zipCode ?: existingHq.address.zipCode
                )
            } else {
                existingHq.address
            },
            phoneNumber = command.phoneNumber?.let { PhoneNumber(it) } ?: existingHq.phoneNumber,
            email = command.email?.let { Email(it) } ?: existingHq.email
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
        return HqResult(
            hqId = UUID.randomUUID(),
            name = domain.name,
            representation = domain.representative,
            street = domain.address.street,
            city = domain.address.city,
            zipCode = domain.address.zipCode,
            email = domain.email.value,
            phoneNumber = domain.phoneNumber.value,
            createdAt = Instant.now(), // TODO: Remove - should come from entity
            updatedAt = Instant.now()  // TODO: Remove - should come from entity
        )
    }
}