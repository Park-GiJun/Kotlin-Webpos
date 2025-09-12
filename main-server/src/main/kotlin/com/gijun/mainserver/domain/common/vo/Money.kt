package com.gijun.mainserver.domain.common.vo

import java.math.BigDecimal

@JvmInline
value class Money(val value: BigDecimal) {
    init {
        require(value >= BigDecimal.ZERO) { "금액은 0 이상이어야 합니다" }
    }

    operator fun plus(other: Money) = Money(value + other.value)
    operator fun minus(other: Money) = Money(value - other.value)
}