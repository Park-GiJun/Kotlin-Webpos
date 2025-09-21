package com.gijun.mainserver.infrastructure.adapter.`in`.web.product

import com.gijun.mainserver.application.port.`in`.product.CreateProductContainerUseCase
import com.gijun.mainserver.application.port.`in`.product.UpdateProductContainerUseCase
import com.gijun.mainserver.application.port.`in`.product.DeleteProductContainerUseCase
import com.gijun.mainserver.infrastructure.adapter.`in`.web.common.ApiResponse
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.CreateProductContainerRequest
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.CreateProductContainerResponse
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.UpdateProductContainerRequest
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.UpdateProductContainerResponse
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.DeleteProductContainerResponse
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.mapper.ProductContainerWebMapper
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/main/product/container")
class ProductContainerWebAdapter(
    private val createProductContainerUseCase: CreateProductContainerUseCase,
    private val updateProductContainerUseCase: UpdateProductContainerUseCase,
    private val deleteProductContainerUseCase: DeleteProductContainerUseCase
) {

    @PostMapping
    fun createProductContainer(
        @RequestBody request: CreateProductContainerRequest
    ): ApiResponse<CreateProductContainerResponse> {
        val command = ProductContainerWebMapper.toCreateCommand(request)
        val result = createProductContainerUseCase.createProductContainerExecute(command)
        val response = ProductContainerWebMapper.toCreateResponse(result)

        return ApiResponse.success(response)
    }

    @PutMapping("/{id}")
    fun updateProductContainer(
        @PathVariable id: Long,
        @RequestBody request: UpdateProductContainerRequest
    ): ApiResponse<UpdateProductContainerResponse> {
        val command = ProductContainerWebMapper.toUpdateCommand(id, request)
        val result = updateProductContainerUseCase.updateProductContainerExecute(command)
        val response = ProductContainerWebMapper.toUpdateResponse(result)

        return ApiResponse.success(response)
    }

    @DeleteMapping("/{id}")
    fun deleteProductContainer(
        @PathVariable id: Long
    ): ApiResponse<DeleteProductContainerResponse> {
        val command = ProductContainerWebMapper.toDeleteCommand(id)
        val result = deleteProductContainerUseCase.deleteProductContainerExecute(command)
        val response = ProductContainerWebMapper.toDeleteResponse(result)

        return ApiResponse.success(response)
    }
}