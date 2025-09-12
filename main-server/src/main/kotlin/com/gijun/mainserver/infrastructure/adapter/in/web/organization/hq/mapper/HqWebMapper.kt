package com.gijun.mainserver.infrastructure.adapter.`in`.web.organization.hq.mapper

import com.gijun.mainserver.application.dto.command.hq.CreateHqCommand
import com.gijun.mainserver.application.dto.result.hq.CreateHqResult
import com.gijun.mainserver.infrastructure.adapter.`in`.web.organization.hq.dto.CreateHqRequest
import com.gijun.mainserver.infrastructure.adapter.`in`.web.organization.hq.dto.CreateHqResponse
import java.util.UUID

object HqWebMapper {

    fun toCommand(request: CreateHqRequest): CreateHqCommand {
        return CreateHqCommand(
            name = request.name,
            representation = request.representation,
            street = request.street,
            city = request.city,
            zipCode = request.zipCode,
            email = request.email,
            phoneNumber = request.phoneNumber,
            requestId = UUID.randomUUID().toString()
        )
    }

    fun toResponse(result: CreateHqResult): CreateHqResponse {
        return CreateHqResponse(
            hqId = result.hqId,
            name = result.name
        )
    }
}