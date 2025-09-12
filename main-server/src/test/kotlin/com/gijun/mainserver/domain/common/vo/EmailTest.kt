package com.gijun.mainserver.domain.common.vo

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@DisplayName("Email 값 객체 테스트")
class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = ["test@example.com", "user.name@company.co.kr", "admin+tag@service.io", "support@sub.domain.com"])
    @DisplayName("유효한 이메일 형식으로 Email 생성 시 성공한다")
    fun `should create Email with valid format`(validEmail: String) {
        // when
        val email = Email(validEmail)

        // then
        assertThat(email.value).isEqualTo(validEmail)
    }

    @ParameterizedTest
    @ValueSource(strings = ["invalid", "@example.com", "test@", "test@com", "test.example.com", "", " ", "test @example.com"])
    @DisplayName("유효하지 않은 이메일 형식으로 Email 생성 시 예외가 발생한다")
    fun `should throw exception when creating Email with invalid format`(invalidEmail: String) {
        // when & then
        assertThatThrownBy { Email(invalidEmail) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("올바른 이메일 형식이 아닙니다")
    }

    @Test
    @DisplayName("동일한 이메일 값을 가진 Email 객체는 동등하다")
    fun `Email objects with same value should be equal`() {
        // given
        val emailString = "test@example.com"

        // when
        val email1 = Email(emailString)
        val email2 = Email(emailString)

        // then
        assertThat(email1).isEqualTo(email2)
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode())
    }

    @Test
    @DisplayName("서로 다른 이메일 값을 가진 Email 객체는 동등하지 않다")
    fun `Email objects with different values should not be equal`() {
        // given & when
        val email1 = Email("test1@example.com")
        val email2 = Email("test2@example.com")

        // then
        assertThat(email1).isNotEqualTo(email2)
    }
}