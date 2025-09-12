package com.gijun.mainserver.application.port.`in`.organziation.hq

import com.gijun.mainserver.application.dto.command.hq.UpdateHqCommand
import com.gijun.mainserver.application.dto.result.hq.UpdateHqResult

interface UpdateHqUseCase {
    fun updateHqExecute(command: UpdateHqCommand): UpdateHqResult
}