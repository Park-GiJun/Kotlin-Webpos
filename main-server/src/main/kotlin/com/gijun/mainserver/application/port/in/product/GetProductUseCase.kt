package com.gijun.mainserver.application.port.`in`.product

import com.gijun.mainserver.application.dto.query.product.GetAllProductQuery
import com.gijun.mainserver.application.dto.query.product.GetProductByIdQuery
import com.gijun.mainserver.application.dto.query.product.GetProductsByHqIdQuery
import com.gijun.mainserver.application.dto.result.product.ProductResult

interface GetProductUseCase {
    fun getAllProductExecute(query: GetAllProductQuery): List<ProductResult>
    fun getProductByIdExecute(query: GetProductByIdQuery): ProductResult?
    fun getProductsByHqIdExecute(query: GetProductsByHqIdQuery): List<ProductResult>
}