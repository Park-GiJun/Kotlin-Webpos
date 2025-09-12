package com.gijun.mainserver.domain.common.vo

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("PhoneNumber 값 객체 테스트")
class PhoneNumberTest {

    @ParameterizedTest
    @ValueSource(strings = ["010-1234-5678", "02-123-4567", "031-8888-9999", "1588-1234", "02-6789-1234"])
    @DisplayName("유효한 전화번호 형식으로 PhoneNumber 생성 시 성공한다")
    fun `should create PhoneNumber with valid format`(validPhone: String) {
        // when
        val phoneNumber = PhoneNumber(validPhone)

        // then
        assertThat(phoneNumber.value).isEqualTo(validPhone)
    }

    @ParameterizedTest
    @ValueSource(strings = ["123456789", "010-abcd-5678", "010 1234 5678", "(02)123-4567", "+82-10-1234-5678", "", " ", "phone-number"])
    @DisplayName("유효하지 않은 전화번호 형식으로 PhoneNumber 생성 시 예외가 발생한다")
    fun `should throw exception when creating PhoneNumber with invalid format`(invalidPhone: String) {
        // when & then
        assertThatThrownBy { PhoneNumber(invalidPhone) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("올바른 전화번호 형식이 아닙니다")
    }

    @Test
    @DisplayName("동일한 전화번호 값을 가진 PhoneNumber 객체는 동등하다")
    fun `PhoneNumber objects with same value should be equal`() {
        // given
        val phoneString = "010-1234-5678"

        // when
        val phone1 = PhoneNumber(phoneString)
        val phone2 = PhoneNumber(phoneString)

        // then
        assertThat(phone1).isEqualTo(phone2)
        assertThat(phone1.hashCode()).isEqualTo(phone2.hashCode())
    }

    @Test
    @DisplayName("서로 다른 전화번호 값을 가진 PhoneNumber 객체는 동등하지 않다")
    fun `PhoneNumber objects with different values should not be equal`() {
        // given & when
        val phone1 = PhoneNumber("010-1234-5678")
        val phone2 = PhoneNumber("010-8765-4321")

        // then
        assertThat(phone1).isNotEqualTo(phone2)
    }
}