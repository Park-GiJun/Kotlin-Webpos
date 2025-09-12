package com.gijun.mainserver.domain.common.vo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Address 값 객체 테스트")
class AddressTest {

    @Test
    @DisplayName("유효한 정보로 Address 생성 시 성공한다")
    fun `should create Address with valid information`() {
        // given
        val street = "서울시 강남구 테헤란로 123"
        val city = "서울"
        val zipCode = "06234"

        // when
        val address = Address(street, city, zipCode)

        // then
        assertThat(address.street).isEqualTo(street)
        assertThat(address.city).isEqualTo(city)
        assertThat(address.zipCode).isEqualTo(zipCode)
    }

    @Test
    @DisplayName("fullAddress 메서드는 전체 주소를 형식에 맞게 반환한다")
    fun `fullAddress should return formatted complete address`() {
        // given
        val address = Address("서울시 강남구 역삼동 123-45", "서울", "06234")

        // when
        val fullAddress = address.fullAddress()

        // then
        assertThat(fullAddress).isEqualTo("서울시 강남구 역삼동 123-45, 서울 06234")
    }

    @Test
    @DisplayName("Address copy 메서드를 통해 일부 필드를 수정할 수 있다")
    fun `should update fields using copy method`() {
        // given
        val originalAddress = Address("원본 주소", "원본시", "12345")

        // when
        val updatedAddress = originalAddress.copy(
            street = "수정된 주소",
            zipCode = "54321"
        )

        // then
        assertThat(updatedAddress.street).isEqualTo("수정된 주소")
        assertThat(updatedAddress.city).isEqualTo(originalAddress.city)
        assertThat(updatedAddress.zipCode).isEqualTo("54321")
    }

    @Test
    @DisplayName("동일한 값을 가진 Address 객체는 동등하다")
    fun `Address objects with same values should be equal`() {
        // given & when
        val address1 = Address("서울시 강남구", "서울", "06234")
        val address2 = Address("서울시 강남구", "서울", "06234")

        // then
        assertThat(address1).isEqualTo(address2)
        assertThat(address1.hashCode()).isEqualTo(address2.hashCode())
    }

    @Test
    @DisplayName("서로 다른 값을 가진 Address 객체는 동등하지 않다")
    fun `Address objects with different values should not be equal`() {
        // given & when
        val address1 = Address("서울시 강남구", "서울", "06234")
        val address2 = Address("서울시 강북구", "서울", "06234")
        val address3 = Address("서울시 강남구", "서울", "06235")

        // then
        assertThat(address1).isNotEqualTo(address2)
        assertThat(address1).isNotEqualTo(address3)
    }
}