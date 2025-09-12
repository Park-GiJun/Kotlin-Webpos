# Kotlin WebPOS - 이벤트 카탈로그

## 📋 이벤트 기반 MSA 통신 카탈로그

이 문서는 **Kotlin WebPOS** 시스템 내에서 발생하는 모든 도메인 이벤트들을 정리한 카탈로그입니다.
각 이벤트는 서비스 간 비동기 통신을 통해 데이터 일관성과 시스템 확장성을 보장합니다.

---

## 🏛️ 조직(Organization) 도메인 이벤트

### 1. HQ (본사) 관련 이벤트

#### 🟢 HqCreatedEvent
- **발행자**: Main Server
- **구독자**: POS Server, Analytics Service (향후)
- **토픽**: `organization-events`
- **라우팅 키**: `hq.created`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "eventType": "HqCreated",
  "aggregateId": "hq-1",
  "version": 1,
  "occurredAt": "2025-01-14T10:30:00.000Z",
  "payload": {
    "hqId": 1,
    "name": "메인 본사",
    "representative": "홍길동",
    "address": "서울시 강남구 테헤란로 123",
    "email": "main@company.com",
    "phoneNumber": "02-1234-5678"
  }
}
```

**비즈니스 의미**: 새로운 본사가 생성되었을 때 발생
**처리 로직**: POS Server에서 본사 정보를 동기화

#### 🔄 HqUpdatedEvent
- **발행자**: Main Server
- **구독자**: POS Server, Analytics Service (향후)
- **토픽**: `organization-events`
- **라우팅 키**: `hq.updated`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440001",
  "eventType": "HqUpdated",
  "aggregateId": "hq-1",
  "version": 2,
  "occurredAt": "2025-01-14T10:35:00.000Z",
  "payload": {
    "hqId": 1,
    "name": "메인 본사",
    "representative": "김업데이트",
    "address": "서울시 강남구 테헤란로 456",
    "email": "updated@company.com",
    "phoneNumber": "02-5678-9012",
    "previousVersion": 1
  }
}
```

**비즈니스 의미**: 본사 정보가 수정되었을 때 발생
**처리 로직**: POS Server에서 본사 정보를 업데이트

#### 🔴 HqDeletedEvent
- **발행자**: Main Server
- **구독자**: POS Server, Analytics Service (향후)
- **토픽**: `organization-events`
- **라우팅 키**: `hq.deleted`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440002",
  "eventType": "HqDeleted",
  "aggregateId": "hq-1",
  "version": 3,
  "occurredAt": "2025-01-14T10:40:00.000Z",
  "payload": {
    "hqId": 1,
    "name": "메인 본사",
    "deletedBy": "admin",
    "deletedReason": "사업 종료"
  }
}
```

**비즈니스 의미**: 본사가 삭제(비활성화)되었을 때 발생
**처리 로직**: POS Server에서 해당 본사와 관련된 모든 데이터를 비활성화

### 2. Store (매장) 관련 이벤트

#### 🟢 StoreCreatedEvent
- **발행자**: Main Server
- **구독자**: POS Server, Analytics Service (향후)
- **토픽**: `organization-events`
- **라우팅 키**: `store.created`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440003",
  "eventType": "StoreCreated",
  "aggregateId": "store-1",
  "version": 1,
  "occurredAt": "2025-01-14T11:00:00.000Z",
  "payload": {
    "storeId": 1,
    "hqId": 1,
    "name": "강남점",
    "representative": "이점장",
    "address": "서울시 강남구 논현로 789",
    "email": "gangnam@company.com",
    "phoneNumber": "02-9876-5432"
  }
}
```

**비즈니스 의미**: 새로운 매장이 생성되었을 때 발생
**처리 로직**: POS Server에서 매장 정보를 동기화

#### 🔄 StoreUpdatedEvent
- **발행자**: Main Server
- **구독자**: POS Server, Analytics Service (향후)
- **토픽**: `organization-events`
- **라우팅 키**: `store.updated`

#### 🔴 StoreDeletedEvent
- **발행자**: Main Server
- **구독자**: POS Server, Analytics Service (향후)
- **토픽**: `organization-events`
- **라우팅 키**: `store.deleted`

### 3. POS 관련 이벤트

#### 🟢 PosCreatedEvent
- **발행자**: Main Server
- **구독자**: POS Server, Monitoring Service (향후)
- **토픽**: `organization-events`
- **라우팅 키**: `pos.created`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440004",
  "eventType": "PosCreated",
  "aggregateId": "pos-1",
  "version": 1,
  "occurredAt": "2025-01-14T11:30:00.000Z",
  "payload": {
    "posId": 1,
    "hqId": 1,
    "storeId": 1,
    "posNumber": "POS-001",
    "status": "ACTIVE"
  }
}
```

**비즈니스 의미**: 새로운 POS 단말이 등록되었을 때 발생
**처리 로직**: POS Server에서 단말 정보를 등록하고 활성화

#### 🔄 PosStatusChangedEvent
- **발행자**: POS Server 또는 Main Server
- **구독자**: Main Server, Monitoring Service (향후)
- **토픽**: `organization-events`
- **라우팅 키**: `pos.status.changed`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440005",
  "eventType": "PosStatusChanged",
  "aggregateId": "pos-1",
  "version": 2,
  "occurredAt": "2025-01-14T12:00:00.000Z",
  "payload": {
    "posId": 1,
    "previousStatus": "ACTIVE",
    "newStatus": "MAINTENANCE",
    "reason": "정기 점검",
    "changedBy": "system"
  }
}
```

**비즈니스 의미**: POS 단말의 상태가 변경되었을 때 발생
**처리 로직**: 상태 변경에 따른 서비스 제어

---

## 🛍️ 상품(Product) 도메인 이벤트

### 1. Product (상품) 관련 이벤트

#### 🟢 ProductCreatedEvent
- **발행자**: Main Server
- **구독자**: POS Server, Analytics Service (향후)
- **토픽**: `product-events`
- **라우팅 키**: `product.created`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440006",
  "eventType": "ProductCreated",
  "aggregateId": "product-1",
  "version": 1,
  "occurredAt": "2025-01-14T13:00:00.000Z",
  "payload": {
    "productId": 1,
    "hqId": 1,
    "productName": "아메리카노",
    "productCode": "COFFEE-001",
    "supplyAmount": 1500.00,
    "unit": "잔",
    "price": 4500.00,
    "usageUnit": "잔",
    "category": "음료",
    "description": "진한 에스프레소 아메리카노"
  }
}
```

**비즈니스 의미**: 새로운 상품이 등록되었을 때 발생
**처리 로직**: POS Server에서 상품 카탈로그를 업데이트

#### 🔄 ProductUpdatedEvent
- **발행자**: Main Server
- **구독자**: POS Server, Analytics Service (향후)
- **토픽**: `product-events`
- **라우팅 키**: `product.updated`

#### 💰 ProductPriceChangedEvent
- **발행자**: Main Server
- **구독자**: POS Server, Analytics Service (향후)
- **토픽**: `product-events`
- **라우팅 키**: `product.price.changed`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440007",
  "eventType": "ProductPriceChanged",
  "aggregateId": "product-1",
  "version": 2,
  "occurredAt": "2025-01-14T13:30:00.000Z",
  "payload": {
    "productId": 1,
    "productName": "아메리카노",
    "previousPrice": 4500.00,
    "newPrice": 5000.00,
    "effectiveDate": "2025-01-15T00:00:00.000Z",
    "reason": "원가 상승",
    "changedBy": "admin"
  }
}
```

**비즈니스 의미**: 상품 가격이 변경되었을 때 발생
**처리 로직**: POS Server에서 가격 정보를 업데이트하고 기존 가격 이력 보관

#### 🔴 ProductDeletedEvent
- **발행자**: Main Server
- **구독자**: POS Server, Analytics Service (향후)
- **토픽**: `product-events`
- **라우팅 키**: `product.deleted`

### 2. Stock (재고) 관련 이벤트

#### 📦 StockUpdatedEvent
- **발행자**: Main Server, POS Server
- **구독자**: Main Server, POS Server, Analytics Service (향후)
- **토픽**: `inventory-events`
- **라우팅 키**: `stock.updated`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440008",
  "eventType": "StockUpdated",
  "aggregateId": "stock-1",
  "version": 1,
  "occurredAt": "2025-01-14T14:00:00.000Z",
  "payload": {
    "stockId": 1,
    "productId": 1,
    "hqId": 1,
    "storeId": 1,
    "previousQuantity": 100.0,
    "newQuantity": 95.0,
    "changeAmount": -5.0,
    "changeType": "SALE",
    "reason": "판매로 인한 재고 감소",
    "transactionId": "TXN-001"
  }
}
```

**비즈니스 의미**: 재고 수량이 변경되었을 때 발생
**처리 로직**: 실시간 재고 동기화

#### ⚠️ LowStockAlertEvent
- **발행자**: Main Server, POS Server
- **구독자**: Notification Service (향후), Analytics Service (향후)
- **토픽**: `inventory-events`
- **라우팅 키**: `stock.low.alert`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440009",
  "eventType": "LowStockAlert",
  "aggregateId": "stock-1",
  "version": 1,
  "occurredAt": "2025-01-14T14:05:00.000Z",
  "payload": {
    "stockId": 1,
    "productId": 1,
    "productName": "아메리카노",
    "hqId": 1,
    "storeId": 1,
    "storeName": "강남점",
    "currentQuantity": 5.0,
    "minimumThreshold": 10.0,
    "urgencyLevel": "MEDIUM"
  }
}
```

**비즈니스 의미**: 재고가 최소 임계값 이하로 떨어졌을 때 발생
**처리 로직**: 알림 발송 및 자동 발주 검토

---

## 💳 판매(Sales) 도메인 이벤트

### 1. Sale (판매) 관련 이벤트

#### 🛒 SaleStartedEvent
- **발행자**: POS Server
- **구독자**: Main Server, Analytics Service (향후)
- **토픽**: `sales-events`
- **라우팅 키**: `sale.started`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440010",
  "eventType": "SaleStarted",
  "aggregateId": "sale-1",
  "version": 1,
  "occurredAt": "2025-01-14T15:00:00.000Z",
  "payload": {
    "saleId": 1,
    "posId": 1,
    "storeId": 1,
    "hqId": 1,
    "cashierId": "CASHIER-001",
    "customerCount": 1,
    "saleType": "DINE_IN"
  }
}
```

**비즈니스 의미**: 새로운 판매 트랜잭션이 시작되었을 때 발생
**처리 로직**: 판매 세션 생성 및 추적 시작

#### 🛍️ SaleItemAddedEvent
- **발행자**: POS Server
- **구독자**: Main Server, Analytics Service (향후)
- **토픽**: `sales-events`
- **라우팅 키**: `sale.item.added`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440011",
  "eventType": "SaleItemAdded",
  "aggregateId": "sale-1",
  "version": 2,
  "occurredAt": "2025-01-14T15:01:00.000Z",
  "payload": {
    "saleId": 1,
    "productId": 1,
    "productName": "아메리카노",
    "productCode": "COFFEE-001",
    "quantity": 2,
    "unitPrice": 5000.00,
    "totalPrice": 10000.00,
    "options": [
      {
        "name": "샷 추가",
        "price": 500.00,
        "quantity": 1
      }
    ]
  }
}
```

**비즈니스 의미**: 판매 항목이 추가되었을 때 발생
**처리 로직**: 주문 내역 업데이트 및 재고 예약

#### ✅ SaleCompletedEvent
- **발행자**: POS Server
- **구독자**: Main Server, Analytics Service (향후)
- **토픽**: `sales-events`
- **라우팅 키**: `sale.completed`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440012",
  "eventType": "SaleCompleted",
  "aggregateId": "sale-1",
  "version": 5,
  "occurredAt": "2025-01-14T15:10:00.000Z",
  "payload": {
    "saleId": 1,
    "posId": 1,
    "storeId": 1,
    "hqId": 1,
    "cashierId": "CASHIER-001",
    "totalAmount": 10500.00,
    "discountAmount": 0.00,
    "taxAmount": 955.00,
    "finalAmount": 10500.00,
    "paymentMethod": "CARD",
    "completedAt": "2025-01-14T15:10:00.000Z",
    "receiptNumber": "RCP-20250114-001",
    "items": [
      {
        "productId": 1,
        "productName": "아메리카노",
        "quantity": 2,
        "unitPrice": 5000.00,
        "totalPrice": 10000.00
      }
    ]
  }
}
```

**비즈니스 의미**: 판매가 완료되었을 때 발생
**처리 로직**: 재고 차감, 매출 집계, 영수증 생성

### 2. Payment (결제) 관련 이벤트

#### 💳 PaymentProcessedEvent
- **발행자**: POS Server
- **구독자**: Main Server, Payment Service (향후)
- **토픽**: `payment-events`
- **라우팅 키**: `payment.processed`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440013",
  "eventType": "PaymentProcessed",
  "aggregateId": "payment-1",
  "version": 1,
  "occurredAt": "2025-01-14T15:08:00.000Z",
  "payload": {
    "paymentId": 1,
    "saleId": 1,
    "paymentMethod": "CARD",
    "cardType": "CREDIT",
    "amount": 10500.00,
    "approvalNumber": "12345678",
    "merchantId": "MERCHANT-001",
    "transactionId": "TXN-12345",
    "processedAt": "2025-01-14T15:08:00.000Z"
  }
}
```

**비즈니스 의미**: 결제가 정상적으로 처리되었을 때 발생
**처리 로직**: 결제 내역 기록 및 정산 데이터 생성

#### ❌ PaymentFailedEvent
- **발행자**: POS Server
- **구독자**: Main Server, Analytics Service (향후)
- **토픽**: `payment-events`
- **라우팅 키**: `payment.failed`

### 3. Receipt (영수증) 관련 이벤트

#### 🧾 ReceiptGeneratedEvent
- **발행자**: POS Server
- **구독자**: Main Server, Document Service (향후)
- **토픽**: `receipt-events`
- **라우팅 키**: `receipt.generated`

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440014",
  "eventType": "ReceiptGenerated",
  "aggregateId": "receipt-1",
  "version": 1,
  "occurredAt": "2025-01-14T15:10:30.000Z",
  "payload": {
    "receiptId": 1,
    "saleId": 1,
    "receiptNumber": "RCP-20250114-001",
    "receiptType": "SALE",
    "storeName": "강남점",
    "storeAddress": "서울시 강남구 논현로 789",
    "posNumber": "POS-001",
    "cashierName": "김계산",
    "printedAt": "2025-01-14T15:10:30.000Z",
    "digitalReceiptUrl": "https://receipt.company.com/RCP-20250114-001"
  }
}
```

**비즈니스 의미**: 영수증이 생성되었을 때 발생
**처리 로직**: 영수증 보관 및 디지털 영수증 서비스 연동

---

## 🔄 이벤트 처리 패턴

### 1. 이벤트 발행 패턴
```kotlin
// 1. Transactional Outbox Pattern
@Component
class EventPublisher {
    fun publishWithOutbox(domainEvent: DomainEvent) {
        // 1. 이벤트를 Outbox 테이블에 저장
        eventOutboxRepository.save(EventOutbox.from(domainEvent))
        
        // 2. 별도 프로세스에서 Outbox를 폴링하여 Kafka로 발행
        // (At-least-once delivery 보장)
    }
}

// 2. 즉시 발행 패턴 (Simple)
@Component  
class SimpleEventPublisher {
    fun publish(domainEvent: DomainEvent) {
        kafkaTemplate.send(
            domainEvent.getTopicName(),
            domainEvent.getPartitionKey(),
            domainEvent
        )
    }
}
```

### 2. 이벤트 구독 패턴
```kotlin
// 1. 멱등성 보장 패턴
@KafkaListener(topics = ["product-events"])
fun handleProductEvent(
    @Payload event: ProductCreatedEvent,
    @Header(KafkaHeaders.RECEIVED_KEY) key: String
) {
    val eventId = event.eventId
    
    // 중복 처리 방지
    if (processedEventRepository.existsByEventId(eventId)) {
        logger.info("Event already processed: {}", eventId)
        return
    }
    
    try {
        // 비즈니스 로직 처리
        productSyncService.syncProduct(event)
        
        // 처리 완료 기록
        processedEventRepository.save(ProcessedEvent(eventId))
        
    } catch (e: Exception) {
        logger.error("Failed to process event: {}", eventId, e)
        throw e // 재시도를 위해 예외 재발생
    }
}

// 2. Saga Pattern (분산 트랜잭션)
@Component
class OrderSaga {
    @SagaOrchestrationStart
    fun handleOrderPlaced(event: OrderPlacedEvent) {
        // 1단계: 재고 예약
        commandGateway.send(ReserveStockCommand(event.productId, event.quantity))
    }
    
    @SagaAssociationProperty("orderId")
    fun handleStockReserved(event: StockReservedEvent) {
        // 2단계: 결제 처리
        commandGateway.send(ProcessPaymentCommand(event.orderId, event.amount))
    }
    
    @SagaAssociationProperty("orderId")
    fun handlePaymentFailed(event: PaymentFailedEvent) {
        // 보상 트랜잭션: 재고 예약 취소
        commandGateway.send(CancelStockReservationCommand(event.orderId))
    }
}
```

### 3. 이벤트 버전 관리
```kotlin
// 이벤트 스키마 진화
data class ProductCreatedEventV1(
    override val eventId: String,
    override val aggregateId: String,
    val productId: Long,
    val productName: String,
    val price: BigDecimal
) : DomainEvent(eventType = "ProductCreated", version = 1)

data class ProductCreatedEventV2(
    override val eventId: String,
    override val aggregateId: String,
    val productId: Long,
    val productName: String,
    val price: BigDecimal,
    val category: String, // 새로운 필드 추가
    val description: String? = null // 선택적 필드
) : DomainEvent(eventType = "ProductCreated", version = 2)

// 버전별 처리
@Component
class ProductEventHandler {
    @KafkaListener(topics = ["product-events"])
    fun handleProductCreated(
        @Payload event: JsonNode,
        @Header("eventVersion") version: Int?
    ) {
        when (version ?: 1) {
            1 -> handleV1(objectMapper.treeToValue(event, ProductCreatedEventV1::class.java))
            2 -> handleV2(objectMapper.treeToValue(event, ProductCreatedEventV2::class.java))
            else -> throw UnsupportedEventVersionException("Unsupported version: $version")
        }
    }
}
```

---

## 📊 이벤트 모니터링

### 1. 이벤트 메트릭
```kotlin
@Component
class EventMetrics {
    private val eventPublishedCounter = Counter.builder("events.published.total")
        .description("Total events published")
        .tag("service", "main-server")
        .register(Metrics.globalRegistry)
    
    private val eventProcessedCounter = Counter.builder("events.processed.total")
        .description("Total events processed")
        .tag("service", "pos-server")
        .register(Metrics.globalRegistry)
    
    private val eventProcessingTimer = Timer.builder("events.processing.duration")
        .description("Event processing duration")
        .register(Metrics.globalRegistry)
    
    fun recordEventPublished(eventType: String) {
        eventPublishedCounter.tag("event_type", eventType).increment()
    }
    
    fun recordEventProcessed(eventType: String, duration: Duration) {
        eventProcessedCounter.tag("event_type", eventType).increment()
        eventProcessingTimer.record(duration)
    }
}
```

### 2. 이벤트 추적
```kotlin
// 분산 추적을 위한 Correlation ID
@Component
class EventTracing {
    fun publishWithTrace(event: DomainEvent) {
        val traceId = MDC.get("traceId") ?: UUID.randomUUID().toString()
        val spanId = UUID.randomUUID().toString()
        
        val headers = mapOf(
            "traceId" to traceId,
            "spanId" to spanId,
            "timestamp" to Instant.now().toString()
        )
        
        kafkaTemplate.send(
            event.getTopicName(),
            event.getPartitionKey(),
            event
        ).addCallback(
            { logger.info("Event published successfully: traceId={}, eventId={}", traceId, event.eventId) },
            { error -> logger.error("Failed to publish event: traceId={}, eventId={}", traceId, event.eventId, error) }
        )
    }
}
```

---

## 🚀 이벤트 기반 아키텍처 Best Practices

### 1. 이벤트 설계 원칙
- **도메인 중심**: 비즈니스 도메인의 중요한 변화만 이벤트로 발행
- **불변성**: 발행된 이벤트는 절대 변경하지 않음
- **자기 서술적**: 이벤트만으로 완전한 정보를 제공
- **버전 호환성**: 스키마 변경 시 하위 호환성 유지

### 2. 성능 고려사항
- **배치 처리**: 대량 이벤트 처리 시 배치 단위로 처리
- **파티셔닝**: 연관된 이벤트는 같은 파티션에 배치
- **압축**: 큰 페이로드는 압축하여 전송
- **필터링**: 불필요한 이벤트는 컨슈머 레벨에서 필터링

### 3. 오류 처리 전략
- **재시도**: 일시적 오류에 대한 자동 재시도
- **DLQ**: 처리 불가능한 이벤트는 Dead Letter Queue로 이동
- **Circuit Breaker**: 연속된 실패 시 일시적 중단
- **보상 트랜잭션**: 실패 시 이전 상태로 복구

---

*이 이벤트 카탈로그는 시스템 진화에 따라 지속적으로 업데이트됩니다.*

*Last updated: 2025-01-14*