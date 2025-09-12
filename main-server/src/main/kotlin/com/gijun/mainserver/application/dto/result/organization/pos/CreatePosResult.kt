package com.gijun.mainserver.application.dto.result.organization.pos

data class CreatePosResult(
    val posId: Long,
    val storeId: Long,
    val posNumber: String,
    val status: String
)