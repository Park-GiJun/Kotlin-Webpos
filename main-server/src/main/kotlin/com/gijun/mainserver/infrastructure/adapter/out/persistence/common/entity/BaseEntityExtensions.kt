package com.gijun.mainserver.infrastructure.adapter.out.persistence.common.entity

fun BaseEntity.isActive(): Boolean = !this.isDeleted