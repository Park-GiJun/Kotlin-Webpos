package com.gijun.mainserver

import com.fasterxml.jackson.databind.ObjectMapper
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.CreateProductRequest
import com.gijun.mainserver.infrastructure.adapter.`in`.web.product.dto.InitialStockRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductStockIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should create product with specific initial stock for one store`() {
        // Given - 특정 매장에 대해 초기 재고를 설정
        // 나머지 매장들은 자동으로 0,0으로 설정됨
        val initialStock = InitialStockRequest(
            storeId = 1L,
            unitQty = BigDecimal("100.00"),
            usageQty = BigDecimal("100.00")
        )

        val request = CreateProductRequest(
            hqId = 1L,
            name = "Test Product",
            price = BigDecimal("10000.00"),
            productCode = "P001",
            supplyAmt = BigDecimal("8000.00"),
            unit = "EA",
            usageUnit = "EA",
            initialStock = initialStock
        )

        // When & Then
        mockMvc.perform(
            post("/main/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
        // 이 테스트는 매장 1에는 100,100 재고가,
        // 나머지 모든 매장에는 0,0 재고가 생성됨을 검증해야 함
    }

    @Test
    fun `should create product with zero stock for all stores`() {
        // Given - initialStock을 제공하지 않으면 모든 매장에 0,0 재고 생성
        val request = CreateProductRequest(
            hqId = 1L,
            name = "Test Product 2",
            price = BigDecimal("20000.00"),
            productCode = "P002",
            supplyAmt = BigDecimal("15000.00"),
            unit = "BOX",
            usageUnit = "BOX",
            initialStock = null
        )

        // When & Then
        mockMvc.perform(
            post("/main/product")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
        // 이 테스트는 해당 HQ의 모든 매장에 0,0 재고가 생성됨을 검증해야 함
    }
}