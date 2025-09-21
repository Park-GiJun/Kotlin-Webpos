package com.gijun.mainserver.domain.product.productStock.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals

class ProductStockTest {

    @Test
    fun `ProductStock 생성 성공 - 모든 필드가 유효한 경우`() {
        // given
        val productId = 1L
        val containerId = 1L
        val unitQty = BigDecimal("100")
        val usageQty = BigDecimal("50")

        // when
        val productStock = ProductStock(
            id = 1L,
            productId = productId,
            containerId = containerId,
            unitQty = unitQty,
            usageQty = usageQty
        )

        // then
        assertEquals(1L, productStock.id)
        assertEquals(productId, productStock.productId)
        assertEquals(containerId, productStock.containerId)
        assertEquals(unitQty, productStock.unitQty)
        assertEquals(usageQty, productStock.usageQty)
    }

    @Test
    fun `재고 증가 성공 - 단위재고와 사용재고 모두 증가`() {
        // given
        val productStock = ProductStock(
            id = 1L,
            productId = 1L,
            containerId = 1L,
            unitQty = BigDecimal("100"),
            usageQty = BigDecimal("50")
        )
        val unitQtyToAdd = BigDecimal("20")
        val usageQtyToAdd = BigDecimal("10")

        // when
        val increasedStock = productStock.increase(unitQtyToAdd, usageQtyToAdd)

        // then
        assertEquals(BigDecimal("120"), increasedStock.unitQty)
        assertEquals(BigDecimal("60"), increasedStock.usageQty)
        assertEquals(productStock.id, increasedStock.id)
        assertEquals(productStock.productId, increasedStock.productId)
        assertEquals(productStock.containerId, increasedStock.containerId)
    }

    @Test
    fun `재고 증가 성공 - 0 증가`() {
        // given
        val originalStock = ProductStock(
            id = 1L,
            productId = 1L,
            containerId = 1L,
            unitQty = BigDecimal("100"),
            usageQty = BigDecimal("50")
        )

        // when
        val increasedStock = originalStock.increase(BigDecimal.ZERO, BigDecimal.ZERO)

        // then
        assertEquals(originalStock.unitQty, increasedStock.unitQty)
        assertEquals(originalStock.usageQty, increasedStock.usageQty)
    }

    @Test
    fun `재고 감소 성공 - 단위재고와 사용재고 모두 감소`() {
        // given
        val productStock = ProductStock(
            id = 1L,
            productId = 1L,
            containerId = 1L,
            unitQty = BigDecimal("100"),
            usageQty = BigDecimal("50")
        )
        val unitQtyToSubtract = BigDecimal("30")
        val usageQtyToSubtract = BigDecimal("20")

        // when
        val decreasedStock = productStock.decrease(unitQtyToSubtract, usageQtyToSubtract)

        // then
        assertEquals(BigDecimal("70"), decreasedStock.unitQty)
        assertEquals(BigDecimal("30"), decreasedStock.usageQty)
        assertEquals(productStock.id, decreasedStock.id)
        assertEquals(productStock.productId, decreasedStock.productId)
        assertEquals(productStock.containerId, decreasedStock.containerId)
    }

    @Test
    fun `재고 감소 성공 - 재고가 정확히 0이 되는 경우`() {
        // given
        val productStock = ProductStock(
            id = 1L,
            productId = 1L,
            containerId = 1L,
            unitQty = BigDecimal("100"),
            usageQty = BigDecimal("50")
        )

        // when
        val decreasedStock = productStock.decrease(BigDecimal("100"), BigDecimal("50"))

        // then
        assertEquals(BigDecimal.ZERO, decreasedStock.unitQty)
        assertEquals(BigDecimal.ZERO, decreasedStock.usageQty)
    }

    @Test
    fun `재고 감소 실패 - 단위재고가 부족한 경우`() {
        // given
        val productStock = ProductStock(
            id = 1L,
            productId = 1L,
            containerId = 1L,
            unitQty = BigDecimal("100"),
            usageQty = BigDecimal("50")
        )
        val unitQtyToSubtract = BigDecimal("150")
        val usageQtyToSubtract = BigDecimal("10")

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            productStock.decrease(unitQtyToSubtract, usageQtyToSubtract)
        }

        assertEquals("단위 재고가 부족합니다. 현재: 100, 요청: 150", exception.message)
    }

    @Test
    fun `재고 감소 실패 - 사용재고가 부족한 경우`() {
        // given
        val productStock = ProductStock(
            id = 1L,
            productId = 1L,
            containerId = 1L,
            unitQty = BigDecimal("100"),
            usageQty = BigDecimal("50")
        )
        val unitQtyToSubtract = BigDecimal("10")
        val usageQtyToSubtract = BigDecimal("100")

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            productStock.decrease(unitQtyToSubtract, usageQtyToSubtract)
        }

        assertEquals("사용 재고가 부족합니다. 현재: 50, 요청: 100", exception.message)
    }

    @Test
    fun `재고 감소 실패 - 단위재고와 사용재고 모두 부족한 경우`() {
        // given
        val productStock = ProductStock(
            id = 1L,
            productId = 1L,
            containerId = 1L,
            unitQty = BigDecimal("10"),
            usageQty = BigDecimal("5")
        )
        val unitQtyToSubtract = BigDecimal("20")
        val usageQtyToSubtract = BigDecimal("10")

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            productStock.decrease(unitQtyToSubtract, usageQtyToSubtract)
        }

        assertEquals("단위 재고가 부족합니다. 현재: 10, 요청: 20", exception.message)
    }

    @Test
    fun `연속 재고 조정 테스트`() {
        // given
        val initialStock = ProductStock(
            id = 1L,
            productId = 1L,
            containerId = 1L,
            unitQty = BigDecimal("100"),
            usageQty = BigDecimal("50")
        )

        // when
        val step1 = initialStock.increase(BigDecimal("50"), BigDecimal("25"))
        val step2 = step1.decrease(BigDecimal("30"), BigDecimal("15"))
        val step3 = step2.increase(BigDecimal("10"), BigDecimal("5"))

        // then
        assertEquals(BigDecimal("130"), step3.unitQty)
        assertEquals(BigDecimal("65"), step3.usageQty)
    }
}