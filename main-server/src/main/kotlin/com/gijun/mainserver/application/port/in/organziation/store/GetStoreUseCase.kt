package com.gijun.mainserver.application.port.`in`.organziation.store

import com.gijun.mainserver.application.dto.query.organization.store.GetAllStoreQuery
import com.gijun.mainserver.application.dto.query.organization.store.GetStoreByIdQuery
import com.gijun.mainserver.application.dto.query.organization.store.GetStoresByHqIdQuery
import com.gijun.mainserver.application.dto.result.organization.store.StoreResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetStoreUseCase {
    fun getStoreById(query: GetStoreByIdQuery): StoreResult
    fun getAllStores(query: GetAllStoreQuery, pageable: Pageable): Page<StoreResult>
    fun getStoresByHqId(query: GetStoresByHqIdQuery): List<StoreResult>
    fun searchStoreByName(name: String): List<StoreResult>
}