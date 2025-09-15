package com.gijun.posserver.application.dto.query.sales

import java.time.LocalDateTime

data class GetSalesByStoreQuery(
    val storeId: Long,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null
) {
    init {
        require(storeId > 0) { "Store ID must be positive" }
        if (startDate != null && endDate != null) {
            require(startDate <= endDate) { "Start date must be before or equal to end date" }
        }
    }
}