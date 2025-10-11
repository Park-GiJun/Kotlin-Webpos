package com.gijun.posserver.infrastructure.adapter.out.http.stock

import com.gijun.posserver.application.port.out.stock.StockAdjustmentClient
import com.gijun.posserver.application.port.out.stock.StockAdjustmentRequest
import com.gijun.posserver.application.port.out.stock.StockAdjustmentResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class MainServerStockClient(
    private val restClient: RestClient
) : StockAdjustmentClient {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun adjustStock(request: StockAdjustmentRequest): StockAdjustmentResponse {
        logger.info("Adjusting stock for productId=${request.productId}, storeId=${request.storeId}, qty=${request.usageQty}")

        // Main-server returns ApiResponse<AdjustProductStockResponse>
        val apiResponse = restClient.put()
            .uri("/product-stock/product/{productId}/store/{storeId}/adjust", request.productId, request.storeId)
            .body(mapOf(
                "adjustmentType" to request.adjustmentType,
                "unitQty" to request.unitQty,
                "usageQty" to request.usageQty,
                "reason" to request.reason
            ))
            .retrieve()
            .body(Map::class.java) as? Map<*, *>

        if (apiResponse == null) {
            throw RuntimeException("Failed to adjust stock: null response")
        }

        // Extract data from ApiResponse wrapper
        val success = apiResponse["success"] as? Boolean ?: false
        if (!success) {
            val error = apiResponse["error"] as? Map<*, *>
            val errorMessage = error?.get("message") as? String ?: "Unknown error"
            throw RuntimeException("Failed to adjust stock: $errorMessage")
        }

        val data = apiResponse["data"] as? Map<*, *>
            ?: throw RuntimeException("Failed to adjust stock: no data in response")

        logger.info("Stock adjustment completed for productId=${request.productId}")

        // Map to StockAdjustmentResponse
        return StockAdjustmentResponse(
            productStockId = (data["productStockId"] as? Number)?.toLong() ?: 0L,
            productId = (data["productId"] as? Number)?.toLong() ?: request.productId,
            containerId = (data["containerId"] as? Number)?.toLong() ?: request.storeId,
            unitQtyBefore = java.math.BigDecimal(data["unitQtyBefore"]?.toString() ?: "0"),
            usageQtyBefore = java.math.BigDecimal(data["usageQtyBefore"]?.toString() ?: "0"),
            unitQtyAfter = java.math.BigDecimal(data["unitQtyAfter"]?.toString() ?: "0"),
            usageQtyAfter = java.math.BigDecimal(data["usageQtyAfter"]?.toString() ?: "0")
        )
    }
}
