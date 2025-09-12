package com.gijun.mainserver.domain.organization.hq.model

import com.gijun.mainserver.domain.common.vo.Address
import com.gijun.mainserver.domain.common.vo.Email
import com.gijun.mainserver.domain.common.vo.PhoneNumber
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("Hq 도메인 엔티티 테스트")
class HqTest {

    @Test
    @DisplayName("유효한 정보로 Hq 생성 시 성공한다")
    fun `should create Hq with valid information`() {
        // given
        val name = "테스트 본사"
        val representative = "홍길동"
        val address = Address("서울시 강남구 테헤란로 123", "서울", "06234")
        val email = Email("test@company.com")
        val phoneNumber = PhoneNumber("02-1234-5678")

        // when
        val hq = Hq(
            id = null,
            name = name,
            representative = representative,
            address = address,
            email = email,
            phoneNumber = phoneNumber
        )

        // then
        assertThat(hq).isNotNull
        assertThat(hq.id).isNull()
        assertThat(hq.name).isEqualTo(name)
        assertThat(hq.representative).isEqualTo(representative)
        assertThat(hq.address).isEqualTo(address)
        assertThat(hq.email).isEqualTo(email)
        assertThat(hq.phoneNumber).isEqualTo(phoneNumber)
    }

    @Test
    @DisplayName("Hq copy 메서드를 통해 일부 필드를 수정할 수 있다")
    fun `should update fields using copy method`() {
        // given
        val originalHq = Hq(
            id = 1L,
            name = "원본 본사",
            representative = "김철수",
            address = Address("서울시 강남구", "서울", "06234"),
            email = Email("original@company.com"),
            phoneNumber = PhoneNumber("02-1111-2222")
        )

        // when
        val updatedHq = originalHq.copy(
            name = "수정된 본사",
            representative = "박영희"
        )

        // then
        assertThat(updatedHq.id).isEqualTo(originalHq.id)
        assertThat(updatedHq.name).isEqualTo("수정된 본사")
        assertThat(updatedHq.representative).isEqualTo("박영희")
        assertThat(updatedHq.address).isEqualTo(originalHq.address)
        assertThat(updatedHq.email).isEqualTo(originalHq.email)
        assertThat(updatedHq.phoneNumber).isEqualTo(originalHq.phoneNumber)
    }

    @Test
    @DisplayName("Hq equals와 hashCode는 모든 필드를 비교한다")
    fun `equals and hashCode should compare all fields`() {
        // given
        val address = Address("서울시 강남구", "서울", "06234")
        val email = Email("test@company.com")
        val phoneNumber = PhoneNumber("02-1234-5678")

        val hq1 = Hq(1L, "본사", "대표", address, email, phoneNumber)
        val hq2 = Hq(1L, "본사", "대표", address, email, phoneNumber)
        val hq3 = Hq(2L, "본사", "대표", address, email, phoneNumber)

        // then
        assertThat(hq1).isEqualTo(hq2)
        assertThat(hq1.hashCode()).isEqualTo(hq2.hashCode())
        assertThat(hq1).isNotEqualTo(hq3)
    }
}