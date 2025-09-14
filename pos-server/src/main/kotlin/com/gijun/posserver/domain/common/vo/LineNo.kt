package com.gijun.posserver.domain.common.vo

@JvmInline
value class LineNo(val value: Int) {

    init {
        require(value > 0) { "Line number must be positive" }
        require(value <= 9999) { "Line number cannot exceed 9999" }
    }

    fun next(): LineNo = LineNo(value + 1)

    override fun toString(): String = value.toString()
}