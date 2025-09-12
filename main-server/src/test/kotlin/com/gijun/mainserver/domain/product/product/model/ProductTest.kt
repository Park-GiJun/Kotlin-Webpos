package com.gijun.mainserver.domain.product.product.model

import com.gijun.mainserver.domain.common.vo.Money
import com.gijun.mainserver.domain.common.vo.ProductCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("Product 도메인 엔티티 테스트")
class ProductTest {

    @Test
    @DisplayName("유효한 정보로 Product 생성 시 성공한다")
    fun `should create Product with valid information`() {
        // given
        val hqId = 1L
        val name = "아메리카노"
        val price = Money(BigDecimal("4500"))
        val productCode = ProductCode("COFFEE001")
        val supplyAmt = BigDecimal("2000")
        val unit = "잔"
        val usageUnit = "ml"

        // when
        val product = Product(
            id = null,
            hqId = hqId,
            name = name,
            price = price,
            productCode = productCode,
            supplyAmt = supplyAmt,
            unit = unit,
            usageUnit = usageUnit
        )

        // then
        assertThat(product).isNotNull
        assertThat(product.id).isNull()
        assertThat(product.hqId).isEqualTo(hqId)
        assertThat(product.name).isEqualTo(name)
        assertThat(product.price).isEqualTo(price)
        assertThat(product.productCode).isEqualTo(productCode)
        assertThat(product.supplyAmt).isEqualTo(supplyAmt)
        assertThat(product.unit).isEqualTo(unit)
        assertThat(product.usageUnit).isEqualTo(usageUnit)
    }

    @Test
    @DisplayName("Product는 상품코드 없이도 생성할 수 있다")
    fun `should create Product without productCode`() {
        // given & when
        val product = Product(
            id = null,
            hqId = 1L,
            name = "샌드위치",
            price = Money(BigDecimal("6000")),
            productCode = null,
            supplyAmt = BigDecimal("3000"),
            unit = "개",
            usageUnit = "개"
        )

        // then
        assertThat(product.productCode).isNull()
    }

    @Test
    @DisplayName("Product copy 메서드를 통해 가격을 수정할 수 있다")
    fun `should update price using copy method`() {
        // given
        val originalProduct = Product(
            id = 1L,
            hqId = 1L,
            name = "라떼",
            price = Money(BigDecimal("5000")),
            productCode = ProductCode("COFFEE002"),
            supplyAmt = BigDecimal("2500"),
            unit = "잔",
            usageUnit = "ml"
        )

        // when
        val updatedProduct = originalProduct.copy(
            price = Money(BigDecimal("5500"))
        )

        // then
        assertThat(updatedProduct.price.value).isEqualTo(BigDecimal("5500"))
        assertThat(updatedProduct.id).isEqualTo(originalProduct.id)
        assertThat(updatedProduct.name).isEqualTo(originalProduct.name)
        assertThat(updatedProduct.productCode).isEqualTo(originalProduct.productCode)
    }

    @Test
    @DisplayName("서로 다른 단위(판매단위/사용단위)를 가진 Product를 생성할 수 있다")
    fun `should create Product with different units`() {
        // given & when
        val product = Product(
            id = null,
            hqId = 1L,
            name = "시럽",
            price = Money(BigDecimal("15000")),
            productCode = ProductCode("SYRUP001"),
            supplyAmt = BigDecimal("8000"),
            unit = "병",        // 판매 단위
            usageUnit = "ml"    // 사용 단위
        )

        // then
        assertThat(product.unit).isEqualTo("병")
        assertThat(product.usageUnit).isEqualTo("ml")
        assertThat(product.unit).isNotEqualTo(product.usageUnit)
    }

    @Test
    @DisplayName("Product equals와 hashCode는 모든 필드를 비교한다")
    fun `equals and hashCode should compare all fields`() {
        // given
        val price = Money(BigDecimal("4500"))
        val productCode = ProductCode("TEST001")

        val product1 = Product(1L, 1L, "상품", price, productCode, BigDecimal("2000"), "개", "개")
        val product2 = Product(1L, 1L, "상품", price, productCode, BigDecimal("2000"), "개", "개")
        val product3 = Product(2L, 1L, "상품", price, productCode, BigDecimal("2000"), "개", "개")

        // then
        assertThat(product1).isEqualTo(product2)
        assertThat(product1.hashCode()).isEqualTo(product2.hashCode())
        assertThat(product1).isNotEqualTo(product3)
    }
}