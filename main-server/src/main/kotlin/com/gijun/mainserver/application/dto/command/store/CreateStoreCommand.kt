package com.gijun.mainserver.application.dto.command.store

data class CreateStoreCommand(
    val hqId: Long,
    val name: String,
    val manager: String,
    val street: String,
    val city: String,
    val zipCode: String,
    val email: String,
    val phoneNumber: String,
    val requestId: String
)