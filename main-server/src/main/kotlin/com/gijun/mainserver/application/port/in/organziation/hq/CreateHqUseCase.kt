package com.gijun.mainserver.application.port.`in`.organziation.hq

import com.gijun.mainserver.application.dto.command.hq.CreateHqCommand
import com.gijun.mainserver.application.dto.result.hq.CreateHqResult

interface CreateHqUseCase {
    fun createHqExecute(command: CreateHqCommand): CreateHqResult
}