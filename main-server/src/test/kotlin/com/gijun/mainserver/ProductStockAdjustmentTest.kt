package com.gijun.mainserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.AdjustProductStockRequest
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.CreateProductRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductStockAdjustmentTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should increase product stock`() {
        // Given - 먼저 상품을 생성 (자동으로 재고 0,0 생성됨)
        val createProductRequest = CreateProductRequest(
            hqId = 1L,
            name = "Test Product for Stock Adjustment",
            price = BigDecimal("10000.00"),
            productCode = "P999",
            supplyAmt = BigDecimal("8000.00"),
            unit = "EA",
            usageUnit = "EA",
            initialStock = null
        )

        val productResult = mockMvc.perform(
            post("/main/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createProductRequest))
        )
            .andExpect(status().isOk)
            .andReturn()

        val productId = 1L // 실제로는 response에서 가져와야 함
        val storeId = 1L

        // When - 재고 증가
        val increaseRequest = AdjustProductStockRequest(
            adjustmentType = "INCREASE",
            unitQty = BigDecimal("50.00"),
            usageQty = BigDecimal("50.00"),
            reason = "입고"
        )

        // Then
        mockMvc.perform(
            put("/main/product-stock/product/$productId/store/$storeId/adjust")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(increaseRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.unitQtyBefore").value(0))
            .andExpect(jsonPath("$.usageQtyBefore").value(0))
            .andExpect(jsonPath("$.unitQtyAfter").value(50))
            .andExpect(jsonPath("$.usageQtyAfter").value(50))
    }

    @Test
    fun `should decrease product stock`() {
        // Given - 재고가 있는 상품 준비
        val createProductRequest = CreateProductRequest(
            hqId = 1L,
            name = "Test Product for Decrease",
            price = BigDecimal("10000.00"),
            productCode = "P998",
            supplyAmt = BigDecimal("8000.00"),
            unit = "EA",
            usageUnit = "EA",
            initialStock = null
        )

        mockMvc.perform(
            post("/main/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createProductRequest))
        )
            .andExpect(status().isOk)

        val productId = 1L
        val storeId = 1L

        // 먼저 재고를 증가시킴
        val increaseRequest = AdjustProductStockRequest(
            adjustmentType = "INCREASE",
            unitQty = BigDecimal("100.00"),
            usageQty = BigDecimal("100.00"),
            reason = "초기 입고"
        )

        mockMvc.perform(
            put("/main/product-stock/product/$productId/store/$storeId/adjust")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(increaseRequest))
        )
            .andExpect(status().isOk)

        // When - 재고 감소
        val decreaseRequest = AdjustProductStockRequest(
            adjustmentType = "DECREASE",
            unitQty = BigDecimal("30.00"),
            usageQty = BigDecimal("30.00"),
            reason = "판매"
        )

        // Then
        mockMvc.perform(
            put("/main/product-stock/product/$productId/store/$storeId/adjust")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(decreaseRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.unitQtyBefore").value(100))
            .andExpect(jsonPath("$.usageQtyBefore").value(100))
            .andExpect(jsonPath("$.unitQtyAfter").value(70))
            .andExpect(jsonPath("$.usageQtyAfter").value(70))
    }

    @Test
    fun `should fail when decreasing stock below zero`() {
        // Given - 재고가 부족한 상황
        val createProductRequest = CreateProductRequest(
            hqId = 1L,
            name = "Test Product for Insufficient Stock",
            price = BigDecimal("10000.00"),
            productCode = "P997",
            supplyAmt = BigDecimal("8000.00"),
            unit = "EA",
            usageUnit = "EA",
            initialStock = null
        )

        mockMvc.perform(
            post("/main/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createProductRequest))
        )
            .andExpect(status().isOk)

        val productId = 1L
        val storeId = 1L

        // When - 재고를 음수로 만들려고 시도
        val decreaseRequest = AdjustProductStockRequest(
            adjustmentType = "DECREASE",
            unitQty = BigDecimal("10.00"),
            usageQty = BigDecimal("10.00"),
            reason = "판매"
        )

        // Then - 실패해야 함
        mockMvc.perform(
            put("/main/product-stock/product/$productId/store/$storeId/adjust")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(decreaseRequest))
        )
            .andExpect(status().isBadRequest)
    }
}