package com.gijun.mainserver.application.dto.command.organization.hq

data class DeleteHqCommand(
    val hqId: Long,
    val requestId: String
)
