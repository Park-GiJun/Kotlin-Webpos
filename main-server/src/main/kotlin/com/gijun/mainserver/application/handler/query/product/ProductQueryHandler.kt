package com.gijun.mainserver.application.handler.query.product

import com.gijun.mainserver.application.dto.query.product.product.GetAllProductQuery
import com.gijun.mainserver.application.dto.query.product.product.GetProductByIdQuery
import com.gijun.mainserver.application.dto.query.product.product.GetProductsByHqIdQuery
import com.gijun.mainserver.application.dto.result.product.product.ProductResult
import com.gijun.mainserver.application.mapper.ProductApplicationMapper
import com.gijun.mainserver.application.port.`in`.product.GetProductUseCase
import com.gijun.mainserver.application.port.out.product.product.ProductQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductQueryHandler(
    private val productQueryRepository: ProductQueryRepository
) : GetProductUseCase {

    override fun getAllProductExecute(query: GetAllProductQuery): List<ProductResult> {
        return if (query.hqId != null) {
            productQueryRepository.findByHqId(query.hqId)
                .map { ProductApplicationMapper.toProductResult(it) }
        } else {
            productQueryRepository.findAll()
                .map { ProductApplicationMapper.toProductResult(it) }
        }
    }

    override fun getProductByIdExecute(query: GetProductByIdQuery): ProductResult? {
        return productQueryRepository.findById(query.id)
            ?.let { ProductApplicationMapper.toProductResult(it) }
    }

    override fun getProductsByHqIdExecute(query: GetProductsByHqIdQuery): List<ProductResult> {
        return productQueryRepository.findByHqId(query.hqId)
            .map { ProductApplicationMapper.toProductResult(it) }
    }
}