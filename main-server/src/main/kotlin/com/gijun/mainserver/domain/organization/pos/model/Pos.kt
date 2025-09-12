package com.gijun.mainserver.domain.organization.pos.model

import com.gijun.mainserver.domain.organization.pos.vo.PosStatus

data class Pos(
    val id: Long?,
    val storeId: Long,
    val posNumber: String,
    val deviceType: String,
    val status: PosStatus
) {
    init {
        require(storeId > 0) { "POS must belong to a store (storeId)" }
        require(posNumber.isNotBlank()) { "POS number cannot be blank" }
        require(deviceType.isNotBlank()) { "Device type cannot be blank" }
    }
}