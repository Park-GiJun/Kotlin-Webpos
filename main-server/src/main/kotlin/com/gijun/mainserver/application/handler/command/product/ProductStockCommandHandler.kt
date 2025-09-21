package com.gijun.mainserver.application.handler.command.product

import com.gijun.mainserver.application.dto.command.product.productStock.AdjustProductStockCommand
import com.gijun.mainserver.application.dto.command.product.productStock.StockAdjustmentType
import com.gijun.mainserver.application.dto.result.product.productStock.AdjustProductStockResult
import com.gijun.mainserver.application.handler.cache.CacheHandler
import com.gijun.mainserver.application.port.`in`.product.AdjustProductStockUseCase
import com.gijun.mainserver.application.port.out.product.productStock.ProductStockCommandRepository
import com.gijun.mainserver.application.port.out.product.productStock.ProductStockQueryRepository
import com.gijun.mainserver.domain.common.exception.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductStockCommandHandler(
    private val productStockCommandRepository: ProductStockCommandRepository,
    private val productStockQueryRepository: ProductStockQueryRepository,
    private val cacheHandler: CacheHandler
) : AdjustProductStockUseCase {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun adjustProductStockExecute(command: AdjustProductStockCommand): AdjustProductStockResult {
        val productStock = productStockQueryRepository.findByProductIdAndContainerId(
            command.productId,
            command.containerId
        ) ?: throw EntityNotFoundException("ProductStock", "productId: ${command.productId}, containerId: ${command.containerId}")

        val unitQtyBefore = productStock.unitQty
        val usageQtyBefore = productStock.usageQty

        val adjustedStock = when (command.adjustmentType) {
            StockAdjustmentType.INCREASE -> productStock.increase(command.unitQty, command.usageQty)
            StockAdjustmentType.DECREASE -> productStock.decrease(command.unitQty, command.usageQty)
        }

        val savedStock = productStockCommandRepository.update(adjustedStock)

        // Invalidate product cache since stock affects product data
        cacheHandler.delete(CacheHandler.Keys.productKey(savedStock.productId))
        // Only invalidate container-specific product lists
        cacheHandler.deletePattern("product:list:container:${savedStock.containerId}:*")
        logger.debug("Cache invalidated for product: ${savedStock.productId} and container: ${savedStock.containerId} after stock adjustment")

        return AdjustProductStockResult(
            productStockId = savedStock.id!!,
            productId = savedStock.productId,
            containerId = savedStock.containerId,
            unitQtyBefore = unitQtyBefore,
            usageQtyBefore = usageQtyBefore,
            unitQtyAfter = savedStock.unitQty,
            usageQtyAfter = savedStock.usageQty
        )
    }
}