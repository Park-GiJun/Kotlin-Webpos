package com.gijun.mainserver.infrastructure.adapter.`in`.messaging.kafka

import com.gijun.mainserver.application.port.out.event.InboxEventRepository
import com.gijun.mainserver.domain.event.model.InboxEvent
import com.gijun.mainserver.domain.event.model.InboxStatus
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Component
class StockReductionEventConsumer(
    private val inboxEventRepository: InboxEventRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = ["stock-reduction-requested"],
        groupId = "\${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    fun consumeStockReductionEvent(
        @Payload message: String,
        @Header(KafkaHeaders.RECEIVED_KEY) key: String,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.OFFSET) offset: Long,
        acknowledgment: Acknowledgment
    ) {
        try {
            logger.info("Received event from Kafka: topic={}, key={}, offset={}", topic, key, offset)

            val eventId = extractEventId(message)

            if (inboxEventRepository.existsByEventId(eventId)) {
                logger.info("Event already processed (idempotency check): eventId={}", eventId)
                acknowledgment.acknowledge()
                return
            }

            val inboxEvent = InboxEvent(
                id = null,
                eventId = eventId,
                eventType = "STOCK_REDUCTION_REQUESTED",
                aggregateType = "SALES",
                aggregateId = key,
                eventData = message,
                status = InboxStatus.PENDING,
                retryCount = 0
            )

            inboxEventRepository.save(inboxEvent)

            acknowledgment.acknowledge()

            logger.info("Successfully saved inbox event: eventId={}", eventId)
        } catch (e: Exception) {
            logger.error("Error processing Kafka message: topic={}, offset={}", topic, offset, e)
            throw e
        }
    }

    private fun extractEventId(message: String): String {
        return try {
            val regex = """"eventId"\s*:\s*"([^"]+)"""".toRegex()
            regex.find(message)?.groupValues?.get(1) ?: UUID.randomUUID().toString()
        } catch (e: Exception) {
            logger.warn("Failed to extract eventId from message, generating new UUID", e)
            UUID.randomUUID().toString()
        }
    }
}