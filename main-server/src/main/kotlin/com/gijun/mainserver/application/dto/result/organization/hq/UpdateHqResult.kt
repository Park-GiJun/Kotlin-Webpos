package com.gijun.mainserver.application.dto.result.organization.hq

data class UpdateHqResult(
    val hqId: Long,
    val name: String,
    val updatedFields: List<String>
)