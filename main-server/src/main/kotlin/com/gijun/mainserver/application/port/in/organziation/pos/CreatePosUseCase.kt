package com.gijun.mainserver.application.port.`in`.organziation.pos

import com.gijun.mainserver.application.dto.command.pos.CreatePosCommand
import com.gijun.mainserver.application.dto.result.pos.CreatePosResult

interface CreatePosUseCase {
    fun createPosExecute(command: CreatePosCommand): CreatePosResult
}