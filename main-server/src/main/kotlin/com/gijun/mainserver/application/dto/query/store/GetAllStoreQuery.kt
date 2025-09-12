package com.gijun.mainserver.application.dto.query.store

data class GetAllStoreQuery(
    val hqId: Long? = null,
    val includeDeleted: Boolean = false
)