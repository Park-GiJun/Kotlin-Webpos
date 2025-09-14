package com.gijun.posserver.domain.sales.model

import com.gijun.posserver.domain.common.vo.BillNo
import com.gijun.posserver.domain.common.vo.HqId
import com.gijun.posserver.domain.common.vo.Money
import com.gijun.posserver.domain.common.vo.PosId
import com.gijun.posserver.domain.common.vo.SalesId
import com.gijun.posserver.domain.common.vo.StoreId
import java.time.LocalDate

data class SalesHeader(
    val id: Long?,
    val hqId: Long,
    val storeId: Long,
    val posId: Long,
    val billNo: BillNo,
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
    }

    fun getTotalDiscountAmount(): Money = dcAmt + couponAmt + promotionAmt

    fun getTotalPaymentAmount(): Money = cardAmt + cashAmt + voucherAmt

    fun isPaymentComplete(): Boolean = payAmt == getTotalPaymentAmount()

    fun getNetSaleAmount(): Money = saleAmt - getTotalDiscountAmount()

    fun isValidSale(): Boolean {
        return saleAmt.isPositive() &&
               payAmt >= Money.ZERO &&
               getNetSaleAmount() == payAmt &&
               isPaymentComplete()
    }
}