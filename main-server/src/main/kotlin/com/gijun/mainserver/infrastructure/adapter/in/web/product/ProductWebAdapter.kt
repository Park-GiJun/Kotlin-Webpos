package com.gijun.mainserver.infrastructure.adapter.`in`.web.product

import com.gijun.mainserver.application.dto.command.product.DeleteProductCommand
import com.gijun.mainserver.application.dto.query.product.GetAllProductQuery
import com.gijun.mainserver.application.dto.query.product.GetProductByIdQuery
import com.gijun.mainserver.application.dto.query.product.GetProductsByHqIdQuery
import com.gijun.mainserver.application.port.`in`.product.CreateProductUseCase
import com.gijun.mainserver.application.port.`in`.product.DeleteProductUseCase
import com.gijun.mainserver.application.port.`in`.product.GetProductUseCase
import com.gijun.mainserver.application.port.`in`.product.UpdateProductUseCase
import com.gijun.mainserver.infrastructure.adapter.`in`.web.common.ApiResponse
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.*
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.mapper.ProductWebMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/main/product")
class ProductWebAdapter(
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val getProductUseCase: GetProductUseCase
) {

    @PostMapping
    fun createProduct(@RequestBody request: CreateProductRequest): ResponseEntity<ApiResponse<CreateProductResponse>> {
        return request
            .let { ProductWebMapper.toCommand(it) }
            .let { createProductUseCase.createProductExecute(it) }
            .let { ProductWebMapper.toResponse(it) }
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody request: UpdateProductRequest
    ): ResponseEntity<ApiResponse<UpdateProductResponse>> {
        return ProductWebMapper.toCommand(id, request)
            .let { updateProductUseCase.updateProductExecute(it) }
            .let { ProductWebMapper.toResponse(it) }
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.ok(it) }
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        DeleteProductCommand(id)
            .let { deleteProductUseCase.deleteProductExecute(it) }
        
        return ApiResponse.success(Unit)
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping
    fun getAllProducts(@RequestParam(required = false) hqId: Long?): ResponseEntity<ApiResponse<List<ProductResponse>>> {
        return GetAllProductQuery(hqId)
            .let { getProductUseCase.getAllProductExecute(it) }
            .map { ProductWebMapper.toResponse(it) }
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): ResponseEntity<ApiResponse<ProductResponse?>> {
        return GetProductByIdQuery(id)
            .let { getProductUseCase.getProductByIdExecute(it) }
            ?.let { ProductWebMapper.toResponse(it) }
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/hq/{hqId}")
    fun getProductsByHqId(@PathVariable hqId: Long): ResponseEntity<ApiResponse<List<ProductResponse>>> {
        return GetProductsByHqIdQuery(hqId)
            .let { getProductUseCase.getProductsByHqIdExecute(it) }
            .map { ProductWebMapper.toResponse(it) }
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.ok(it) }
    }
}