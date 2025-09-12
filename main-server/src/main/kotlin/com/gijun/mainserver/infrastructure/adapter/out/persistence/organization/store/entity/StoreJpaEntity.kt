package com.gijun.mainserver.infrastructure.adapter.out.persistence.organization.store.entity

import com.gijun.mainserver.infrastructure.adapter.out.persistence.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "store")
open class StoreJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "hq_id", nullable = false)
    val hqId: Long,

    @Column(name = "name", nullable = false, length = 100)
    val name: String,

    @Column(name = "representative", nullable = false, length = 50)
    val representative: String,

    @Column(name = "street", nullable = false, length = 255)
    val street: String,

    @Column(name = "city", nullable = false, length = 100)
    val city: String,

    @Column(name = "zip_code", nullable = false, length = 20)
    val zipCode: String,

    @Column(name = "email", length = 100)
    val email: String?,

    @Column(name = "phone_number", length = 20)
    val phoneNumber: String?

) : BaseEntity() {


}