package com.gijun.mainserver.application.handler.command.product

import com.gijun.mainserver.application.dto.command.product.productContainer.CreateProductContainerCommand
import com.gijun.mainserver.application.dto.command.product.productContainer.UpdateProductContainerCommand
import com.gijun.mainserver.application.dto.command.product.productContainer.DeleteProductContainerCommand
import com.gijun.mainserver.application.dto.result.product.productContainer.CreateProductContainerResult
import com.gijun.mainserver.application.dto.result.product.productContainer.UpdateProductContainerResult
import com.gijun.mainserver.application.dto.result.product.productContainer.DeleteProductContainerResult
import com.gijun.mainserver.application.handler.cache.CacheHandler
import com.gijun.mainserver.application.port.`in`.product.CreateProductContainerUseCase
import com.gijun.mainserver.application.port.`in`.product.UpdateProductContainerUseCase
import com.gijun.mainserver.application.port.`in`.product.DeleteProductContainerUseCase
import com.gijun.mainserver.application.port.out.product.productContainer.ProductContainerCommandRepository
import com.gijun.mainserver.application.port.out.product.productContainer.ProductContainerQueryRepository
import com.gijun.mainserver.domain.common.exception.EntityNotFoundException
import com.gijun.mainserver.domain.product.productContainer.model.ProductContainer
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductContainerCommandHandler(
    private val productContainerCommandRepository: ProductContainerCommandRepository,
    private val productContainerQueryRepository: ProductContainerQueryRepository,
    private val cacheHandler: CacheHandler
) : CreateProductContainerUseCase, UpdateProductContainerUseCase, DeleteProductContainerUseCase {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun createProductContainerExecute(command: CreateProductContainerCommand): CreateProductContainerResult {
        val productContainer = ProductContainer(
            id = null,
            hqId = command.hqId,
            containerId = command.containerId,
            containerName = command.containerName
        )

        val savedContainer = productContainerCommandRepository.save(productContainer)

        // Invalidate cache
        invalidateContainerCaches(savedContainer.hqId, savedContainer.containerId)
        logger.debug("Cache invalidated after creating container: ${savedContainer.containerId} at HQ: ${savedContainer.hqId}")

        return CreateProductContainerResult(
            id = savedContainer.id!!,
            hqId = savedContainer.hqId,
            containerId = savedContainer.containerId,
            containerName = savedContainer.containerName
        )
    }

    override fun updateProductContainerExecute(command: UpdateProductContainerCommand): UpdateProductContainerResult {
        require(productContainerQueryRepository.existsById(command.id)) {
            "ProductContainer with id ${command.id} not found"
        }

        val productContainer = ProductContainer(
            id = command.id,
            hqId = command.hqId,
            containerId = command.containerId,
            containerName = command.containerName
        )

        val updatedContainer = productContainerCommandRepository.update(productContainer)

        // Invalidate cache
        invalidateContainerCaches(updatedContainer.hqId, updatedContainer.containerId)
        logger.debug("Cache invalidated after updating container: ${updatedContainer.containerId} at HQ: ${updatedContainer.hqId}")

        return UpdateProductContainerResult(
            id = updatedContainer.id!!,
            hqId = updatedContainer.hqId,
            containerId = updatedContainer.containerId,
            containerName = updatedContainer.containerName
        )
    }

    override fun deleteProductContainerExecute(command: DeleteProductContainerCommand): DeleteProductContainerResult {
        val productContainer = productContainerQueryRepository.findById(command.id)
            ?: throw EntityNotFoundException("ProductContainer", command.id.toString())

        productContainerCommandRepository.delete(command.id)

        // Invalidate cache
        invalidateContainerCaches(productContainer.hqId, productContainer.containerId)
        logger.debug("Cache invalidated after deleting container: ${productContainer.containerId} at HQ: ${productContainer.hqId}")

        return DeleteProductContainerResult(id = command.id)
    }

    private fun invalidateContainerCaches(hqId: Long, containerId: Long) {
        cacheHandler.delete(CacheHandler.Keys.productContainerKey(hqId, containerId))
        cacheHandler.delete(CacheHandler.Keys.productContainerListByHqKey(hqId))
        cacheHandler.deletePattern("product:container:list:hq:${hqId}:*")
    }
}