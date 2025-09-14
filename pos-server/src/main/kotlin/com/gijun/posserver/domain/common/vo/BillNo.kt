package com.gijun.posserver.domain.common.vo

@JvmInline
value class BillNo(val value: String) {

    init {
        require(value.isNotBlank()) { "Bill number cannot be blank" }
        require(value.length <= 50) { "Bill number cannot exceed 50 characters" }
        require(value.matches(Regex("^[A-Za-z0-9\\-_]+$"))) { "Bill number can only contain alphanumeric characters, hyphens, and underscores" }
    }

    override fun toString(): String = value
}