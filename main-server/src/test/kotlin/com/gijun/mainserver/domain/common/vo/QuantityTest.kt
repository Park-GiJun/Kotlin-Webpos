package com.gijun.mainserver.domain.common.vo

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("Quantity 값 객체 테스트")
class QuantityTest {

    @Test
    @DisplayName("0 이상의 수량으로 Quantity 생성 시 성공한다")
    fun `should create Quantity with non-negative value`() {
        // when
        val zeroQuantity = Quantity(BigDecimal.ZERO)
        val integerQuantity = Quantity(BigDecimal("100"))
        val decimalQuantity = Quantity(BigDecimal("10.5"))

        // then
        assertThat(zeroQuantity.value).isEqualTo(BigDecimal.ZERO)
        assertThat(integerQuantity.value).isEqualTo(BigDecimal("100"))
        assertThat(decimalQuantity.value).isEqualTo(BigDecimal("10.5"))
    }

    @Test
    @DisplayName("음수 수량으로 Quantity 생성 시 예외가 발생한다")
    fun `should throw exception when creating Quantity with negative value`() {
        // when & then
        assertThatThrownBy { Quantity(BigDecimal("-1")) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("수량은 0 이상이어야 합니다")

        assertThatThrownBy { Quantity(BigDecimal("-0.1")) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("수량은 0 이상이어야 합니다")
    }

    @Test
    @DisplayName("동일한 수량 값을 가진 Quantity 객체는 동등하다")
    fun `Quantity objects with same value should be equal`() {
        // given & when
        val quantity1 = Quantity(BigDecimal("100"))
        val quantity2 = Quantity(BigDecimal("100"))
        val quantity3 = Quantity(BigDecimal("100.00"))

        // then
        assertThat(quantity1).isEqualTo(quantity2)
        assertThat(quantity1).isEqualTo(quantity3)
        assertThat(quantity1.hashCode()).isEqualTo(quantity2.hashCode())
    }

    @Test
    @DisplayName("서로 다른 수량 값을 가진 Quantity 객체는 동등하지 않다")
    fun `Quantity objects with different values should not be equal`() {
        // given & when
        val quantity1 = Quantity(BigDecimal("100"))
        val quantity2 = Quantity(BigDecimal("100.1"))

        // then
        assertThat(quantity1).isNotEqualTo(quantity2)
    }

    @Test
    @DisplayName("소수점을 포함한 수량을 정확히 표현할 수 있다")
    fun `should accurately represent decimal quantities`() {
        // given & when
        val quantity1 = Quantity(BigDecimal("0.1"))
        val quantity2 = Quantity(BigDecimal("0.333"))
        val quantity3 = Quantity(BigDecimal("99.999"))

        // then
        assertThat(quantity1.value).isEqualTo(BigDecimal("0.1"))
        assertThat(quantity2.value).isEqualTo(BigDecimal("0.333"))
        assertThat(quantity3.value).isEqualTo(BigDecimal("99.999"))
    }
}