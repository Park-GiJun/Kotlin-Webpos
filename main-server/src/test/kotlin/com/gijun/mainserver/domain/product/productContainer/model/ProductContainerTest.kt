package com.gijun.mainserver.domain.product.productContainer.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals

class ProductContainerTest {

    @Test
    fun `ProductContainer 생성 성공 - 모든 필드가 유효한 경우`() {
        // given
        val hqId = 1L
        val containerId = 1L
        val containerName = "서울창고"
        val unitQty = BigDecimal("1000")
        val usageQty = BigDecimal("500")

        // when
        val productContainer = ProductContainer(
            id = 1L,
            hqId = hqId,
            containerId = containerId,
            containerName = containerName,
            unitQty = unitQty,
            usageQty = usageQty
        )

        // then
        assertEquals(1L, productContainer.id)
        assertEquals(hqId, productContainer.hqId)
        assertEquals(containerId, productContainer.containerId)
        assertEquals(containerName, productContainer.containerName)
        assertEquals(unitQty, productContainer.unitQty)
        assertEquals(usageQty, productContainer.usageQty)
    }

    @Test
    fun `컨테이너 재고 증가 성공 - 단위재고와 사용재고 모두 증가`() {
        // given
        val productContainer = ProductContainer(
            id = 1L,
            hqId = 1L,
            containerId = 1L,
            containerName = "서울창고",
            unitQty = BigDecimal("1000"),
            usageQty = BigDecimal("500")
        )
        val unitQtyToAdd = BigDecimal("200")
        val usageQtyToAdd = BigDecimal("100")

        // when
        val increasedContainer = productContainer.increase(unitQtyToAdd, usageQtyToAdd)

        // then
        assertEquals(BigDecimal("1200"), increasedContainer.unitQty)
        assertEquals(BigDecimal("600"), increasedContainer.usageQty)
        assertEquals(productContainer.id, increasedContainer.id)
        assertEquals(productContainer.hqId, increasedContainer.hqId)
        assertEquals(productContainer.containerId, increasedContainer.containerId)
        assertEquals(productContainer.containerName, increasedContainer.containerName)
    }

    @Test
    fun `컨테이너 재고 증가 성공 - 0 증가`() {
        // given
        val originalContainer = ProductContainer(
            id = 1L,
            hqId = 1L,
            containerId = 1L,
            containerName = "서울창고",
            unitQty = BigDecimal("1000"),
            usageQty = BigDecimal("500")
        )

        // when
        val increasedContainer = originalContainer.increase(BigDecimal.ZERO, BigDecimal.ZERO)

        // then
        assertEquals(originalContainer.unitQty, increasedContainer.unitQty)
        assertEquals(originalContainer.usageQty, increasedContainer.usageQty)
    }

    @Test
    fun `컨테이너 재고 감소 성공 - 단위재고와 사용재고 모두 감소`() {
        // given
        val productContainer = ProductContainer(
            id = 1L,
            hqId = 1L,
            containerId = 1L,
            containerName = "서울창고",
            unitQty = BigDecimal("1000"),
            usageQty = BigDecimal("500")
        )
        val unitQtyToSubtract = BigDecimal("300")
        val usageQtyToSubtract = BigDecimal("200")

        // when
        val decreasedContainer = productContainer.decrease(unitQtyToSubtract, usageQtyToSubtract)

        // then
        assertEquals(BigDecimal("700"), decreasedContainer.unitQty)
        assertEquals(BigDecimal("300"), decreasedContainer.usageQty)
        assertEquals(productContainer.id, decreasedContainer.id)
        assertEquals(productContainer.hqId, decreasedContainer.hqId)
        assertEquals(productContainer.containerId, decreasedContainer.containerId)
        assertEquals(productContainer.containerName, decreasedContainer.containerName)
    }

    @Test
    fun `컨테이너 재고 감소 성공 - 재고가 정확히 0이 되는 경우`() {
        // given
        val productContainer = ProductContainer(
            id = 1L,
            hqId = 1L,
            containerId = 1L,
            containerName = "서울창고",
            unitQty = BigDecimal("1000"),
            usageQty = BigDecimal("500")
        )

        // when
        val decreasedContainer = productContainer.decrease(BigDecimal("1000"), BigDecimal("500"))

        // then
        assertEquals(BigDecimal.ZERO, decreasedContainer.unitQty)
        assertEquals(BigDecimal.ZERO, decreasedContainer.usageQty)
    }

    @Test
    fun `컨테이너 재고 감소 실패 - 단위재고가 부족한 경우`() {
        // given
        val productContainer = ProductContainer(
            id = 1L,
            hqId = 1L,
            containerId = 1L,
            containerName = "서울창고",
            unitQty = BigDecimal("1000"),
            usageQty = BigDecimal("500")
        )
        val unitQtyToSubtract = BigDecimal("1500")
        val usageQtyToSubtract = BigDecimal("100")

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            productContainer.decrease(unitQtyToSubtract, usageQtyToSubtract)
        }

        assertEquals("컨테이너 단위 재고가 부족합니다. 현재: 1000, 요청: 1500", exception.message)
    }

    @Test
    fun `컨테이너 재고 감소 실패 - 사용재고가 부족한 경우`() {
        // given
        val productContainer = ProductContainer(
            id = 1L,
            hqId = 1L,
            containerId = 1L,
            containerName = "서울창고",
            unitQty = BigDecimal("1000"),
            usageQty = BigDecimal("500")
        )
        val unitQtyToSubtract = BigDecimal("100")
        val usageQtyToSubtract = BigDecimal("800")

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            productContainer.decrease(unitQtyToSubtract, usageQtyToSubtract)
        }

        assertEquals("컨테이너 사용 재고가 부족합니다. 현재: 500, 요청: 800", exception.message)
    }

    @Test
    fun `컨테이너 재고 감소 실패 - 단위재고와 사용재고 모두 부족한 경우`() {
        // given
        val productContainer = ProductContainer(
            id = 1L,
            hqId = 1L,
            containerId = 1L,
            containerName = "부산창고",
            unitQty = BigDecimal("100"),
            usageQty = BigDecimal("50")
        )
        val unitQtyToSubtract = BigDecimal("200")
        val usageQtyToSubtract = BigDecimal("100")

        // when & then
        val exception = assertThrows<IllegalArgumentException> {
            productContainer.decrease(unitQtyToSubtract, usageQtyToSubtract)
        }

        assertEquals("컨테이너 단위 재고가 부족합니다. 현재: 100, 요청: 200", exception.message)
    }

    @Test
    fun `컨테이너 이름별 재고 관리 테스트`() {
        // given
        val seoulContainer = ProductContainer(
            id = 1L,
            hqId = 1L,
            containerId = 1L,
            containerName = "서울창고",
            unitQty = BigDecimal("1000"),
            usageQty = BigDecimal("500")
        )

        val busanContainer = ProductContainer(
            id = 2L,
            hqId = 1L,
            containerId = 2L,
            containerName = "부산창고",
            unitQty = BigDecimal("800"),
            usageQty = BigDecimal("400")
        )

        // when
        val updatedSeoul = seoulContainer.decrease(BigDecimal("200"), BigDecimal("100"))
        val updatedBusan = busanContainer.increase(BigDecimal("200"), BigDecimal("100"))

        // then
        assertEquals("서울창고", updatedSeoul.containerName)
        assertEquals(BigDecimal("800"), updatedSeoul.unitQty)
        assertEquals(BigDecimal("400"), updatedSeoul.usageQty)

        assertEquals("부산창고", updatedBusan.containerName)
        assertEquals(BigDecimal("1000"), updatedBusan.unitQty)
        assertEquals(BigDecimal("500"), updatedBusan.usageQty)
    }

    @Test
    fun `연속 재고 조정 테스트`() {
        // given
        val initialContainer = ProductContainer(
            id = 1L,
            hqId = 1L,
            containerId = 1L,
            containerName = "대구창고",
            unitQty = BigDecimal("1000"),
            usageQty = BigDecimal("500")
        )

        // when
        val step1 = initialContainer.increase(BigDecimal("500"), BigDecimal("250"))
        val step2 = step1.decrease(BigDecimal("300"), BigDecimal("150"))
        val step3 = step2.increase(BigDecimal("100"), BigDecimal("50"))

        // then
        assertEquals(BigDecimal("1300"), step3.unitQty)
        assertEquals(BigDecimal("650"), step3.usageQty)
        assertEquals("대구창고", step3.containerName)
    }
}