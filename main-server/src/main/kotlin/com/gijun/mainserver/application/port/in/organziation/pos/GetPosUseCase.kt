package com.gijun.mainserver.application.port.`in`.organziation.pos

import com.gijun.mainserver.application.dto.query.organization.pos.GetAllPosQuery
import com.gijun.mainserver.application.dto.query.organization.pos.GetPosByIdQuery
import com.gijun.mainserver.application.dto.query.organization.pos.GetPosByStoreIdQuery
import com.gijun.mainserver.application.dto.query.organization.pos.GetPosByHqIdQuery
import com.gijun.mainserver.application.dto.result.organization.pos.PosResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetPosUseCase {
    fun getPosById(query: GetPosByIdQuery): PosResult
    fun getAllPos(query: GetAllPosQuery, pageable: Pageable): Page<PosResult>
    fun getPosByStoreId(query: GetPosByStoreIdQuery): List<PosResult>
    fun getPosByHqId(query: GetPosByHqIdQuery): List<PosResult>
    fun searchPosByName(name: String): List<PosResult>
}