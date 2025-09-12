package com.gijun.mainserver.application.dto.command.hq

data class UpdateHqCommand(
    val hqId: Long,
    val name: String? = null,
    val representation: String? = null,
    val street: String? = null,
    val city: String? = null,
    val zipCode: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val requestId: String
)