package com.gijun.posserver.domain.common.vo

import java.math.BigDecimal
import java.math.RoundingMode

@JvmInline
value class Money(val amount: BigDecimal) {

    init {
        require(amount.scale() <= 2) { "Money amount cannot have more than 2 decimal places" }
        require(amount >= BigDecimal.ZERO) { "Money amount cannot be negative" }
    }

    constructor(amount: Double) : this(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP))
    constructor(amount: Long) : this(BigDecimal.valueOf(amount))
    constructor(amount: Int) : this(BigDecimal.valueOf(amount.toLong()))

    operator fun plus(other: Money): Money = Money(amount + other.amount)
    operator fun minus(other: Money): Money = Money(amount - other.amount)
    operator fun times(multiplier: BigDecimal): Money = Money(amount * multiplier)
    operator fun times(multiplier: Int): Money = Money(amount * BigDecimal.valueOf(multiplier.toLong()))
    operator fun div(divisor: BigDecimal): Money = Money(amount.divide(divisor, 2, RoundingMode.HALF_UP))
    operator fun div(divisor: Int): Money = Money(amount.divide(BigDecimal.valueOf(divisor.toLong()), 2, RoundingMode.HALF_UP))

    operator fun compareTo(other: Money): Int = amount.compareTo(other.amount)

    fun isZero(): Boolean = amount == BigDecimal.ZERO
    fun isPositive(): Boolean = amount > BigDecimal.ZERO

    companion object {
        val ZERO = Money(BigDecimal.ZERO)

        fun of(amount: BigDecimal): Money = Money(amount.setScale(2, RoundingMode.HALF_UP))
        fun of(amount: Double): Money = Money(amount)
        fun of(amount: Long): Money = Money(amount)
        fun of(amount: Int): Money = Money(amount)
    }
}