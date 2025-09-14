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
import com.gijun.mainserver.domain.common.vo.Quantity
import com.gijun.mainserver.domain.product.productStock.model.ProductStock
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class ProductCommandHandler(
    private val productCommandRepository: ProductCommandRepository,
    private val productQueryRepository: ProductQueryRepository,
    private val productStockCommandRepository: ProductStockCommandRepository,
    private val storeQueryRepository: StoreQueryRepository
) : CreateProductUseCase, UpdateProductUseCase, DeleteProductUseCase {

    override fun createProductExecute(command: CreateProductCommand): CreateProductResult {
        val product = ProductApplicationMapper.toDomain(command)
        val savedProduct = productCommandRepository.save(product)

        // 해당 HQ의 모든 매장에 대해 0,0 재고 생성
        val stores = storeQueryRepository.findByHqId(command.hqId)
        stores.forEach { store ->
            val initialQuantity = command.initialStock?.let { stockCommand ->
                if (stockCommand.storeId == store.id) {
                    // 초기 재고가 지정된 매장인 경우 해당 값 사용
                    Pair(stockCommand.unitQty, stockCommand.usageQty)
                } else {
                    // 다른 매장은 0,0으로 초기화
                    Pair(Quantity(BigDecimal.ZERO), Quantity(BigDecimal.ZERO))
                }
            } ?: Pair(Quantity(BigDecimal.ZERO), Quantity(BigDecimal.ZERO))

            val productStock = ProductStock(
                id = null,
                productId = savedProduct.id!!,
                hqId = command.hqId,
                storeId = store.id!!,
                unitQty = initialQuantity.first,
                usageQty = initialQuantity.second
            )
            productStockCommandRepository.save(productStock)
        }

        return ProductApplicationMapper.toCreateResult(savedProduct)
    }

    override fun updateProductExecute(command: UpdateProductCommand): UpdateProductResult {
        require(productQueryRepository.existsById(command.id)) {
            "Product with id ${command.id} not found"
        }

        val product = ProductApplicationMapper.toDomain(command)
        val updatedProduct = productCommandRepository.update(product)
        return ProductApplicationMapper.toUpdateResult(updatedProduct)
    }

    override fun deleteProductExecute(command: DeleteProductCommand): DeleteProductResult {
        require(productQueryRepository.existsById(command.id)) {
            "Product with id ${command.id} not found"
        }

        productCommandRepository.delete(command.id)
        return ProductApplicationMapper.toDeleteResult(command.id)
    }
}