package com.gijun.mainserver.application.handler.command.product

import com.gijun.mainserver.application.dto.command.product.product.CreateProductCommand
import com.gijun.mainserver.application.dto.command.product.product.DeleteProductCommand
import com.gijun.mainserver.application.dto.command.product.product.UpdateProductCommand
import com.gijun.mainserver.application.dto.result.product.product.CreateProductResult
import com.gijun.mainserver.application.dto.result.product.product.DeleteProductResult
import com.gijun.mainserver.application.dto.result.product.product.UpdateProductResult
import com.gijun.mainserver.application.mapper.ProductApplicationMapper
import com.gijun.mainserver.application.port.`in`.product.CreateProductUseCase
import com.gijun.mainserver.application.port.`in`.product.DeleteProductUseCase
import com.gijun.mainserver.application.port.`in`.product.UpdateProductUseCase
import com.gijun.mainserver.application.port.out.organization.store.StoreQueryRepository
import com.gijun.mainserver.application.port.out.product.product.ProductCommandRepository
import com.gijun.mainserver.application.port.out.product.product.ProductQueryRepository
import com.gijun.mainserver.application.port.out.product.productStock.ProductStockCommandRepository
import com.gijun.mainserver.domain.product.productStock.model.ProductStock
import com.gijun.mainserver.application.handler.cache.CacheHandler
import com.gijun.mainserver.domain.common.exception.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class ProductCommandHandler(
    private val productCommandRepository: ProductCommandRepository,
    private val productQueryRepository: ProductQueryRepository,
    private val productStockCommandRepository: ProductStockCommandRepository,
    private val storeQueryRepository: StoreQueryRepository,
    private val cacheHandler: CacheHandler
) : CreateProductUseCase, UpdateProductUseCase, DeleteProductUseCase {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun createProductExecute(command: CreateProductCommand): CreateProductResult {
        val product = ProductApplicationMapper.toDomain(command)
        val savedProduct = productCommandRepository.save(product)

        // 초기 재고 생성 (Container 기반)
        command.initialStock?.let { stockCommand ->
            val productStock = ProductStock(
                id = null,
                productId = savedProduct.id!!,
                containerId = stockCommand.containerId,
                unitQty = stockCommand.unitQty,
                usageQty = stockCommand.usageQty
            )
            productStockCommandRepository.save(productStock)
        }

        invalidateProductCaches(command.hqId)
        logger.debug("Cache invalidated after creating product: ${savedProduct.id}")

        return ProductApplicationMapper.toCreateResult(savedProduct)
    }

    override fun updateProductExecute(command: UpdateProductCommand): UpdateProductResult {
        if (!productQueryRepository.existsById(command.id)) {
            throw EntityNotFoundException("Product", command.id.toString())
        }

        val product = ProductApplicationMapper.toDomain(command)
        val updatedProduct = productCommandRepository.update(product)

        cacheHandler.delete(CacheHandler.Keys.productKey(command.id))
        invalidateProductCaches(product.hqId)
        logger.debug("Cache invalidated after updating product: ${command.id}")

        return ProductApplicationMapper.toUpdateResult(updatedProduct)
    }

    override fun deleteProductExecute(command: DeleteProductCommand): DeleteProductResult {
        if (!productQueryRepository.existsById(command.id)) {
            throw EntityNotFoundException("Product", command.id.toString())
        }

        val product = productQueryRepository.findById(command.id)

        productCommandRepository.delete(command.id)

        cacheHandler.delete(CacheHandler.Keys.productKey(command.id))
        product?.let { invalidateProductCaches(it.hqId) }
        logger.debug("Cache invalidated after deleting product: ${command.id}")

        return ProductApplicationMapper.toDeleteResult(command.id)
    }

    private fun invalidateProductCaches(hqId: Long) {
        cacheHandler.deletePattern("product:list:*")
        cacheHandler.delete(CacheHandler.Keys.productListByHqKey(hqId))
        cacheHandler.delete(CacheHandler.Keys.allProductsKey())
    }

    private fun invalidateProductCaches(hqId: Long, storeId: Long) {
        cacheHandler.deletePattern("product:list:*")
        cacheHandler.delete(CacheHandler.Keys.productListByHqKey(hqId))
        cacheHandler.delete(CacheHandler.Keys.productListByStoreKey(storeId))
        cacheHandler.delete(CacheHandler.Keys.allProductsKey())
    }
}