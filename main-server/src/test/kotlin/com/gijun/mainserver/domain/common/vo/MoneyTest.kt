package com.gijun.mainserver.domain.common.vo

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("Money 값 객체 테스트")
class MoneyTest {

    @Test
    @DisplayName("0 이상의 금액으로 Money 생성 시 성공한다")
    fun `should create Money with non-negative amount`() {
        // when
        val zeroMoney = Money(BigDecimal.ZERO)
        val positiveMoney = Money(BigDecimal("1000"))
        val decimalMoney = Money(BigDecimal("1234.56"))

        // then
        assertThat(zeroMoney.value).isEqualTo(BigDecimal.ZERO)
        assertThat(positiveMoney.value).isEqualTo(BigDecimal("1000"))
        assertThat(decimalMoney.value).isEqualTo(BigDecimal("1234.56"))
    }

    @Test
    @DisplayName("음수 금액으로 Money 생성 시 예외가 발생한다")
    fun `should throw exception when creating Money with negative amount`() {
        // when & then
        assertThatThrownBy { Money(BigDecimal("-1")) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("금액은 0 이상이어야 합니다")

        assertThatThrownBy { Money(BigDecimal("-0.01")) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("금액은 0 이상이어야 합니다")
    }

    @Test
    @DisplayName("Money 더하기 연산이 정상적으로 동작한다")
    fun `plus operator should work correctly`() {
        // given
        val money1 = Money(BigDecimal("1000"))
        val money2 = Money(BigDecimal("500"))

        // when
        val result = money1 + money2

        // then
        assertThat(result.value).isEqualTo(BigDecimal("1500"))
    }

    @Test
    @DisplayName("Money 빼기 연산이 정상적으로 동작한다")
    fun `minus operator should work correctly`() {
        // given
        val money1 = Money(BigDecimal("1000"))
        val money2 = Money(BigDecimal("300"))

        // when
        val result = money1 - money2

        // then
        assertThat(result.value).isEqualTo(BigDecimal("700"))
    }

    @Test
    @DisplayName("Money 빼기 연산 결과가 음수일 경우 예외가 발생한다")
    fun `minus operator should throw exception when result is negative`() {
        // given
        val money1 = Money(BigDecimal("100"))
        val money2 = Money(BigDecimal("200"))

        // when & then
        assertThatThrownBy { money1 - money2 }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("금액은 0 이상이어야 합니다")
    }

    @Test
    @DisplayName("동일한 금액을 가진 Money 객체는 동등하다")
    fun `Money objects with same value should be equal`() {
        // given & when
        val money1 = Money(BigDecimal("1000"))
        val money2 = Money(BigDecimal("1000"))
        val money3 = Money(BigDecimal("1000.00"))

        // then
        assertThat(money1).isEqualTo(money2)
        assertThat(money1).isEqualTo(money3)
        assertThat(money1.hashCode()).isEqualTo(money2.hashCode())
    }

    @Test
    @DisplayName("서로 다른 금액을 가진 Money 객체는 동등하지 않다")
    fun `Money objects with different values should not be equal`() {
        // given & when
        val money1 = Money(BigDecimal("1000"))
        val money2 = Money(BigDecimal("1000.01"))

        // then
        assertThat(money1).isNotEqualTo(money2)
    }
}