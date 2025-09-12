package com.gijun.mainserver.domain.organization.hq.model

import com.gijun.mainserver.domain.common.vo.Address
import com.gijun.mainserver.domain.common.vo.Email
import com.gijun.mainserver.domain.common.vo.PhoneNumber

data class Hq(
    val id: Long?,
    val name: String,
    val representative: String,
    val address: Address,
    val email: Email,
    val phoneNumber: PhoneNumber
)