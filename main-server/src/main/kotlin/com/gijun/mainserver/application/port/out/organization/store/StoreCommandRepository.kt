package com.gijun.mainserver.application.port.out.organization.store

import com.gijun.mainserver.domain.organization.store.model.Store

interface StoreCommandRepository {
    fun save(store: Store, requestId: String): Store
    fun update(store: Store, requestId: String): Store
    fun deleteById(storeId: Long, requestId: String)
}