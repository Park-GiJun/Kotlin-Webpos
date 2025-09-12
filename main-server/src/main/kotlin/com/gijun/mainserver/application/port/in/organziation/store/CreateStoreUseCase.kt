package com.gijun.mainserver.application.port.`in`.organziation.store

import com.gijun.mainserver.application.dto.command.organization.store.CreateStoreCommand
import com.gijun.mainserver.application.dto.result.organization.store.CreateStoreResult

interface CreateStoreUseCase {
    fun createStoreExecute(command: CreateStoreCommand): CreateStoreResult
}