package com.gijun.mainserver.application.scheduler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gijun.mainserver.application.dto.command.product.productStock.AdjustProductStockCommand
import com.gijun.mainserver.application.dto.command.product.productStock.StockAdjustmentType
import com.gijun.mainserver.application.port.`in`.product.AdjustProductStockUseCase
import com.gijun.mainserver.application.port.out.event.InboxEventRepository
import com.gijun.mainserver.domain.event.model.InboxStatus
import com.gijun.mainserver.domain.event.model.stock.StockReductionRequestedEvent
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Component
class InboxEventProcessor(
    private val inboxEventRepository: InboxEventRepository,
    private val adjustProductStockUseCase: AdjustProductStockUseCase,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val BATCH_SIZE = 100
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    fun processPendingEvents() {
        try {
            val pendingEvents = inboxEventRepository.findByStatusOrderByCreatedAtAsc(
                InboxStatus.PENDING,
                PageRequest.of(0, BATCH_SIZE)
            )

            if (pendingEvents.isEmpty()) {
                return
            }

            logger.info("Processing {} pending inbox events", pendingEvents.size)

            pendingEvents.forEach { event ->
                try {
                    val processingEvent = event.markAsProcessing()
                    inboxEventRepository.save(processingEvent)

                    when (event.eventType) {
                        "STOCK_REDUCTION_REQUESTED" -> processStockReductionEvent(event.eventData)
                        else -> {
                            logger.warn("Unknown event type: {}", event.eventType)
                            return@forEach
                        }
                    }

                    val completedEvent = event.markAsCompleted()
                    inboxEventRepository.save(completedEvent)

                    logger.debug("Successfully processed event: eventId={}", event.eventId)
                } catch (e: Exception) {
                    logger.error("Failed to process event: eventId={}", event.eventId, e)
                    val failedEvent = event.markAsFailed()
                    inboxEventRepository.save(failedEvent)
                }
            }
        } catch (e: Exception) {
            logger.error("Error in inbox event processor", e)
        }
    }

    private fun processStockReductionEvent(eventData: String) {
        val stockEvent = objectMapper.readValue(eventData, StockReductionRequestedEvent::class.java)

        stockEvent.soldItems.forEach { soldItem ->
            val command = AdjustProductStockCommand(
                productId = soldItem.productId,
                containerId = stockEvent.storeId,
                adjustmentType = StockAdjustmentType.DECREASE,
                unitQty = soldItem.soldQuantity,
                usageQty = BigDecimal.ZERO
            )

            adjustProductStockUseCase.adjustProductStockExecute(command)
            logger.info(
                "Stock reduced for productId={}, containerId={}, quantity={}",
                soldItem.productId, stockEvent.storeId, soldItem.soldQuantity
            )
        }
    }
}