package com.gijun.mainserver.domain.organization.store.model

import com.gijun.mainserver.domain.common.vo.Address
import com.gijun.mainserver.domain.common.vo.Email
import com.gijun.mainserver.domain.common.vo.PhoneNumber
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Store 도메인 엔티티 테스트")
class StoreTest {

    @Test
    @DisplayName("유효한 정보로 Store 생성 시 성공한다")
    fun `should create Store with valid information`() {
        // given
        val hqId = 1L
        val name = "강남점"
        val representative = "이영희"
        val address = Address("서울시 강남구 역삼동 123-45", "서울", "06234")
        val email = Email("gangnam@store.com")
        val phoneNumber = PhoneNumber("02-3456-7890")

        // when
        val store = Store(
            id = null,
            hqId = hqId,
            name = name,
            representative = representative,
            address = address,
            email = email,
            phoneNumber = phoneNumber
        )

        // then
        assertThat(store).isNotNull
        assertThat(store.id).isNull()
        assertThat(store.hqId).isEqualTo(hqId)
        assertThat(store.name).isEqualTo(name)
        assertThat(store.representative).isEqualTo(representative)
        assertThat(store.address).isEqualTo(address)
        assertThat(store.email).isEqualTo(email)
        assertThat(store.phoneNumber).isEqualTo(phoneNumber)
    }

    @Test
    @DisplayName("Store는 본사 ID 없이 생성할 수 없다")
    fun `should not create Store without hqId`() {
        // when & then
        assertThatThrownBy {
            Store(
                id = null,
                hqId = 0,
                name = "독립점포",
                representative = "김독립",
                address = Address("부산시 해운대구", "부산", "48095"),
                email = Email("independent@store.com"),
                phoneNumber = PhoneNumber("051-1234-5678")
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Store must belong to a headquarters (hqId)")
    }

    @Test
    @DisplayName("Store copy 메서드를 통해 일부 필드를 수정할 수 있다")
    fun `should update fields using copy method`() {
        // given
        val originalStore = Store(
            id = 1L,
            hqId = 1L,
            name = "원본점포",
            representative = "김원본",
            address = Address("서울시 종로구", "서울", "03195"),
            email = Email("original@store.com"),
            phoneNumber = PhoneNumber("02-1111-2222")
        )

        // when
        val updatedStore = originalStore.copy(
            name = "수정된점포",
            representative = "박수정",
            hqId = 2L
        )

        // then
        assertThat(updatedStore.id).isEqualTo(originalStore.id)
        assertThat(updatedStore.hqId).isEqualTo(2L)
        assertThat(updatedStore.name).isEqualTo("수정된점포")
        assertThat(updatedStore.representative).isEqualTo("박수정")
        assertThat(updatedStore.address).isEqualTo(originalStore.address)
        assertThat(updatedStore.email).isEqualTo(originalStore.email)
        assertThat(updatedStore.phoneNumber).isEqualTo(originalStore.phoneNumber)
    }

    @Test
    @DisplayName("Store equals와 hashCode는 모든 필드를 비교한다")
    fun `equals and hashCode should compare all fields`() {
        // given
        val address = Address("서울시 강남구", "서울", "06234")
        val email = Email("test@store.com")
        val phoneNumber = PhoneNumber("02-1234-5678")

        val store1 = Store(1L, 1L, "점포", "대표", address, email, phoneNumber)
        val store2 = Store(1L, 1L, "점포", "대표", address, email, phoneNumber)
        val store3 = Store(2L, 1L, "점포", "대표", address, email, phoneNumber)
        val store4 = Store(1L, 2L, "점포", "대표", address, email, phoneNumber)

        // then
        assertThat(store1).isEqualTo(store2)
        assertThat(store1.hashCode()).isEqualTo(store2.hashCode())
        assertThat(store1).isNotEqualTo(store3)
        assertThat(store1).isNotEqualTo(store4)
    }
}