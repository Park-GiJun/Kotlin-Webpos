package com.gijun.mainserver.application.port.out.organization.hq

import com.gijun.mainserver.domain.organization.hq.model.Hq

interface HqCommandRepository {
    fun save(hq: Hq, requestId: String): Hq
    fun update(hq: Hq, requestId: String): Hq
    fun deleteById(hqId: Long, requestId: String)
}