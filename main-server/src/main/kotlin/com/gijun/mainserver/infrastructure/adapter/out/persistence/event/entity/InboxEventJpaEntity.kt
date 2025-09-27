package com.gijun.mainserver.infrastructure.adapter.out.persistence.event.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "inbox_events")
class InboxEventJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "event_id", unique = true)
    val eventId: String,

    @Column(name = "event_type")
    val eventType: String,

    @Column(name = "aggregate_type")
    val aggregateType: String,

    @Column(name = "aggregate_id")
    val aggregateId: String,

    @Column(name = "event_data", columnDefinition = "JSON")
    val eventData: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "processed_at")
    var processedAt: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    var status: InboxStatus = InboxStatus.PENDING,

    @Column(name = "retry_count")
    var retryCount: Int = 0
)