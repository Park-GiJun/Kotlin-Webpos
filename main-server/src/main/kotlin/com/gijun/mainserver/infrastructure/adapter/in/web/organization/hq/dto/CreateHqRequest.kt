package com.gijun.mainserver.infrastructure.adapter.`in`.web.organization.hq.dto

data class CreateHqRequest(
    val name: String,
    val representation: String,
    val street: String,
    val city: String,
    val zipCode: String,
    val email: String,
    val phoneNumber: String
)