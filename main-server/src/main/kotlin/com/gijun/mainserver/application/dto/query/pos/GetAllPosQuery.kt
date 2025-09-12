package com.gijun.mainserver.application.dto.query.pos

data class GetAllPosQuery(
    val storeId: Long? = null,
    val hqId: Long? = null,
    val includeDeleted: Boolean = false
)