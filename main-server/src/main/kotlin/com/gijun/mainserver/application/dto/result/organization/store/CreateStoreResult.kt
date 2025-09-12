package com.gijun.mainserver.application.dto.result.organization.store

data class CreateStoreResult(
    val storeId: Long,
    val hqId: Long,
    val name: String
)