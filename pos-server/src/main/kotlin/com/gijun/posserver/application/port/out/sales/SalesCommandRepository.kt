package com.gijun.posserver.application.port.out.sales

import com.gijun.posserver.domain.sales.model.Sales
import com.gijun.posserver.domain.sales.model.SalesHeader

interface SalesCommandRepository {
    fun save(sales: Sales): Sales
    fun update(sales: Sales): Sales
    fun delete(id: Long)
    fun deleteByBillNo(billNo: String)
    fun saveHeader(header: SalesHeader): SalesHeader
    fun updateHeader(header: SalesHeader): SalesHeader
}