package com.gijun.mainserver.domain.product.recipeComposition.model

import java.math.BigDecimal

data class RecipeComposition(
    val id: Long?,
    val recipeProductId: Long,
    val ingredientProductId: Long,
    val requiredQty: BigDecimal
) {
    init {
        require(recipeProductId > 0) { "Recipe product ID must be positive" }
        require(ingredientProductId > 0) { "Ingredient product ID must be positive" }
        require(requiredQty > BigDecimal.ZERO) { "Required quantity must be positive" }
    }
}