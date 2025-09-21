package com.gijun.mainserver.application.handler.command.product

import com.gijun.mainserver.application.dto.command.product.productContainer.AdjustProductContainerCommand
import com.gijun.mainserver.application.dto.command.product.productContainer.ContainerAdjustmentType
import com.gijun.mainserver.application.dto.result.product.productContainer.AdjustProductContainerResult
import com.gijun.mainserver.application.handler.cache.CacheHandler
import com.gijun.mainserver.application.port.`in`.product.AdjustProductContainerUseCase
import com.gijun.mainserver.application.port.out.product.productContainer.ProductContainerCommandRepository
import com.gijun.mainserver.application.port.out.product.productContainer.ProductContainerQueryRepository
import com.gijun.mainserver.domain.common.exception.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductContainerCommandHandler(
    private val productContainerCommandRepository: ProductContainerCommandRepository,
    private val productContainerQueryRepository: ProductContainerQueryRepository,
    private val cacheHandler: CacheHandler
) : AdjustProductContainerUseCase {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun adjustProductContainerExecute(command: AdjustProductContainerCommand): AdjustProductContainerResult {
        val productContainer = productContainerQueryRepository.findByHqIdAndContainerId(
            command.hqId,
            command.containerId
        ) ?: throw EntityNotFoundException("ProductContainer", "hqId: ${command.hqId}, containerId: ${command.containerId}")

        val unitQtyBefore = productContainer.unitQty
        val usageQtyBefore = productContainer.usageQty

        val adjustedContainer = when (command.adjustmentType) {
            ContainerAdjustmentType.INCREASE -> productContainer.increase(command.unitQty, command.usageQty)
            ContainerAdjustmentType.DECREASE -> productContainer.decrease(command.unitQty, command.usageQty)
        }

        val savedContainer = productContainerCommandRepository.update(adjustedContainer)

        // Invalidate container cache
        cacheHandler.delete(CacheHandler.Keys.productContainerKey(savedContainer.hqId, savedContainer.containerId))
        // Invalidate HQ container list cache
        cacheHandler.deletePattern("product:container:list:hq:${savedContainer.hqId}:*")
        cacheHandler.delete(CacheHandler.Keys.productContainerListByHqKey(savedContainer.hqId))
        logger.debug("Cache invalidated for container: ${savedContainer.containerId} at HQ: ${savedContainer.hqId} after adjustment")

        return AdjustProductContainerResult(
            productContainerId = savedContainer.id!!,
            hqId = savedContainer.hqId,
            containerId = savedContainer.containerId,
            containerName = savedContainer.containerName,
            unitQtyBefore = unitQtyBefore,
            usageQtyBefore = usageQtyBefore,
            unitQtyAfter = savedContainer.unitQty,
            usageQtyAfter = savedContainer.usageQty
        )
    }
}