package com.gijun.mainserver.application.dto.result.hq

import com.gijun.mainserver.domain.organization.hq.model.Hq
import java.time.LocalDateTime

data class CreateHqResult(
    val hqId: Long,
    val name: String
)
