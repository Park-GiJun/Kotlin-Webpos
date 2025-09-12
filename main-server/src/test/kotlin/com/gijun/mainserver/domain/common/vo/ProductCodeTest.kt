package com.gijun.mainserver.domain.common.vo

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("ProductCode 값 객체 테스트")
class ProductCodeTest {

    @ParameterizedTest
    @ValueSource(strings = ["PROD001", "ABC-123", "1234567890", "COFFEE_001", "TEST.CODE"])
    @DisplayName("비어있지 않은 문자열로 ProductCode 생성 시 성공한다")
    fun `should create ProductCode with non-blank string`(validCode: String) {
        // when
        val productCode = ProductCode(validCode)

        // then
        assertThat(productCode.value).isEqualTo(validCode)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", " ", "   ", "\t", "\n"])
    @DisplayName("비어있거나 공백만 있는 문자열로 ProductCode 생성 시 예외가 발생한다")
    fun `should throw exception when creating ProductCode with blank string`(blankCode: String) {
        // when & then
        assertThatThrownBy { ProductCode(blankCode) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("상품코드는 필수입니다")
    }

    @Test
    @DisplayName("동일한 코드 값을 가진 ProductCode 객체는 동등하다")
    fun `ProductCode objects with same value should be equal`() {
        // given
        val codeString = "PROD001"

        // when
        val code1 = ProductCode(codeString)
        val code2 = ProductCode(codeString)

        // then
        assertThat(code1).isEqualTo(code2)
        assertThat(code1.hashCode()).isEqualTo(code2.hashCode())
    }

    @Test
    @DisplayName("서로 다른 코드 값을 가진 ProductCode 객체는 동등하지 않다")
    fun `ProductCode objects with different values should not be equal`() {
        // given & when
        val code1 = ProductCode("PROD001")
        val code2 = ProductCode("PROD002")

        // then
        assertThat(code1).isNotEqualTo(code2)
    }
}