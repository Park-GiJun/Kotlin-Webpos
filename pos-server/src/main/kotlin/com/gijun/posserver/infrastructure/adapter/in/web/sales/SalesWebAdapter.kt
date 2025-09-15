package com.gijun.posserver.infrastructure.adapter.`in`.web.sales

import com.gijun.posserver.application.dto.query.sales.GetSalesByBillNoQuery
import com.gijun.posserver.application.dto.query.sales.GetSalesByIdQuery
import com.gijun.posserver.application.dto.query.sales.GetSalesByPosQuery
import com.gijun.posserver.application.dto.query.sales.GetSalesByStoreQuery
import com.gijun.posserver.application.handler.command.sales.SalesCommandHandler
import com.gijun.posserver.application.handler.query.sales.SalesQueryHandler
import com.gijun.posserver.infrastructure.adapter.`in`.web.common.ApiResponse
import com.gijun.posserver.infrastructure.adapter.`in`.web.sales.dto.*
import com.gijun.posserver.infrastructure.adapter.`in`.web.sales.mapper.SalesWebMapper
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/pos/sales")
class SalesWebAdapter(
    private val salesCommandHandler: SalesCommandHandler,
    private val salesQueryHandler: SalesQueryHandler
) {

    @PostMapping
    fun createSales(@Valid @RequestBody request: CreateSalesRequest): ResponseEntity<ApiResponse<CreateSalesResponse>> {
        return request
            .let { SalesWebMapper.toCommand(it) }
            .let { salesCommandHandler.createSales(it) }
            .let { SalesWebMapper.toCreateResponse(it) }
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    @PutMapping("/{id}")
    fun updateSales(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateSalesRequest
    ): ResponseEntity<ApiResponse<UpdateSalesResponse>> {
        return SalesWebMapper.toCommand(id, request)
            .let { salesCommandHandler.updateSales(it) }
            .let { SalesWebMapper.toUpdateResponse(it) }
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.ok(it) }
    }

    @DeleteMapping("/{id}")
    fun deleteSales(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        salesCommandHandler.deleteSales(id)

        return ApiResponse.success(Unit)
            .let { ResponseEntity.ok(it) }
    }

    @DeleteMapping("/bill/{billNo}")
    fun deleteSalesByBillNo(@PathVariable billNo: String): ResponseEntity<ApiResponse<Unit>> {
        salesCommandHandler.deleteSalesByBillNo(billNo)

        return ApiResponse.success(Unit)
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{id}")
    fun getSalesById(@PathVariable id: Long): ResponseEntity<ApiResponse<SalesResponse?>> {
        return GetSalesByIdQuery(id)
            .let { salesQueryHandler.getSalesById(it) }
            ?.let { SalesWebMapper.toResponse(it) }
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/bill/{billNo}")
    fun getSalesByBillNo(@PathVariable billNo: String): ResponseEntity<ApiResponse<SalesResponse?>> {
        return GetSalesByBillNoQuery(billNo)
            .let { salesQueryHandler.getSalesByBillNo(it) }
            ?.let { SalesWebMapper.toResponse(it) }
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/store/{storeId}")
    fun getSalesByStore(
        @PathVariable storeId: Long,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") endDate: LocalDateTime?
    ): ResponseEntity<ApiResponse<List<SalesResponse>>> {
        return GetSalesByStoreQuery(storeId, startDate, endDate)
            .let { salesQueryHandler.getSalesByStore(it) }
            .map { SalesWebMapper.toResponse(it) }
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/pos/{posId}")
    fun getSalesByPos(
        @PathVariable posId: Long,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") endDate: LocalDateTime?
    ): ResponseEntity<ApiResponse<List<SalesResponse>>> {
        return GetSalesByPosQuery(posId, startDate, endDate)
            .let { salesQueryHandler.getSalesByPos(it) }
            .map { SalesWebMapper.toResponse(it) }
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping
    fun getAllSales(): ResponseEntity<ApiResponse<List<SalesResponse>>> {
        return salesQueryHandler.getAllSales()
            .map { SalesWebMapper.toResponse(it) }
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/count")
    fun getSalesCount(
        @RequestParam storeId: Long,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") endDate: LocalDateTime
    ): ResponseEntity<ApiResponse<Long>> {
        return salesQueryHandler.getSalesCount(storeId, startDate, endDate)
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/exists/{id}")
    fun existsSalesById(@PathVariable id: Long): ResponseEntity<ApiResponse<Boolean>> {
        return salesQueryHandler.existsSalesById(id)
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/exists/bill/{billNo}")
    fun existsSalesByBillNo(@PathVariable billNo: String): ResponseEntity<ApiResponse<Boolean>> {
        return salesQueryHandler.existsSalesByBillNo(billNo)
            .let { ApiResponse.success(it) }
            .let { ResponseEntity.ok(it) }
    }
}