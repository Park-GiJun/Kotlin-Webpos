package com.gijun.mainserver.application.port.out.organization.pos

import com.gijun.mainserver.application.dto.result.organization.pos.PosResult
import com.gijun.mainserver.domain.organization.pos.model.Pos
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface PosQueryRepository {
    // Domain methods (for Command handlers)
    fun findById(posId: Long): Pos?
    fun findByStoreId(storeId: Long): List<Pos>
    fun findByHqId(hqId: Long): List<Pos>
    fun findAllActive(): List<Pos>
    fun existsByPosNumber(posNumber: String): Boolean
    fun existsById(posId: Long): Boolean
    fun findByPosNumberContaining(keyword: String): List<Pos>

    // Result DTO methods (for Query handlers) - includes audit data from entities
    fun findPosResultById(posId: Long): PosResult?
    fun findPosResultsByStoreId(storeId: Long): List<PosResult>
    fun findPosResultsByStoreId(storeId: Long, pageable: Pageable): Page<PosResult>
    fun findPosResultsByHqId(hqId: Long): List<PosResult>
    fun findPosResultsByHqId(hqId: Long, pageable: Pageable): Page<PosResult>
    fun findAllPosResults(pageable: Pageable): Page<PosResult>
    fun findPosResultsByNameContaining(keyword: String): List<PosResult>
}