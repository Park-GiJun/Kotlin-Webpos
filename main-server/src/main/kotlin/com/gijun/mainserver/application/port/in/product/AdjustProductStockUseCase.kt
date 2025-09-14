package com.gijun.mainserver.application.port.`in`.product

import com.gijun.mainserver.application.dto.command.product.productStock.AdjustProductStockCommand
import com.gijun.mainserver.application.dto.result.product.productStock.AdjustProductStockResult

interface AdjustProductStockUseCase {
    fun adjustProductStockExecute(command: AdjustProductStockCommand): AdjustProductStockResult
}