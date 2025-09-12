package com.gijun.mainserver.domain.organization.pos.model

import com.gijun.mainserver.domain.organization.pos.vo.PosStatus
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Pos 도메인 엔티티 테스트")
class PosTest {

    @Test
    @DisplayName("유효한 정보로 Pos 생성 시 성공한다")
    fun `should create Pos with valid information`() {
        // given
        val storeId = 10L
        val posNumber = "POS-001"
        val deviceType = "TABLET"
        val status = PosStatus.ACTIVE

        // when
        val pos = Pos(
            id = null,
            storeId = storeId,
            posNumber = posNumber,
            deviceType = deviceType,
            status = status
        )

        // then
        assertThat(pos).isNotNull
        assertThat(pos.id).isNull()
        assertThat(pos.storeId).isEqualTo(storeId)
        assertThat(pos.posNumber).isEqualTo(posNumber)
        assertThat(pos.deviceType).isEqualTo(deviceType)
        assertThat(pos.status).isEqualTo(status)
    }

    @Test
    @DisplayName("Pos는 매장 ID 없이 생성할 수 없다")
    fun `should not create Pos without storeId`() {
        // when & then
        assertThatThrownBy {
            Pos(
                id = null,
                storeId = 0,
                posNumber = "POS-001",
                deviceType = "TABLET",
                status = PosStatus.ACTIVE
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("POS must belong to a store (storeId)")
    }

    @Test
    @DisplayName("POS 번호나 기기 타입이 비어있으면 생성할 수 없다")
    fun `should not create Pos with blank posNumber or deviceType`() {
        // when & then
        assertThatThrownBy {
            Pos(
                id = null,
                storeId = 1L,
                posNumber = "",
                deviceType = "TABLET",
                status = PosStatus.ACTIVE
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("POS number cannot be blank")

        assertThatThrownBy {
            Pos(
                id = null,
                storeId = 1L,
                posNumber = "POS-001",
                deviceType = "",
                status = PosStatus.ACTIVE
            )
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Device type cannot be blank")
    }

    @Test
    @DisplayName("Pos copy 메서드를 통해 일부 필드를 수정할 수 있다")
    fun `should update fields using copy method`() {
        // given
        val originalPos = Pos(
            id = 1L,
            storeId = 10L,
            posNumber = "POS-001",
            deviceType = "TABLET",
            status = PosStatus.ACTIVE
        )

        // when
        val updatedPos = originalPos.copy(
            storeId = 20L,
            posNumber = "POS-002",
            status = PosStatus.INACTIVE
        )

        // then
        assertThat(updatedPos.id).isEqualTo(originalPos.id)
        assertThat(updatedPos.storeId).isEqualTo(20L)
        assertThat(updatedPos.posNumber).isEqualTo("POS-002")
        assertThat(updatedPos.deviceType).isEqualTo(originalPos.deviceType)
        assertThat(updatedPos.status).isEqualTo(PosStatus.INACTIVE)
    }

    @Test
    @DisplayName("동일한 매장에 여러 POS를 생성할 수 있다")
    fun `should create multiple POS for same store`() {
        // given
        val storeId = 10L

        // when
        val pos1 = Pos(null, storeId, "POS-001", "TABLET", PosStatus.ACTIVE)
        val pos2 = Pos(null, storeId, "POS-002", "DESKTOP", PosStatus.ACTIVE)
        val pos3 = Pos(null, storeId, "POS-003", "MOBILE", PosStatus.INACTIVE)

        // then
        assertThat(pos1.storeId).isEqualTo(storeId)
        assertThat(pos2.storeId).isEqualTo(storeId)
        assertThat(pos3.storeId).isEqualTo(storeId)
        assertThat(pos1.posNumber).isEqualTo("POS-001")
        assertThat(pos2.posNumber).isEqualTo("POS-002")
        assertThat(pos3.posNumber).isEqualTo("POS-003")
        assertThat(pos1.deviceType).isEqualTo("TABLET")
        assertThat(pos2.deviceType).isEqualTo("DESKTOP")
        assertThat(pos3.deviceType).isEqualTo("MOBILE")
    }

    @Test
    @DisplayName("POS 상태를 변경할 수 있다")
    fun `should change POS status`() {
        // given
        val pos = Pos(
            id = 1L,
            storeId = 1L,
            posNumber = "POS-001",
            deviceType = "TABLET",
            status = PosStatus.ACTIVE
        )

        // when & then
        val inactivePos = pos.copy(status = PosStatus.INACTIVE)
        val maintenancePos = pos.copy(status = PosStatus.MAINTENANCE)
        val offlinePos = pos.copy(status = PosStatus.OFFLINE)

        assertThat(inactivePos.status).isEqualTo(PosStatus.INACTIVE)
        assertThat(maintenancePos.status).isEqualTo(PosStatus.MAINTENANCE)
        assertThat(offlinePos.status).isEqualTo(PosStatus.OFFLINE)
    }

    @Test
    @DisplayName("Pos equals와 hashCode는 모든 필드를 비교한다")
    fun `equals and hashCode should compare all fields`() {
        // given
        val pos1 = Pos(1L, 10L, "POS-001", "TABLET", PosStatus.ACTIVE)
        val pos2 = Pos(1L, 10L, "POS-001", "TABLET", PosStatus.ACTIVE)
        val pos3 = Pos(2L, 10L, "POS-001", "TABLET", PosStatus.ACTIVE)
        val pos4 = Pos(1L, 10L, "POS-002", "TABLET", PosStatus.ACTIVE)

        // then
        assertThat(pos1).isEqualTo(pos2)
        assertThat(pos1.hashCode()).isEqualTo(pos2.hashCode())
        assertThat(pos1).isNotEqualTo(pos3)
        assertThat(pos1).isNotEqualTo(pos4)
    }
}