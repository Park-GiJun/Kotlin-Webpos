package com.gijun.mainserver.application.scheduler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gijun.mainserver.application.port.out.event.InboxEventRepository
import com.gijun.mainserver.application.port.out.product.productStock.ProductStockQueryRepository
import com.gijun.mainserver.domain.event.model.InboxEvent
import com.gijun.mainserver.domain.event.model.InboxStatus
import com.gijun.mainserver.domain.event.model.stock.SoldItem
import com.gijun.mainserver.domain.event.model.stock.StockReductionRequestedEvent
import com.gijun.mainserver.domain.product.productStock.model.ProductStock
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class InboxEventProcessorTest {

    @Autowired
    private lateinit var inboxEventProcessor: InboxEventProcessor

    @Autowired
    private lateinit var inboxEventRepository: InboxEventRepository

    @Autowired
    private lateinit var productStockQueryRepository: ProductStockQueryRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `재고 감소 이벤트 처리 성공 - PENDING 상태의 이벤트가 처리되고 재고가 감소한다`() {
        val eventId = UUID.randomUUID().toString()
        val salesId = UUID.randomUUID().toString()
        val productId = 1L
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
                    productCode = "BEV-AME-T",
                    soldQuantity = soldQuantity,
                    unitPrice = BigDecimal("4500.00")
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
        assertEquals(
            initialStock.unitQty - soldQuantity,
            updatedStock.unitQty,
            "재고가 판매 수량만큼 감소해야 합니다"
        )
    }

    @Test
    fun `여러 상품 재고 감소 이벤트 처리 성공`() {
        val eventId = UUID.randomUUID().toString()
        val salesId = UUID.randomUUID().toString()
        val containerId = 2L

        val product1Id = 6L
        val product2Id = 7L

        val initialStock1 = productStockQueryRepository.findByProductIdAndContainerId(product1Id, containerId)
        val initialStock2 = productStockQueryRepository.findByProductIdAndContainerId(product2Id, containerId)

        assertNotNull(initialStock1, "상품1 초기 재고가 존재해야 합니다")
        assertNotNull(initialStock2, "상품2 초기 재고가 존재해야 합니다")

        val soldQuantity1 = BigDecimal("2.00")
        val soldQuantity2 = BigDecimal("3.00")

        val stockEvent = StockReductionRequestedEvent(
            eventId = eventId,
            salesId = salesId,
            storeId = containerId,
            posId = 1L,
            soldItems = listOf(
                SoldItem(
                    productId = product1Id,
                    productCode = "FOD-CHZ-001",
                    soldQuantity = soldQuantity1,
                    unitPrice = BigDecimal("6300.00")
                ),
                SoldItem(
                    productId = product2Id,
                    productCode = "FOD-CRO-001",
                    soldQuantity = soldQuantity2,
                    unitPrice = BigDecimal("4000.00")
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

        val updatedStock1 = productStockQueryRepository.findByProductIdAndContainerId(product1Id, containerId)
        val updatedStock2 = productStockQueryRepository.findByProductIdAndContainerId(product2Id, containerId)

        assertNotNull(updatedStock1, "상품1 업데이트된 재고가 존재해야 합니다")
        assertNotNull(updatedStock2, "상품2 업데이트된 재고가 존재해야 합니다")

        assertEquals(
            initialStock1.unitQty - soldQuantity1,
            updatedStock1.unitQty,
            "상품1 재고가 판매 수량만큼 감소해야 합니다"
        )
        assertEquals(
            initialStock2.unitQty - soldQuantity2,
            updatedStock2.unitQty,
            "상품2 재고가 판매 수량만큼 감소해야 합니다"
        )
    }

    @Test
    fun `PENDING 상태가 아닌 이벤트는 처리하지 않는다`() {
        val eventId = UUID.randomUUID().toString()
        val salesId = UUID.randomUUID().toString()
        val productId = 1L
        val containerId = 2L

        val initialStock = productStockQueryRepository.findByProductIdAndContainerId(productId, containerId)
        assertNotNull(initialStock, "초기 재고가 존재해야 합니다")

        val stockEvent = StockReductionRequestedEvent(
            eventId = eventId,
            salesId = salesId,
            storeId = containerId,
            posId = 1L,
            soldItems = listOf(
                SoldItem(
                    productId = productId,
                    productCode = "BEV-AME-T",
                    soldQuantity = BigDecimal("1.00"),
                    unitPrice = BigDecimal("4500.00")
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
            status = InboxStatus.COMPLETED,
            retryCount = 0
        )

        inboxEventRepository.save(inboxEvent)

        inboxEventProcessor.processPendingEvents()

        val unchangedStock = productStockQueryRepository.findByProductIdAndContainerId(productId, containerId)
        assertNotNull(unchangedStock, "재고가 존재해야 합니다")
        assertEquals(
            initialStock.unitQty,
            unchangedStock.unitQty,
            "COMPLETED 상태의 이벤트는 처리하지 않으므로 재고가 변경되지 않아야 합니다"
        )
    }

    @Test
    fun `여러 PENDING 이벤트를 순차적으로 처리한다`() {
        val containerId = 2L
        val productId = 1L

        val initialStock = productStockQueryRepository.findByProductIdAndContainerId(productId, containerId)
        assertNotNull(initialStock, "초기 재고가 존재해야 합니다")

        val event1Id = UUID.randomUUID().toString()
        val event2Id = UUID.randomUUID().toString()
        val event3Id = UUID.randomUUID().toString()

        listOf(
            event1Id to BigDecimal("1.00"),
            event2Id to BigDecimal("2.00"),
            event3Id to BigDecimal("1.00")
        ).forEach { (eventId, quantity) ->
            val stockEvent = StockReductionRequestedEvent(
                eventId = eventId,
                salesId = UUID.randomUUID().toString(),
                storeId = containerId,
                posId = 1L,
                soldItems = listOf(
                    SoldItem(
                        productId = productId,
                        productCode = "BEV-AME-T",
                        soldQuantity = quantity,
                        unitPrice = BigDecimal("4500.00")
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
                aggregateId = UUID.randomUUID().toString(),
                eventData = eventData,
                status = InboxStatus.PENDING,
                retryCount = 0
            )

            inboxEventRepository.save(inboxEvent)
        }

        inboxEventProcessor.processPendingEvents()

        val event1 = inboxEventRepository.findByEventId(event1Id)
        val event2 = inboxEventRepository.findByEventId(event2Id)
        val event3 = inboxEventRepository.findByEventId(event3Id)

        assertNotNull(event1, "이벤트1이 존재해야 합니다")
        assertNotNull(event2, "이벤트2가 존재해야 합니다")
        assertNotNull(event3, "이벤트3이 존재해야 합니다")

        assertEquals(InboxStatus.COMPLETED, event1.status, "이벤트1 상태가 COMPLETED여야 합니다")
        assertEquals(InboxStatus.COMPLETED, event2.status, "이벤트2 상태가 COMPLETED여야 합니다")
        assertEquals(InboxStatus.COMPLETED, event3.status, "이벤트3 상태가 COMPLETED여야 합니다")

        val finalStock = productStockQueryRepository.findByProductIdAndContainerId(productId, containerId)
        assertNotNull(finalStock, "최종 재고가 존재해야 합니다")

        val totalSoldQuantity = BigDecimal("1.00") + BigDecimal("2.00") + BigDecimal("1.00")
        assertEquals(
            initialStock.unitQty - totalSoldQuantity,
            finalStock.unitQty,
            "재고가 모든 판매 수량의 합만큼 감소해야 합니다"
        )
    }
}