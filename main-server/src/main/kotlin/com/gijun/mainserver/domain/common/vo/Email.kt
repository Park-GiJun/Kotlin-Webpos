package com.gijun.mainserver.domain.common.vo

@JvmInline
value class Email(val value: String) {
    init {
        require(isValid(value)) { "올바른 이메일 형식이 아닙니다" }
    }

    private fun isValid(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
}