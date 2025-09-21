package com.gijun.mainserver.application.handler.query.product

import com.gijun.mainserver.application.dto.query.product.product.GetAllProductQuery
import com.gijun.mainserver.application.dto.query.product.product.GetProductByIdQuery
import com.gijun.mainserver.application.dto.query.product.product.GetProductsByHqIdQuery
import com.gijun.mainserver.application.dto.result.product.product.ProductResult
import com.gijun.mainserver.application.mapper.ProductApplicationMapper
import com.gijun.mainserver.application.port.`in`.product.GetProductUseCase
import com.gijun.mainserver.application.port.out.product.product.ProductQueryRepository
import com.gijun.mainserver.application.handler.cache.CacheHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
@Transactional(readOnly = true)
class ProductQueryHandler(
    private val productQueryRepository: ProductQueryRepository,
    private val cacheHandler: CacheHandler
) : GetProductUseCase {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val cacheTtl = Duration.ofMinutes(5)

    override fun getAllProductExecute(query: GetAllProductQuery): List<ProductResult> {
        val cacheKey = if (query.hqId != null) {
            CacheHandler.Keys.productListByHqKey(query.hqId)
        } else {
            CacheHandler.Keys.allProductsKey()
        }

        // Try cache first
        val cached = cacheHandler.get(cacheKey, Array<ProductResult>::class.java)
        if (cached != null) {
            logger.debug("Cache HIT for all products")
            return cached.toList()
        }

        // Load from database
        val products = if (query.hqId != null) {
            productQueryRepository.findByHqId(query.hqId)
                .map { ProductApplicationMapper.toProductResult(it) }
        } else {
            productQueryRepository.findAll()
                .map { ProductApplicationMapper.toProductResult(it) }
        }

        // Cache the result
        if (products.isNotEmpty()) {
            cacheHandler.set(cacheKey, products.toTypedArray(), cacheTtl)
        }

        return products
    }

    override fun getProductByIdExecute(query: GetProductByIdQuery): ProductResult? {
        val cacheKey = CacheHandler.Keys.productKey(query.id)

        // Try cache first
        val cached = cacheHandler.get(cacheKey, ProductResult::class.java)
        if (cached != null) {
            logger.debug("Cache HIT for product id: ${query.id}")
            return cached
        }

        // Load from database
        val product = productQueryRepository.findById(query.id)
            ?.let { ProductApplicationMapper.toProductResult(it) }

        // Cache if found
        if (product != null) {
            cacheHandler.set(cacheKey, product, cacheTtl)
        }

        return product
    }

    override fun getProductsByHqIdExecute(query: GetProductsByHqIdQuery): List<ProductResult> {
        val cacheKey = CacheHandler.Keys.productListByHqKey(query.hqId)

        // Try cache first
        val cached = cacheHandler.get(cacheKey, Array<ProductResult>::class.java)
        if (cached != null) {
            logger.debug("Cache HIT for products by hqId: ${query.hqId}")
            return cached.toList()
        }

        // Load from database
        val products = productQueryRepository.findByHqId(query.hqId)
            .map { ProductApplicationMapper.toProductResult(it) }

        // Cache the result
        if (products.isNotEmpty()) {
            cacheHandler.set(cacheKey, products.toTypedArray(), cacheTtl)
        }

        return products
    }
}