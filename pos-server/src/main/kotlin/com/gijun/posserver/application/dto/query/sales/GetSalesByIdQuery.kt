package com.gijun.posserver.application.dto.query.sales

data class GetSalesByIdQuery(
    val id: Long
) {
    init {
        require(id > 0) { "Sales ID must be positive" }
    }
}