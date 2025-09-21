package com.gijun.mainserver.infrastructure.adapter.`in`.web.product

import com.gijun.mainserver.application.port.`in`.product.AdjustProductContainerUseCase
import com.gijun.mainserver.infrastructure.adapter.`in`.web.common.ApiResponse
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.AdjustProductContainerRequest
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.AdjustProductContainerResponse
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.mapper.ProductContainerWebMapper
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/main/product/container")
class ProductContainerWebAdapter(
    private val adjustProductContainerUseCase: AdjustProductContainerUseCase
) {

    @PostMapping("/adjust")
    fun adjustProductContainer(
        @RequestBody request: AdjustProductContainerRequest
    ): ApiResponse<AdjustProductContainerResponse> {
        val command = ProductContainerWebMapper.toCommand(request)
        val result = adjustProductContainerUseCase.adjustProductContainerExecute(command)
        val response = ProductContainerWebMapper.toResponse(result)

        return ApiResponse.success(response)
    }
}