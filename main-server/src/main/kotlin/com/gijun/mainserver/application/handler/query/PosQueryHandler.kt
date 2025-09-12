package com.gijun.mainserver.application.handler.query

import com.gijun.mainserver.application.dto.query.pos.GetAllPosQuery
import com.gijun.mainserver.application.dto.query.pos.GetPosByIdQuery
import com.gijun.mainserver.application.dto.query.pos.GetPosByStoreIdQuery
import com.gijun.mainserver.application.dto.query.pos.GetPosByHqIdQuery
import com.gijun.mainserver.application.dto.result.pos.PosResult
import com.gijun.mainserver.application.port.`in`.organziation.pos.GetPosUseCase
import com.gijun.mainserver.application.port.out.organization.pos.PosQueryRepository
import com.gijun.mainserver.infrastructure.config.ReadOnlyTransaction
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class PosQueryHandler(
    private val posQueryRepository: PosQueryRepository
) : GetPosUseCase {

    @ReadOnlyTransaction
    @Cacheable(value = ["pos"], key = "#query.posId")
    override fun getPosById(query: GetPosByIdQuery): PosResult {
        return posQueryRepository.findPosResultById(query.posId)
            ?: throw NoSuchElementException("POS not found with id: ${query.posId}")
    }

    @ReadOnlyTransaction
    @Cacheable(
        value = ["pos-list"],
        key = "#query.storeId + '-' + #query.hqId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize"
    )
    override fun getAllPos(query: GetAllPosQuery, pageable: Pageable): Page<PosResult> {
        return when {
            query.storeId != null -> posQueryRepository.findPosResultsByStoreId(query.storeId, pageable)
            query.hqId != null -> posQueryRepository.findPosResultsByHqId(query.hqId, pageable)
            else -> posQueryRepository.findAllPosResults(pageable)
        }
    }

    @ReadOnlyTransaction
    @Cacheable(value = ["pos-by-store"], key = "#query.storeId")
    override fun getPosByStoreId(query: GetPosByStoreIdQuery): List<PosResult> {
        return posQueryRepository.findPosResultsByStoreId(query.storeId)
    }

    @ReadOnlyTransaction
    @Cacheable(value = ["pos-by-hq"], key = "#query.hqId")
    override fun getPosByHqId(query: GetPosByHqIdQuery): List<PosResult> {
        return posQueryRepository.findPosResultsByHqId(query.hqId)
    }

    @ReadOnlyTransaction
    override fun searchPosByName(name: String): List<PosResult> {
        return posQueryRepository.findPosResultsByNameContaining(name)
    }
}