package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.recipeComposition.repository

import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.recipeComposition.entity.RecipeCompositionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RecipeCompositionJpaRepository : JpaRepository<RecipeCompositionJpaEntity, Long> {
    fun findByRecipeProductId(recipeProductId: Long): List<RecipeCompositionJpaEntity>
    fun findByIngredientProductId(ingredientProductId: Long): List<RecipeCompositionJpaEntity>
}