package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.pos.entity

import com.gijun.mainserver.infrastructure.adapter.out.persistence.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "pos")
open class PosJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "hq_id", nullable = false)
    val hqId: Long,

    @Column(name = "store_id", nullable = false)
    val storeId: Long,

    @Column(name = "pos_number", nullable = false, unique = true, length = 50)
    val posNumber: String,

    @Column(name = "device_type", nullable = false, length = 50)
    val deviceType: String,

    @Column(name = "status", nullable = false, length = 20)
    val status: String

) : BaseEntity() {


}