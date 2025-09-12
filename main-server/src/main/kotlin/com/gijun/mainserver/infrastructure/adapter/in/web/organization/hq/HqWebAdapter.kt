package com.gijun.mainserver.infrastructure.adapter.`in`.web.organization.hq

import com.gijun.mainserver.application.port.`in`.organziation.hq.CreateHqUseCase
import com.gijun.mainserver.infrastructure.adapter.`in`.web.common.ApiResponse
import com.gijun.mainserver.infrastructure.adapter.`in`.web.organization.hq.dto.CreateHqRequest
import com.gijun.mainserver.infrastructure.adapter.`in`.web.organization.hq.dto.CreateHqResponse
import com.gijun.mainserver.infrastructure.adapter.`in`.web.organization.hq.mapper.HqWebMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/main/organization/hq")
class HqWebAdapter(
    private val createHqUseCase: CreateHqUseCase
) {

    @PostMapping
    fun createHq(@RequestBody request: CreateHqRequest): ResponseEntity<ApiResponse<CreateHqResponse>> {
        return request
            .let { HqWebMapper.toCommand(it) }
            .let { createHqUseCase.createHqExecute(it) }
            .let { HqWebMapper.toResponse(it) }
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }
}