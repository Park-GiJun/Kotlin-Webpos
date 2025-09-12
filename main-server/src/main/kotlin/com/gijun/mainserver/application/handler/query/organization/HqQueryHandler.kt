package com.gijun.mainserver.application.handler.query.organization

import com.gijun.mainserver.application.dto.query.organization.hq.GetHqByIdQuery
import com.gijun.mainserver.application.dto.query.organization.hq.GetAllHqQuery
import com.gijun.mainserver.application.dto.result.organization.hq.HqResult
import com.gijun.mainserver.application.mapper.HqApplicationMapper
import com.gijun.mainserver.application.port.`in`.organziation.hq.GetHqUseCase
import com.gijun.mainserver.application.port.out.organization.hq.HqQueryRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class HqQueryHandler(
    private val hqQueryRepository: HqQueryRepository
) : GetHqUseCase {

    @Transactional(readOnly = true)
    @Cacheable(value = ["hq"], key = "#query.hqId")
    override fun getHqById(query: GetHqByIdQuery): HqResult {
        return hqQueryRepository.findById(query.hqId)
            ?.let { HqApplicationMapper.toHqResultFromDomain(it) }
            ?: throw NoSuchElementException("HQ not found with id: ${query.hqId}")
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["hq-list"], key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    override fun getAllHq(query: GetAllHqQuery, pageable: Pageable): Page<HqResult> {
        return hqQueryRepository.findAll(pageable)
            .map { HqApplicationMapper.toHqResultFromDomain(it) }
    }

    @Transactional(readOnly = true)
    override fun searchHqByName(name: String): List<HqResult> {
        return hqQueryRepository.findByNameContaining(name)
            .map { HqApplicationMapper.toHqResultFromDomain(it) }
    }
}