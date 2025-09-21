package com.gijun.mainserver.domain.product.product.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals

class ProductTest {

    @Test
    fun `Product 생성 성공 - 모든 필드가 유효한 경우`() {
        // given
        val hqId = 1L
        val name = "아메리카노"
        val price = BigDecimal("4500")
        val productType = ProductType.RECIPE
        val productCode = "COFFEE-001"
        val supplyAmt = BigDecimal("2000")
        val unit = "컵"
        val usageUnit = "잔"

        // when
        val product = Product(
            id = 1L,
            hqId = hqId,
            name = name,
            price = price,
            productType = productType,
            productCode = productCode,
            supplyAmt = supplyAmt,
            unit = unit,
            usageUnit = usageUnit
        )

        // then
        assertEquals(1L, product.id)
        assertEquals(hqId, product.hqId)
        assertEquals(name, product.name)
        assertEquals(price, product.price)
        assertEquals(productType, product.productType)
        assertEquals(productCode, product.productCode)
        assertEquals(supplyAmt, product.supplyAmt)
        assertEquals(unit, product.unit)
        assertEquals(usageUnit, product.usageUnit)
    }

    @Test
    fun `Product 생성 실패 - name이 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Product(
                id = 1L,
                hqId = 1L,
                name = "",
                price = BigDecimal("4500"),
                productType = ProductType.RECIPE,
                productCode = "COFFEE-001",
                supplyAmt = BigDecimal("2000"),
                unit = "컵",
                usageUnit = "잔"
            )
        }
    }

    @Test
    fun `Product 생성 실패 - price가 음수인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Product(
                id = 1L,
                hqId = 1L,
                name = "아메리카노",
                price = BigDecimal("-100"),
                productType = ProductType.RECIPE,
                productCode = "COFFEE-001",
                supplyAmt = BigDecimal("2000"),
                unit = "컵",
                usageUnit = "잔"
            )
        }
    }

    @Test
    fun `Product 생성 성공 - price가 0인 경우`() {
        // given & when
        val product = Product(
            id = 1L,
            hqId = 1L,
            name = "무료샘플",
            price = BigDecimal.ZERO,
            productType = ProductType.PRODUCT,
            productCode = "SAMPLE-001",
            supplyAmt = BigDecimal("100"),
            unit = "개",
            usageUnit = "개"
        )

        // then
        assertEquals(BigDecimal.ZERO, product.price)
    }

    @Test
    fun `Product 생성 실패 - productCode가 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Product(
                id = 1L,
                hqId = 1L,
                name = "아메리카노",
                price = BigDecimal("4500"),
                productType = ProductType.RECIPE,
                productCode = "   ",
                supplyAmt = BigDecimal("2000"),
                unit = "컵",
                usageUnit = "잔"
            )
        }
    }

    @Test
    fun `Product 생성 실패 - supplyAmt가 음수인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Product(
                id = 1L,
                hqId = 1L,
                name = "아메리카노",
                price = BigDecimal("4500"),
                productType = ProductType.RECIPE,
                productCode = "COFFEE-001",
                supplyAmt = BigDecimal("-100"),
                unit = "컵",
                usageUnit = "잔"
            )
        }
    }

    @Test
    fun `Product 생성 실패 - unit이 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Product(
                id = 1L,
                hqId = 1L,
                name = "아메리카노",
                price = BigDecimal("4500"),
                productType = ProductType.RECIPE,
                productCode = "COFFEE-001",
                supplyAmt = BigDecimal("2000"),
                unit = "",
                usageUnit = "잔"
            )
        }
    }

    @Test
    fun `Product 생성 실패 - usageUnit이 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Product(
                id = 1L,
                hqId = 1L,
                name = "아메리카노",
                price = BigDecimal("4500"),
                productType = ProductType.RECIPE,
                productCode = "COFFEE-001",
                supplyAmt = BigDecimal("2000"),
                unit = "컵",
                usageUnit = ""
            )
        }
    }
}