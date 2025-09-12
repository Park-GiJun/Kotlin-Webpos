package com.gijun.mainserver.domain.common.vo

import java.math.BigDecimal

@JvmInline
value class Quantity(val value: BigDecimal) {
    init {
        require(value >= BigDecimal.ZERO) { "수량은 0 이상이어야 합니다" }
    }
}