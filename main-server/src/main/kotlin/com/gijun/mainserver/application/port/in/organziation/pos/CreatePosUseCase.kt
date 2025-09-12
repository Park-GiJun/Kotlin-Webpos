package com.gijun.mainserver.application.port.`in`.organziation.pos

import com.gijun.mainserver.application.dto.command.organization.pos.CreatePosCommand
import com.gijun.mainserver.application.dto.result.organization.pos.CreatePosResult

interface CreatePosUseCase {
    fun createPosExecute(command: CreatePosCommand): CreatePosResult
}