package com.gijun.mainserver.application.dto.result.hq

data class UpdateHqResult(
    val hqId: Long,
    val name: String,
    val updatedFields: List<String>
)