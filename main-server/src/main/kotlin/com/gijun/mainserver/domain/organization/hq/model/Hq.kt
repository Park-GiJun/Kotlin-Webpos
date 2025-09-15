package com.gijun.mainserver.domain.organization.hq.model

data class Hq(
    val id: Long?,
    val name: String,
    val representative: String,
    val address: String,
    val email: String,
    val phoneNumber: String
) {
    init {
        require(name.isNotBlank()) { "HQ name cannot be blank" }
        require(representative.isNotBlank()) { "Representative cannot be blank" }
        require(address.isNotBlank()) { "Address cannot be blank" }
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(phoneNumber.isNotBlank()) { "Phone number cannot be blank" }
    }
}