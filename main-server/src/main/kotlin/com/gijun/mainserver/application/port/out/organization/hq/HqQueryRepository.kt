package com.gijun.mainserver.application.port.out.organization.hq

import com.gijun.mainserver.domain.organization.hq.model.Hq
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface HqQueryRepository {
    fun findById(hqId: Long): Hq?
    fun findAllActive(): List<Hq>
    fun findAll(pageable: Pageable): Page<Hq>
    fun existsByName(name: String): Boolean
    fun existsById(hqId: Long): Boolean
    fun findByNameContaining(keyword: String): List<Hq>
}