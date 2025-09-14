package com.gijun.posserver.domain.sales.model

import com.gijun.posserver.domain.common.vo.LineNo
import com.gijun.posserver.domain.common.vo.Money
import java.time.LocalDate

data class SalesDetail(
    val id: Long?,
    val billId: Long,
    val hqId: Long,
    val storeId: Long,
    val posId: Long,
    val lineNo: LineNo,
    val productId: Long,
    val saleQty: Int,
    val saleType: Boolean,
    val saleDate: LocalDate,
    val saleAmt: Money,
    val payAmt: Money,
    val dcAmt: Money,
    val couponAmt: Money,
    val cardAmt: Money,
    val cashAmt: Money,
    val voucherAmt: Money,
    val promotionAmt: Money
) {
    init {
        require(saleAmt >= Money.ZERO) { "Sale amount cannot be negative" }
        require(payAmt >= Money.ZERO) { "Pay amount cannot be negative" }
        require(dcAmt >= Money.ZERO) { "Discount amount cannot be negative" }
        require(couponAmt >= Money.ZERO) { "Coupon amount cannot be negative" }
        require(cardAmt >= Money.ZERO) { "Card amount cannot be negative" }
        require(cashAmt >= Money.ZERO) { "Cash amount cannot be negative" }
        require(voucherAmt >= Money.ZERO) { "Voucher amount cannot be negative" }
        require(promotionAmt >= Money.ZERO) { "Promotion amount cannot be negative" }
        require(saleQty > 0) { "Sale quantity must be positive" }
    }

    fun getTotalDiscountAmount(): Money = dcAmt + couponAmt + promotionAmt

    fun getTotalPaymentAmount(): Money = cardAmt + cashAmt + voucherAmt

    fun getNetSaleAmount(): Money = saleAmt - getTotalDiscountAmount()

    fun isValidLineItem(): Boolean {
        return saleAmt.isPositive() &&
               payAmt >= Money.ZERO &&
               getNetSaleAmount() == payAmt &&
               getTotalPaymentAmount() == payAmt
    }

    fun applyDiscount(discountAmount: Money): SalesDetail {
        require(discountAmount >= Money.ZERO) { "Discount amount cannot be negative" }
        require(discountAmount <= saleAmt) { "Discount cannot exceed sale amount" }

        return this.copy(
            dcAmt = this.dcAmt + discountAmount,
            payAmt = this.payAmt - discountAmount
        )
    }

    fun getTotalProductAmount(): Money = saleAmt * saleQty
}
