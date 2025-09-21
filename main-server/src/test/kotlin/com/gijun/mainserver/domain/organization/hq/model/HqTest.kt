package com.gijun.mainserver.domain.organization.hq.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class HqTest {

    @Test
    fun `HQ 생성 성공 - 모든 필드가 유효한 경우`() {
        // given
        val name = "본사"
        val representative = "김대표"
        val address = "서울시 강남구 테헤란로 123"
        val email = "hq@company.com"
        val phoneNumber = "02-1234-5678"

        // when
        val hq = Hq(
            id = 1L,
            name = name,
            representative = representative,
            address = address,
            email = email,
            phoneNumber = phoneNumber
        )

        // then
        assertEquals(1L, hq.id)
        assertEquals(name, hq.name)
        assertEquals(representative, hq.representative)
        assertEquals(address, hq.address)
        assertEquals(email, hq.email)
        assertEquals(phoneNumber, hq.phoneNumber)
    }

    @Test
    fun `HQ 생성 실패 - name이 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Hq(
                id = 1L,
                name = "",
                representative = "김대표",
                address = "서울시 강남구 테헤란로 123",
                email = "hq@company.com",
                phoneNumber = "02-1234-5678"
            )
        }
    }

    @Test
    fun `HQ 생성 실패 - representative가 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Hq(
                id = 1L,
                name = "본사",
                representative = "   ",
                address = "서울시 강남구 테헤란로 123",
                email = "hq@company.com",
                phoneNumber = "02-1234-5678"
            )
        }
    }

    @Test
    fun `HQ 생성 실패 - address가 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Hq(
                id = 1L,
                name = "본사",
                representative = "김대표",
                address = "",
                email = "hq@company.com",
                phoneNumber = "02-1234-5678"
            )
        }
    }

    @Test
    fun `HQ 생성 실패 - email이 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Hq(
                id = 1L,
                name = "본사",
                representative = "김대표",
                address = "서울시 강남구 테헤란로 123",
                email = "",
                phoneNumber = "02-1234-5678"
            )
        }
    }

    @Test
    fun `HQ 생성 실패 - phoneNumber가 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Hq(
                id = 1L,
                name = "본사",
                representative = "김대표",
                address = "서울시 강남구 테헤란로 123",
                email = "hq@company.com",
                phoneNumber = ""
            )
        }
    }
}