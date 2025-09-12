package com.gijun.mainserver.application.port.`in`.organziation.hq

import com.gijun.mainserver.application.dto.command.organization.hq.UpdateHqCommand
import com.gijun.mainserver.application.dto.result.organization.hq.UpdateHqResult

interface UpdateHqUseCase {
    fun updateHqExecute(command: UpdateHqCommand): UpdateHqResult
}