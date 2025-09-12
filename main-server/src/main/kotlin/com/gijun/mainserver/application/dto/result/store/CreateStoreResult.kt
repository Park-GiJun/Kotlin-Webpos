package com.gijun.mainserver.application.dto.result.store

data class CreateStoreResult(
    val storeId: Long,
    val hqId: Long,
    val name: String
)