package com.gijun.mainserver.application.handler.command.product

import com.gijun.mainserver.application.dto.command.product.productStock.AdjustProductStockCommand
import com.gijun.mainserver.application.dto.command.product.productStock.StockAdjustmentType
import com.gijun.mainserver.application.dto.result.product.productStock.AdjustProductStockResult
import com.gijun.mainserver.application.handler.cache.CacheHandler
import com.gijun.mainserver.application.port.`in`.product.AdjustProductStockUseCase
import com.gijun.mainserver.application.port.out.product.product.ProductQueryRepository
import com.gijun.mainserver.application.port.out.product.productStock.ProductStockCommandRepository
import com.gijun.mainserver.application.port.out.product.productStock.ProductStockQueryRepository
import com.gijun.mainserver.application.port.out.product.recipeComposition.RecipeCompositionQueryRepository
import com.gijun.mainserver.domain.common.exception.EntityNotFoundException
import com.gijun.mainserver.domain.product.product.model.ProductType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class ProductStockCommandHandler(
    private val productStockCommandRepository: ProductStockCommandRepository,
    private val productStockQueryRepository: ProductStockQueryRepository,
    private val productQueryRepository: ProductQueryRepository,
    private val recipeCompositionQueryRepository: RecipeCompositionQueryRepository,
    private val cacheHandler: CacheHandler
) : AdjustProductStockUseCase {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun adjustProductStockExecute(command: AdjustProductStockCommand): AdjustProductStockResult {
        val product = productQueryRepository.findById(command.productId)
            ?: throw EntityNotFoundException("Product", command.productId.toString())

        if (product.productType == ProductType.RECIPE && command.adjustmentType == StockAdjustmentType.DECREASE) {
            return adjustRecipeProductStock(command, product.productType)
        }

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

        cacheHandler.delete(CacheHandler.Keys.productKey(savedStock.productId))
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

    private fun adjustRecipeProductStock(command: AdjustProductStockCommand, productType: ProductType): AdjustProductStockResult {
        val recipeCompositions = recipeCompositionQueryRepository.findByRecipeProductId(command.productId)

        if (recipeCompositions.isEmpty()) {
            logger.warn("Recipe product ${command.productId} has no ingredients defined")
            throw EntityNotFoundException("RecipeComposition", "recipeProductId: ${command.productId}")
        }

        recipeCompositions.forEach { composition ->
            val ingredientStock = productStockQueryRepository.findByProductIdAndContainerId(
                composition.ingredientProductId,
                command.containerId
            ) ?: throw EntityNotFoundException(
                "ProductStock",
                "ingredientProductId: ${composition.ingredientProductId}, containerId: ${command.containerId}"
            )

            val requiredQty = composition.requiredQty * command.usageQty
            val adjustedIngredientStock = ingredientStock.decrease(BigDecimal.ZERO, requiredQty)
            productStockCommandRepository.update(adjustedIngredientStock)

            cacheHandler.delete(CacheHandler.Keys.productKey(composition.ingredientProductId))
            logger.debug("Ingredient stock adjusted: ingredientProductId=${composition.ingredientProductId}, qty=$requiredQty")
        }

        return AdjustProductStockResult(
            productStockId = 0L,
            productId = command.productId,
            containerId = command.containerId,
            unitQtyBefore = BigDecimal.ZERO,
            usageQtyBefore = BigDecimal.ZERO,
            unitQtyAfter = BigDecimal.ZERO,
            usageQtyAfter = BigDecimal.ZERO
        )
    }
}