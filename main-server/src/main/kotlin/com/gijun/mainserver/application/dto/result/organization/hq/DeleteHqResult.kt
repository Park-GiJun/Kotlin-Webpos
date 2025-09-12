package com.gijun.mainserver.application.dto.result.organization.hq

data class DeleteHqResult(
    val hqId: Long,
    val deleted: Boolean,
    val message: String = "HQ successfully deleted"
)