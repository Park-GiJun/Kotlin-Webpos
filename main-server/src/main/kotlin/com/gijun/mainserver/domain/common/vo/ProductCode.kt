package com.gijun.mainserver.domain.common.vo

@JvmInline
value class ProductCode(val value: String) {
    init {
        require(value.isNotBlank()) { "상품코드는 필수입니다" }
    }
}