package com.gijun.posserver.application.dto.query.sales

data class GetSalesByBillNoQuery(
    val billNo: String
) {
    init {
        require(billNo.isNotBlank()) { "Bill number cannot be blank" }
    }
}