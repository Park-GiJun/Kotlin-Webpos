package com.gijun.mainserver.domain.organization.store.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class StoreTest {

    @Test
    fun `Store 생성 성공 - 모든 필드가 유효한 경우`() {
        // given
        val hqId = 1L
        val name = "강남점"
        val representative = "박점장"
        val address = "서울시 강남구 역삼동 123"
        val email = "gangnam@company.com"
        val phoneNumber = "02-2345-6789"

        // when
        val store = Store(
            id = 1L,
            hqId = hqId,
            name = name,
            representative = representative,
            address = address,
            email = email,
            phoneNumber = phoneNumber
        )

        // then
        assertEquals(1L, store.id)
        assertEquals(hqId, store.hqId)
        assertEquals(name, store.name)
        assertEquals(representative, store.representative)
        assertEquals(address, store.address)
        assertEquals(email, store.email)
        assertEquals(phoneNumber, store.phoneNumber)
    }

    @Test
    fun `Store 생성 실패 - hqId가 0 이하인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Store(
                id = 1L,
                hqId = 0L,
                name = "강남점",
                representative = "박점장",
                address = "서울시 강남구 역삼동 123",
                email = "gangnam@company.com",
                phoneNumber = "02-2345-6789"
            )
        }
    }

    @Test
    fun `Store 생성 실패 - hqId가 음수인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Store(
                id = 1L,
                hqId = -1L,
                name = "강남점",
                representative = "박점장",
                address = "서울시 강남구 역삼동 123",
                email = "gangnam@company.com",
                phoneNumber = "02-2345-6789"
            )
        }
    }

    @Test
    fun `Store 생성 실패 - name이 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Store(
                id = 1L,
                hqId = 1L,
                name = "",
                representative = "박점장",
                address = "서울시 강남구 역삼동 123",
                email = "gangnam@company.com",
                phoneNumber = "02-2345-6789"
            )
        }
    }

    @Test
    fun `Store 생성 실패 - representative가 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Store(
                id = 1L,
                hqId = 1L,
                name = "강남점",
                representative = "   ",
                address = "서울시 강남구 역삼동 123",
                email = "gangnam@company.com",
                phoneNumber = "02-2345-6789"
            )
        }
    }

    @Test
    fun `Store 생성 실패 - address가 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Store(
                id = 1L,
                hqId = 1L,
                name = "강남점",
                representative = "박점장",
                address = "",
                email = "gangnam@company.com",
                phoneNumber = "02-2345-6789"
            )
        }
    }

    @Test
    fun `Store 생성 실패 - email이 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Store(
                id = 1L,
                hqId = 1L,
                name = "강남점",
                representative = "박점장",
                address = "서울시 강남구 역삼동 123",
                email = "",
                phoneNumber = "02-2345-6789"
            )
        }
    }

    @Test
    fun `Store 생성 실패 - phoneNumber가 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Store(
                id = 1L,
                hqId = 1L,
                name = "강남점",
                representative = "박점장",
                address = "서울시 강남구 역삼동 123",
                email = "gangnam@company.com",
                phoneNumber = ""
            )
        }
    }
}