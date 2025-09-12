package com.gijun.mainserver.application.dto.result.hq

data class DeleteHqResult(
    val hqId: Long,
    val deleted: Boolean,
    val message: String = "HQ successfully deleted"
)