package com.gijun.mainserver.application.port.`in`.organziation.hq

import com.gijun.mainserver.application.dto.command.organization.hq.DeleteHqCommand
import com.gijun.mainserver.application.dto.result.organization.hq.DeleteHqResult

interface DeleteHqUseCase {
    fun deleteHqExecute(command: DeleteHqCommand): DeleteHqResult
}