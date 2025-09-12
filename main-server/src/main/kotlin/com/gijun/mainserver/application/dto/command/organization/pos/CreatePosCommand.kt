package com.gijun.mainserver.application.dto.command.organization.pos

data class CreatePosCommand(
    val storeId: Long,
    val posNumber: String,
    val deviceType: String,
    val status: String = "ACTIVE",
    val requestId: String
)