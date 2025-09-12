package com.gijun.mainserver.application.port.`in`.organziation.hq

import com.gijun.mainserver.application.dto.command.organization.hq.CreateHqCommand
import com.gijun.mainserver.application.dto.result.organization.hq.CreateHqResult

interface CreateHqUseCase {
    fun createHqExecute(command: CreateHqCommand): CreateHqResult
}