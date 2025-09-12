package com.gijun.mainserver.application.port.`in`.organziation.hq

import com.gijun.mainserver.application.dto.query.organization.hq.GetAllHqQuery
import com.gijun.mainserver.application.dto.query.organization.hq.GetHqByIdQuery
import com.gijun.mainserver.application.dto.result.organization.hq.HqResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetHqUseCase {
    fun getHqById(query: GetHqByIdQuery): HqResult
    fun getAllHq(query: GetAllHqQuery, pageable: Pageable): Page<HqResult>
    fun searchHqByName(name: String): List<HqResult>
}