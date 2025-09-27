package com.gijun.mainserver.application.port.out.product.recipeComposition

import com.gijun.mainserver.domain.product.recipeComposition.model.RecipeComposition

interface RecipeCompositionQueryRepository {
    fun findByRecipeProductId(recipeProductId: Long): List<RecipeComposition>
    fun findByIngredientProductId(ingredientProductId: Long): List<RecipeComposition>
}