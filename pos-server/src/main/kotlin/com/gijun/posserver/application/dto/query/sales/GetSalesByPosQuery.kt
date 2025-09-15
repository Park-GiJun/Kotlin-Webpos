package com.gijun.posserver.application.dto.query.sales

import java.time.LocalDateTime

data class GetSalesByPosQuery(
    val posId: Long,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null
) {
    init {
        require(posId > 0) { "POS ID must be positive" }
        if (startDate != null && endDate != null) {
            require(startDate <= endDate) { "Start date must be before or equal to end date" }
        }
    }
}