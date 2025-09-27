package com.gijun.posserver.application.scheduler

import com.gijun.posserver.application.port.out.event.OutboxEventRepository
import com.gijun.posserver.domain.event.model.OutboxStatus
import com.gijun.posserver.infrastructure.adapter.out.messaging.kafka.KafkaEventPublisher
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OutboxEventPublisher(
    private val outboxEventRepository: OutboxEventRepository,
    private val kafkaEventPublisher: KafkaEventPublisher
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val TOPIC_STOCK_REDUCTION = "stock-reduction-requested"
        private const val BATCH_SIZE = 100
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    fun publishPendingEvents() {
        try {
            val pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc(
                OutboxStatus.PENDING,
                PageRequest.of(0, BATCH_SIZE)
            )

            if (pendingEvents.isEmpty()) {
                return
            }

            logger.info("Publishing {} pending outbox events", pendingEvents.size)

            pendingEvents.forEach { event ->
                try {
                    val processingEvent = event.markAsProcessing()
                    outboxEventRepository.save(processingEvent)

                    val topic = when (event.eventType) {
                        "STOCK_REDUCTION_REQUESTED" -> TOPIC_STOCK_REDUCTION
                        else -> {
                            logger.warn("Unknown event type: {}", event.eventType)
                            return@forEach
                        }
                    }

                    kafkaEventPublisher.publish(topic, event)

                    val completedEvent = event.markAsCompleted()
                    outboxEventRepository.save(completedEvent)

                    logger.debug("Successfully published event: eventId={}", event.eventId)
                } catch (e: Exception) {
                    logger.error("Failed to publish event: eventId={}", event.eventId, e)
                    val failedEvent = event.markAsFailed()
                    outboxEventRepository.save(failedEvent)
                }
            }
        } catch (e: Exception) {
            logger.error("Error in outbox event publisher", e)
        }
    }
}