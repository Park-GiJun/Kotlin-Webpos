package com.gijun.mainserver.domain.common.vo

data class Address(
    val street: String,
    val city: String,
    val zipCode: String
) {
    fun fullAddress(): String = "$street, $city $zipCode"
}