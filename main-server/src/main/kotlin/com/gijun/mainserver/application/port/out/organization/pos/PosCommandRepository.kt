package com.gijun.mainserver.application.port.out.organization.pos

import com.gijun.mainserver.domain.organization.pos.model.Pos

interface PosCommandRepository {
    fun save(pos: Pos, requestId: String): Pos
    fun update(pos: Pos, requestId: String): Pos
    fun deleteById(posId: Long, requestId: String)
}