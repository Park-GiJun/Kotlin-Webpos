package com.gijun.mainserver.application.dto.result.pos

data class CreatePosResult(
    val posId: Long,
    val storeId: Long,
    val posNumber: String,
    val status: String
)