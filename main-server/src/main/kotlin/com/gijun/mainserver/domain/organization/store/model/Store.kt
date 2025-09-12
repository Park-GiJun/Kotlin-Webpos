package com.gijun.mainserver.domain.organization.store.model

import com.gijun.mainserver.domain.common.vo.Address
import com.gijun.mainserver.domain.common.vo.Email
import com.gijun.mainserver.domain.common.vo.PhoneNumber

data class Store(
    val id: Long?,
    val hqId: Long,
    val name: String,
    val representative: String,
    val address: Address,
    val email: Email,
    val phoneNumber: PhoneNumber
) {
    init {
        require(hqId > 0) { "Store must belong to a headquarters (hqId)" }
    }
}