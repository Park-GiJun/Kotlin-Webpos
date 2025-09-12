package com.gijun.mainserver.domain.common.vo

@JvmInline
value class PhoneNumber(val value: String) {
    init {
        require(isValid(value)) { "올바른 전화번호 형식이 아닙니다" }
    }

    private fun isValid(phone: String): Boolean {
        return phone.matches(Regex("^[0-9-]+$"))
    }
}