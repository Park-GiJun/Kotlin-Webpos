package com.gijun.mainserver.infrastructure.adapter.out.persistence.common.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: LocalDateTime

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    lateinit var createdBy: String

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: LocalDateTime

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false)
    lateinit var updatedBy: String

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null

    @Column(name = "deleted_by")
    var deletedBy: String? = null

    fun markAsDeleted(requestId: String) {
        this.isDeleted = true
        this.deletedAt = LocalDateTime.now()
        this.deletedBy = requestId
    }

    fun updateAudit(requestId: String) {
        this.updatedAt = LocalDateTime.now()
        this.updatedBy = requestId
    }

    fun initializeAudit(requestId: String) {
        this.createdBy = requestId
        this.createdAt = LocalDateTime.now()
        this.updatedBy = requestId
        this.updatedAt = LocalDateTime.now()
    }
}
