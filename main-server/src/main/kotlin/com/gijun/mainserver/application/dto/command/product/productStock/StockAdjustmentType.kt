package com.gijun.mainserver.application.dto.command.product.productStock

enum class StockAdjustmentType {
    INCREASE,  // 재고 증가 (입고, 반품 등)
    DECREASE   // 재고 감소 (판매, 폐기 등)
}