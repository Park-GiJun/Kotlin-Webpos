package com.gijun.mainserver.application.port.`in`.organziation.store

import com.gijun.mainserver.application.dto.command.store.CreateStoreCommand
import com.gijun.mainserver.application.dto.result.store.CreateStoreResult

interface CreateStoreUseCase {
    fun createStoreExecute(command: CreateStoreCommand): CreateStoreResult
}