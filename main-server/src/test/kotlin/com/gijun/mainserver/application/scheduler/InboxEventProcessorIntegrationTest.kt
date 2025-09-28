package com.gijun.mainserver.application.scheduler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gijun.mainserver.application.port.out.event.InboxEventRepository
import com.gijun.mainserver.application.port.out.product.productStock.ProductStockQueryRepository
import com.gijun.mainserver.domain.event.model.InboxEvent
import com.gijun.mainserver.domain.event.model.InboxStatus
import com.gijun.mainserver.domain.event.model.stock.SoldItem
import com.gijun.mainserver.domain.event.model.stock.StockReductionRequestedEvent
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@ActiveProfiles("test")
class InboxEventProcessorIntegrationTest {

    @Autowired
    private lateinit var inboxEventProcessor: InboxEventProcessor

    @Autowired
    private lateinit var inboxEventRepository: InboxEventRepository

    @Autowired
    private lateinit var productStockQueryRepository: ProductStockQueryRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @AfterEach
    fun cleanup() {
        val allEvents = inboxEventRepository.findAll()
        allEvents.forEach { event ->
            if (event.eventId.startsWith("test-")) {
            }
        }
    }

    @Test
    fun `재고 감소 이벤트 처리 성공`() {
        val eventId = "test-${UUID.randomUUID()}"
        val salesId = UUID.randomUUID().toString()
        val productId = 6L
        val containerId = 2L

        val initialStock = productStockQueryRepository.findByProductIdAndContainerId(productId, containerId)
        assertNotNull(initialStock, "초기 재고가 존재해야 합니다")

        val soldQuantity = BigDecimal("1.00")
        val stockEvent = StockReductionRequestedEvent(
            eventId = eventId,
            salesId = salesId,
            storeId = containerId,
            posId = 1L,
            soldItems = listOf(
                SoldItem(
                    productId = productId,
                    productCode = "FOD-CHZ-001",
                    soldQuantity = soldQuantity,
                    unitPrice = BigDecimal("6300.00")
                )
            ),
            salesDate = LocalDateTime.now()
        )

        val eventData = objectMapper.writeValueAsString(stockEvent)
        val inboxEvent = InboxEvent(
            id = null,
            eventId = eventId,
            eventType = "STOCK_REDUCTION_REQUESTED",
            aggregateType = "SALES",
            aggregateId = salesId,
            eventData = eventData,
            status = InboxStatus.PENDING,
            retryCount = 0
        )

        inboxEventRepository.save(inboxEvent)

        inboxEventProcessor.processPendingEvents()

        val processedEvent = inboxEventRepository.findByEventId(eventId)
        assertNotNull(processedEvent, "처리된 이벤트가 존재해야 합니다")
        assertEquals(InboxStatus.COMPLETED, processedEvent.status, "이벤트 상태가 COMPLETED여야 합니다")

        val updatedStock = productStockQueryRepository.findByProductIdAndContainerId(productId, containerId)
        assertNotNull(updatedStock, "업데이트된 재고가 존재해야 합니다")

        println("Initial stock: ${initialStock.unitQty}, Updated stock: ${updatedStock.unitQty}, Sold: $soldQuantity")
        assertEquals(
            initialStock.unitQty - soldQuantity,
            updatedStock.unitQty,
            "재고가 판매 수량만큼 감소해야 합니다"
        )
    }
}