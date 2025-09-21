package com.gijun.mainserver.domain.organization.pos.model

import com.gijun.mainserver.domain.organization.pos.vo.PosStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class PosTest {

    @Test
    fun `POS 생성 성공 - 모든 필드가 유효한 경우`() {
        // given
        val storeId = 1L
        val posNumber = "POS-001"
        val deviceType = "TABLET"
        val status = PosStatus.ACTIVE

        // when
        val pos = Pos(
            id = 1L,
            storeId = storeId,
            posNumber = posNumber,
            deviceType = deviceType,
            status = status
        )

        // then
        assertEquals(1L, pos.id)
        assertEquals(storeId, pos.storeId)
        assertEquals(posNumber, pos.posNumber)
        assertEquals(deviceType, pos.deviceType)
        assertEquals(status, pos.status)
    }

    @Test
    fun `POS 생성 실패 - storeId가 0 이하인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Pos(
                id = 1L,
                storeId = 0L,
                posNumber = "POS-001",
                deviceType = "TABLET",
                status = PosStatus.ACTIVE
            )
        }
    }

    @Test
    fun `POS 생성 실패 - storeId가 음수인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Pos(
                id = 1L,
                storeId = -1L,
                posNumber = "POS-001",
                deviceType = "TABLET",
                status = PosStatus.ACTIVE
            )
        }
    }

    @Test
    fun `POS 생성 실패 - posNumber가 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Pos(
                id = 1L,
                storeId = 1L,
                posNumber = "",
                deviceType = "TABLET",
                status = PosStatus.ACTIVE
            )
        }
    }

    @Test
    fun `POS 생성 실패 - deviceType이 공백인 경우`() {
        // given & when & then
        assertThrows<IllegalArgumentException> {
            Pos(
                id = 1L,
                storeId = 1L,
                posNumber = "POS-001",
                deviceType = "   ",
                status = PosStatus.ACTIVE
            )
        }
    }

    @Test
    fun `POS 상태별 생성 테스트`() {
        // given
        val storeId = 1L
        val posNumber = "POS-001"
        val deviceType = "TABLET"

        // when & then
        val activePos = Pos(1L, storeId, posNumber, deviceType, PosStatus.ACTIVE)
        assertEquals(PosStatus.ACTIVE, activePos.status)

        val inactivePos = Pos(2L, storeId, posNumber, deviceType, PosStatus.INACTIVE)
        assertEquals(PosStatus.INACTIVE, inactivePos.status)

        val maintenancePos = Pos(3L, storeId, posNumber, deviceType, PosStatus.MAINTENANCE)
        assertEquals(PosStatus.MAINTENANCE, maintenancePos.status)

        val offlinePos = Pos(4L, storeId, posNumber, deviceType, PosStatus.OFFLINE)
        assertEquals(PosStatus.OFFLINE, offlinePos.status)
    }
}