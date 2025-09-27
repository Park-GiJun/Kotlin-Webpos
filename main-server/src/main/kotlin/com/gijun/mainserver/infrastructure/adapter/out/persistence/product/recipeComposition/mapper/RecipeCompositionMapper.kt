package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.recipeComposition.mapper

import com.gijun.mainserver.domain.product.recipeComposition.model.RecipeComposition
import com.gijun.mainserver.infrastructure.adapter.out.persistence.product.recipeComposition.entity.RecipeCompositionJpaEntity

object RecipeCompositionMapper {

    fun toDomain(entity: RecipeCompositionJpaEntity): RecipeComposition {
        return RecipeComposition(
            id = entity.id,
            recipeProductId = entity.recipeProductId,
            ingredientProductId = entity.ingredientProductId,
            requiredQty = entity.requiredQty
        )
    }

    fun toEntity(domain: RecipeComposition): RecipeCompositionJpaEntity {
        return RecipeCompositionJpaEntity(
            id = domain.id ?: 0L,
            recipeProductId = domain.recipeProductId,
            ingredientProductId = domain.ingredientProductId,
            requiredQty = domain.requiredQty
        )
    }
}