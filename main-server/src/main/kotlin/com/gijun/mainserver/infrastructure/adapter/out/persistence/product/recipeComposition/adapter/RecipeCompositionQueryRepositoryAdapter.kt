package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.recipeComposition.adapter

import com.gijun.mainserver.application.port.out.product.recipeComposition.RecipeCompositionQueryRepository
import com.gijun.mainserver.domain.product.recipeComposition.model.RecipeComposition
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.recipeComposition.mapper.RecipeCompositionMapper
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.recipeComposition.repository.RecipeCompositionJpaRepository
import org.springframework.stereotype.Repository

@Repository
class RecipeCompositionQueryRepositoryAdapter(
    private val recipeCompositionJpaRepository: RecipeCompositionJpaRepository
) : RecipeCompositionQueryRepository {

    override fun findByRecipeProductId(recipeProductId: Long): List<RecipeComposition> {
        return recipeCompositionJpaRepository.findByRecipeProductId(recipeProductId)
            .map { RecipeCompositionMapper.toDomain(it) }
    }

    override fun findByIngredientProductId(ingredientProductId: Long): List<RecipeComposition> {
        return recipeCompositionJpaRepository.findByIngredientProductId(ingredientProductId)
            .map { RecipeCompositionMapper.toDomain(it) }
    }
}