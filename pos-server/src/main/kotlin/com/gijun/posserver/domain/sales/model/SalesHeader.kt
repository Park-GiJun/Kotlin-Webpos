package com.gijun.posserver.domain.sales.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class SalesHeader(
    val id: Long?,
    val hqId: Long,
    val storeId: Long,
    val posId: Long,
    val billNo: String,
    val saleType: Boolean,
    val saleDate: LocalDateTime,
    val saleAmt: BigDecimal,
    val payAmt: BigDecimal,
    val dcAmt: BigDecimal,
    val couponAmt: BigDecimal,
    val cardAmt: BigDecimal,
    val cashAmt: BigDecimal,
    val voucherAmt: BigDecimal,
    val promotionAmt: BigDecimal,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
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
        require(billNo.isNotBlank()) { "Bill number cannot be blank" }
    }

    fun getTotalDiscountAmount(): BigDecimal = dcAmt + couponAmt + promotionAmt

    fun getTotalPaymentAmount(): BigDecimal = cardAmt + cashAmt + voucherAmt

    fun isPaymentComplete(): Boolean = payAmt.compareTo(getTotalPaymentAmount()) == 0

    fun getNetSaleAmount(): BigDecimal = saleAmt - getTotalDiscountAmount()

    fun isValidSale(): Boolean {
        return saleAmt > BigDecimal.ZERO &&
               payAmt >= BigDecimal.ZERO &&
               getNetSaleAmount().compareTo(payAmt) == 0 &&
               isPaymentComplete()
    }
}