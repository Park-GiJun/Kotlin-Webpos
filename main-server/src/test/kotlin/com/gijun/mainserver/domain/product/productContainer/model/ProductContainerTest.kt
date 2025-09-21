package com.gijun.mainserver.domain.product.productContainer.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ProductContainerTest {

    @Test
    fun `ProductContainer 생성 성공 - 모든 필드가 유효한 경우`() {
        // given
        val hqId = 1L
        val containerId = 1L
        val containerName = "서울창고"

        // when
        val productContainer = ProductContainer(
            id = 1L,
            hqId = hqId,
            containerId = containerId,
            containerName = containerName
        )

        // then
        assertEquals(1L, productContainer.id)
        assertEquals(hqId, productContainer.hqId)
        assertEquals(containerId, productContainer.containerId)
        assertEquals(containerName, productContainer.containerName)
    }

    @Test
    fun `ProductContainer 생성 실패 - containerName이 공백인 경우`() {
        // given & when & then
        val exception = assertThrows<IllegalArgumentException> {
            ProductContainer(
                id = 1L,
                hqId = 1L,
                containerId = 1L,
                containerName = ""
            )
        }

        assertEquals("Container name cannot be blank", exception.message)
    }

    @Test
    fun `ProductContainer 생성 실패 - containerName이 빈 문자열인 경우`() {
        // given & when & then
        val exception = assertThrows<IllegalArgumentException> {
            ProductContainer(
                id = 1L,
                hqId = 1L,
                containerId = 1L,
                containerName = "   "
            )
        }

        assertEquals("Container name cannot be blank", exception.message)
    }

    @Test
    fun `동일한 값을 가진 ProductContainer 객체는 동등하다`() {
        // given & when
        val container1 = ProductContainer(
            id = 1L,
            hqId = 1L,
            containerId = 1L,
            containerName = "서울창고"
        )
        val container2 = ProductContainer(
            id = 1L,
            hqId = 1L,
            containerId = 1L,
            containerName = "서울창고"
        )

        // then
        assertEquals(container1, container2)
        assertEquals(container1.hashCode(), container2.hashCode())
    }

    @Test
    fun `서로 다른 값을 가진 ProductContainer 객체는 동등하지 않다`() {
        // given & when
        val container1 = ProductContainer(
            id = 1L,
            hqId = 1L,
            containerId = 1L,
            containerName = "서울창고"
        )
        val container2 = ProductContainer(
            id = 2L,
            hqId = 1L,
            containerId = 2L,
            containerName = "부산창고"
        )

        // then
        assertEquals(false, container1 == container2)
    }

    @Test
    fun `컨테이너 이름별 구분 테스트`() {
        // given
        val seoulContainer = ProductContainer(
            id = 1L,
            hqId = 1L,
            containerId = 1L,
            containerName = "서울창고"
        )

        val busanContainer = ProductContainer(
            id = 2L,
            hqId = 1L,
            containerId = 2L,
            containerName = "부산창고"
        )

        val daeguContainer = ProductContainer(
            id = 3L,
            hqId = 1L,
            containerId = 3L,
            containerName = "대구창고"
        )

        // then
        assertEquals("서울창고", seoulContainer.containerName)
        assertEquals("부산창고", busanContainer.containerName)
        assertEquals("대구창고", daeguContainer.containerName)
        assertEquals(1L, seoulContainer.containerId)
        assertEquals(2L, busanContainer.containerId)
        assertEquals(3L, daeguContainer.containerId)
    }
}