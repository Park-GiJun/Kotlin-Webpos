package com.gijun.posserver.domain.sales.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class SalesDetail(
    val id: Long?,
    val billId: Long,
    val hqId: Long,
    val storeId: Long,
    val posId: Long,
    val lineNo: Int,
    val productId: Long,
    val productCode: String,
    val qty: BigDecimal,
    val unitAmt: BigDecimal,
    val saleQty: BigDecimal,
    val saleType: Boolean,
    val saleDate: LocalDateTime,
    val saleAmt: BigDecimal,
    val payAmt: BigDecimal,
    val dcAmt: BigDecimal,
    val couponAmt: BigDecimal,
    val cardAmt: BigDecimal,
    val cashAmt: BigDecimal,
    val voucherAmt: BigDecimal,
    val promotionAmt: BigDecimal
) {
    init {
        require(saleAmt >= BigDecimal.ZERO) { "Sale amount cannot be negative" }
        require(payAmt >= BigDecimal.ZERO) { "Pay amount cannot be negative" }
        require(dcAmt >= BigDecimal.ZERO) { "Discount amount cannot be negative" }
        require(couponAmt >= BigDecimal.ZERO) { "Coupon amount cannot be negative" }
        require(cardAmt >= BigDecimal.ZERO) { "Card amount cannot be negative" }
        require(cashAmt >= BigDecimal.ZERO) { "Cash amount cannot be negative" }
        require(voucherAmt >= BigDecimal.ZERO) { "Voucher amount cannot be negative" }
        require(promotionAmt >= BigDecimal.ZERO) { "Promotion amount cannot be negative" }
        require(saleQty > BigDecimal.ZERO) { "Sale quantity must be positive" }
        require(productCode.isNotBlank()) { "Product code cannot be blank" }
    }

    fun getTotalDiscountAmount(): BigDecimal = dcAmt + couponAmt + promotionAmt

    fun getTotalPaymentAmount(): BigDecimal = cardAmt + cashAmt + voucherAmt

    fun getNetSaleAmount(): BigDecimal = saleAmt - getTotalDiscountAmount()

    fun isValidLineItem(): Boolean {
        return saleAmt > BigDecimal.ZERO &&
               payAmt >= BigDecimal.ZERO &&
               getNetSaleAmount().compareTo(payAmt) == 0 &&
               getTotalPaymentAmount().compareTo(payAmt) == 0
    }

    fun applyDiscount(discountAmount: BigDecimal): SalesDetail {
        require(discountAmount >= BigDecimal.ZERO) { "Discount amount cannot be negative" }
        require(discountAmount <= saleAmt) { "Discount cannot exceed sale amount" }

        return this.copy(
            dcAmt = this.dcAmt + discountAmount,
            payAmt = this.payAmt - discountAmount
        )
    }

    fun getTotalProductAmount(): BigDecimal = saleAmt * saleQty
}
