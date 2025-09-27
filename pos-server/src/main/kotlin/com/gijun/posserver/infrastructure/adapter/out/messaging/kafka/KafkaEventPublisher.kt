package com.gijun.posserver.infrastructure.adapter.out.messaging.kafka

import com.gijun.posserver.domain.event.model.OutboxEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun publish(topic: String, event: OutboxEvent) {
        try {
            val future = kafkaTemplate.send(topic, event.aggregateId, event.eventData)
            future.whenComplete { result, ex ->
                if (ex == null) {
                    logger.info(
                        "Published event to Kafka: topic={}, eventId={}, aggregateId={}, offset={}",
                        topic, event.eventId, event.aggregateId, result?.recordMetadata?.offset()
                    )
                } else {
                    logger.error(
                        "Failed to publish event to Kafka: topic={}, eventId={}, aggregateId={}",
                        topic, event.eventId, event.aggregateId, ex
                    )
                }
            }
        } catch (e: Exception) {
            logger.error("Error publishing event to Kafka: eventId={}", event.eventId, e)
            throw e
        }
    }
}