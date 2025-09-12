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
import com.gijun.mainserver.application.port.out.product.product.ProductCommandRepository
import com.gijun.mainserver.application.port.out.product.product.ProductQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductCommandHandler(
    private val productCommandRepository: ProductCommandRepository,
    private val productQueryRepository: ProductQueryRepository
) : CreateProductUseCase, UpdateProductUseCase, DeleteProductUseCase {

    override fun createProductExecute(command: CreateProductCommand): CreateProductResult {
        val product = ProductApplicationMapper.toDomain(command)
        val savedProduct = productCommandRepository.save(product)
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