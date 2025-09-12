package com.gijun.mainserver.application.dto.command.hq

data class DeleteHqCommand(
    val hqId: Long,
    val requestId: String
)
