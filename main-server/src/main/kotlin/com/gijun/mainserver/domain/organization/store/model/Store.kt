package com.gijun.mainserver.domain.organization.store.model

data class Store(
    val id: Long?,
    val hqId: Long,
    val name: String,
    val representative: String,
    val address: String,
    val email: String,
    val phoneNumber: String
) {
    init {
        require(hqId > 0) { "Store must belong to a headquarters (hqId)" }
        require(name.isNotBlank()) { "Store name cannot be blank" }
        require(representative.isNotBlank()) { "Representative cannot be blank" }
        require(address.isNotBlank()) { "Address cannot be blank" }
        require(email.isNotBlank()) { "Email cannot be blank" }
        require(phoneNumber.isNotBlank()) { "Phone number cannot be blank" }
    }
}