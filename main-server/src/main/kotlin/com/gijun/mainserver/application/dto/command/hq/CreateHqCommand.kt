package com.gijun.mainserver.application.dto.command.hq

data class CreateHqCommand(
    val name: String,
    val representation: String,
    val street: String,
    val city: String,
    val zipCode: String,
    val email: String,
    val phoneNumber: String,
    val requestId: String
)
