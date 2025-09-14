package com.gijun.mainserver.domain.product.product.model

enum class ProductType(val displayName: String, val description: String) {
    RECIPE("레시피", "조리가 필요한 음식/음료 상품"),
    PRODUCT("완제품", "즉시 판매 가능한 완성된 상품"),
    INGREDIENT("원재료", "조리에 사용되는 재료"),
    PACKAGE("패키지", "여러 상품을 묶은 세트 상품"),
    SERVICE("서비스", "무형의 서비스 상품"),
    GIFT_CARD("기프트카드", "선불 결제 카드"),
    VOUCHER("상품권", "할인이나 무료 제공 권한"),
    DIGITAL("디지털", "디지털 콘텐츠나 소프트웨어"),
    SUBSCRIPTION("구독", "정기 구독 상품"),
    RENTAL("렌탈", "대여 상품");

    fun isPhysical(): Boolean = when (this) {
        RECIPE, PRODUCT, INGREDIENT, PACKAGE -> true
        SERVICE, GIFT_CARD, VOUCHER, DIGITAL, SUBSCRIPTION, RENTAL -> false
    }

    fun requiresInventoryTracking(): Boolean = when (this) {
        RECIPE, PRODUCT, INGREDIENT, PACKAGE, GIFT_CARD, VOUCHER -> true
        SERVICE, DIGITAL, SUBSCRIPTION, RENTAL -> false
    }

    fun canBeSold(): Boolean = when (this) {
        INGREDIENT -> false
        else -> true
    }

    fun requiresCooking(): Boolean = when (this) {
        RECIPE -> true
        else -> false
    }

    companion object {
        fun getPhysicalTypes(): List<ProductType> = values().filter { it.isPhysical() }
        fun getSellableTypes(): List<ProductType> = values().filter { it.canBeSold() }
        fun getInventoryTrackedTypes(): List<ProductType> = values().filter { it.requiresInventoryTracking() }
    }
}