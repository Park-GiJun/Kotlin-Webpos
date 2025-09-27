package com.gijun.mainserver.infrastructure.adapter.out.persistence.product.recipeComposition.entity

import com.gijun.mainserver.infrastructure.adapter.out.persistence.common.entity.BaseEntity
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "recipe_composition")
open class RecipeCompositionJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "recipe_product_id", nullable = false)
    val recipeProductId: Long,

    @Column(name = "ingredient_product_id", nullable = false)
    val ingredientProductId: Long,

    @Column(name = "required_qty", nullable = false, precision = 10, scale = 2)
    val requiredQty: BigDecimal

) : BaseEntity()