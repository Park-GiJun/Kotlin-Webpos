package com.gijun.mainserver.infrastructure.adapter.`in`.web.product.mapper

import com.gijun.mainserver.application.dto.command.product.productContainer.CreateProductContainerCommand
import com.gijun.mainserver.application.dto.command.product.productContainer.UpdateProductContainerCommand
import com.gijun.mainserver.application.dto.command.product.productContainer.DeleteProductContainerCommand
import com.gijun.mainserver.application.dto.result.product.productContainer.CreateProductContainerResult
import com.gijun.mainserver.application.dto.result.product.productContainer.UpdateProductContainerResult
import com.gijun.mainserver.application.dto.result.product.productContainer.DeleteProductContainerResult
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.CreateProductContainerRequest
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.CreateProductContainerResponse
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.UpdateProductContainerRequest
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.UpdateProductContainerResponse
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.DeleteProductContainerResponse

object ProductContainerWebMapper {

    fun toCreateCommand(request: CreateProductContainerRequest): CreateProductContainerCommand {
        return CreateProductContainerCommand(
            hqId = request.hqId,
            containerId = request.containerId,
            containerName = request.containerName
        )
    }

    fun toCreateResponse(result: CreateProductContainerResult): CreateProductContainerResponse {
        return CreateProductContainerResponse(
            id = result.id,
            hqId = result.hqId,
            containerId = result.containerId,
            containerName = result.containerName
        )
    }

    fun toUpdateCommand(id: Long, request: UpdateProductContainerRequest): UpdateProductContainerCommand {
        return UpdateProductContainerCommand(
            id = id,
            hqId = request.hqId,
            containerId = request.containerId,
            containerName = request.containerName
        )
    }

    fun toUpdateResponse(result: UpdateProductContainerResult): UpdateProductContainerResponse {
        return UpdateProductContainerResponse(
            id = result.id,
            hqId = result.hqId,
            containerId = result.containerId,
            containerName = result.containerName
        )
    }

    fun toDeleteCommand(id: Long): DeleteProductContainerCommand {
        return DeleteProductContainerCommand(id = id)
    }

    fun toDeleteResponse(result: DeleteProductContainerResult): DeleteProductContainerResponse {
        return DeleteProductContainerResponse(id = result.id)
    }
}