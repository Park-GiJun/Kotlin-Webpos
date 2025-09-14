package com.gijun.mainserver.application.handler.command.product

import com.gijun.mainserver.application.dto.command.product.productStock.AdjustProductStockCommand
import com.gijun.mainserver.application.dto.command.product.productStock.StockAdjustmentType
import com.gijun.mainserver.application.dto.result.product.productStock.AdjustProductStockResult
import com.gijun.mainserver.application.port.`in`.product.AdjustProductStockUseCase
import com.gijun.mainserver.application.port.out.product.productStock.ProductStockCommandRepository
import com.gijun.mainserver.application.port.out.product.productStock.ProductStockQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductStockCommandHandler(
    private val productStockCommandRepository: ProductStockCommandRepository,
    private val productStockQueryRepository: ProductStockQueryRepository
) : AdjustProductStockUseCase {

    override fun adjustProductStockExecute(command: AdjustProductStockCommand): AdjustProductStockResult {
        val productStock = productStockQueryRepository.findByProductIdAndStoreId(
            command.productId,
            command.storeId
        ) ?: throw IllegalArgumentException(
            "ProductStock not found for productId: ${command.productId}, storeId: ${command.storeId}"
        )

        val unitQtyBefore = productStock.unitQty
        val usageQtyBefore = productStock.usageQty

        val adjustedStock = when (command.adjustmentType) {
            StockAdjustmentType.INCREASE -> productStock.increase(command.unitQty, command.usageQty)
            StockAdjustmentType.DECREASE -> productStock.decrease(command.unitQty, command.usageQty)
        }

        val savedStock = productStockCommandRepository.update(adjustedStock)

        return AdjustProductStockResult(
            productStockId = savedStock.id!!,
            productId = savedStock.productId,
            storeId = savedStock.storeId,
            unitQtyBefore = unitQtyBefore,
            usageQtyBefore = usageQtyBefore,
            unitQtyAfter = savedStock.unitQty,
            usageQtyAfter = savedStock.usageQty
        )
    }
}