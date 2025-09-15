package com.gijun.posserver.application.dto.command.sales

import java.time.LocalDateTime

data class CreateSalesCommand(
    val hqId: Long,
    val storeId: Long,
    val posId: Long,
    val billNo: String,
    val saleType: Boolean,
    val saleDate: LocalDateTime,
    val details: List<CreateSalesDetailCommand>,
    val payments: List<CreateSalesPaymentCommand>
) {
    init {
        require(hqId > 0) { "HQ ID must be positive" }
        require(storeId > 0) { "Store ID must be positive" }
        require(posId > 0) { "POS ID must be positive" }
        require(billNo.isNotBlank()) { "Bill number cannot be blank" }
        require(details.isNotEmpty()) { "Sales must have at least one detail item" }
        require(payments.isNotEmpty()) { "Sales must have at least one payment" }
    }
}