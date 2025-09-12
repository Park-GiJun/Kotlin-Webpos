# Kotlin WebPOS - Microservices Architecture Documentation

## 🏗️ 프로젝트 개요

**Kotlin WebPOS**는 멀티 스토어 관리 시스템으로, 다음의 현대적 아키텍처 패턴을 적용한 MSA(Microservices Architecture) 기반 프로젝트입니다.

### 핵심 아키텍처 패턴
- **🔸 Hexagonal Architecture (Ports and Adapters)**
- **🔸 CQRS Pattern (Command Query Responsibility Segregation)**
- **🔸 Event-Driven MSA Communication**
- **🔸 Domain-Driven Design (DDD)**

### 비즈니스 도메인 계층구조
```
HQ (본사) → Store (매장) → POS (포스 단말)
```

---

## 🏛️ Microservices Architecture Overview

### 서비스 구성도
```
┌─────────────────┐    ┌─────────────────┐
│   Main Server   │◄──►│   POS Server    │
│   (Port 8080)   │    │   (Port 8081)   │
│                 │    │                 │
│ • HQ 관리       │    │ • POS 관리      │
│ • Store 관리    │    │ • 판매 처리     │
│ • Product 관리  │    │ • 재고 조회     │
└─────────────────┘    └─────────────────┘
         │                       │
         └───────┬───────────────┘
                 ▼
         ┌─────────────────┐
         │  Shared Infra   │
         │                 │
         │ • Redis Cache   │
         │ • Kafka MQ      │
         │ • MySQL DBs     │
         │ • Service Disco │
         └─────────────────┘
```

### 각 서비스별 특징

#### 🟢 Main Server
- **목적**: 조직 관리 및 상품 마스터 관리
- **데이터베이스**: `main_server` DB (Port 3306)
- **주요 도메인**: HQ, Store, Product
- **아키텍처**: Hexagonal + CQRS + DDD

#### 🔵 POS Server  
- **목적**: POS 단말 관리 및 판매 처리
- **데이터베이스**: `pos_server` DB (Port 3307)
- **주요 도메인**: POS, Sales, Transaction
- **아키텍처**: Hexagonal + CQRS + DDD

---

## 🔷 Hexagonal Architecture (포트와 어댑터)

### 아키텍처 구조
```
      ┌─────────────────────────────────────┐
      │          Infrastructure             │
      │  ┌─────────────┐ ┌─────────────┐   │
      │  │ Web Adapter │ │ Persistence │   │
      │  │   (REST)    │ │  Adapter    │   │
      │  └─────────────┘ └─────────────┘   │
      └─────────┬─────────────────┬─────────┘
                │                 │
      ┌─────────▼─────────────────▼─────────┐
      │           Application               │
      │  ┌───────────┐   ┌───────────┐     │
      │  │ Use Cases │◄─►│  Handlers │     │
      │  │  (Ports)  │   │ (Command/ │     │
      │  │           │   │  Query)   │     │
      │  └───────────┘   └───────────┘     │
      └─────────┬─────────────────┬─────────┘
                │                 │
      ┌─────────▼─────────────────▼─────────┐
      │            Domain                   │
      │  ┌─────────────┐ ┌─────────────┐   │
      │  │  Entities   │ │   Value     │   │
      │  │             │ │  Objects    │   │
      │  └─────────────┘ └─────────────┘   │
      └─────────────────────────────────────┘
```

### 디렉토리 구조
```
src/main/kotlin/com/gijun/{service}/
├── application/              # 애플리케이션 레이어
│   ├── dto/
│   │   ├── command/         # CQRS Commands
│   │   ├── query/          # CQRS Queries
│   │   └── result/         # Result DTOs
│   ├── handler/
│   │   ├── command/        # Command Handlers
│   │   └── query/          # Query Handlers
│   ├── mapper/             # DTO ↔ Domain 매핑
│   └── port/
│       ├── in/             # Inbound Ports (Use Cases)
│       └── out/            # Outbound Ports (Repository Interfaces)
├── domain/                  # 도메인 레이어
│   ├── common/             # 공통 Value Objects
│   │   └── vo/            # Money, Email, PhoneNumber 등
│   ├── {domain}/          # 비즈니스 도메인
│   │   ├── model/         # Domain Entities
│   │   └── vo/           # Domain-specific Value Objects
└── infrastructure/          # 인프라스트럭처 레이어
    └── adapter/
        ├── in/web/         # REST Controllers
        └── out/persistence/ # JPA Repository Implementations
```

### 주요 원칙
1. **의존성 역전**: 도메인은 외부 레이어에 의존하지 않음
2. **포트를 통한 통신**: 모든 외부 통신은 포트 인터페이스를 통해 처리
3. **어댑터 패턴**: 외부 시스템과의 통신은 어댑터가 담당

---

## ⚡ CQRS Pattern Implementation

### CQRS 개념
**Command Query Responsibility Segregation** - 명령과 조회의 책임을 분리하는 패턴

### 명령 (Commands) vs 조회 (Queries)
```
📝 Commands (쓰기 작업)          📖 Queries (읽기 작업)
├── CreateHqCommand              ├── GetHqByIdQuery
├── UpdateHqCommand              ├── GetAllHqQuery
├── DeleteHqCommand              ├── GetHqNameQuery
└── ...                          └── IsHqExistByNameQuery
```

### CQRS 플로우
```
Client Request
     │
     ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Web Adapter │───►│   Handler   │───►│ Repository  │
│ (Controller)│    │ (Command/   │    │ (Command/   │
│             │    │  Query)     │    │  Query)     │
└─────────────┘    └─────────────┘    └─────────────┘
     │                    │                   │
     ▼                    ▼                   ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Result    │◄───│   Domain    │◄───│  Database   │
│   DTO       │    │   Entity    │    │             │
└─────────────┘    └─────────────┘    └─────────────┘
```

### 장점
- **성능 최적화**: 읽기/쓰기 작업의 독립적 최적화 가능
- **확장성**: 각 작업에 맞는 별도 최적화 전략 적용
- **복잡성 관리**: 비즈니스 로직의 명확한 분리

### 구현 예시
```kotlin
// Command
data class CreateHqCommand(
    val name: String,
    val representative: String,
    val address: Address,
    val email: Email?,
    val phoneNumber: PhoneNumber?
)

// Query
data class GetHqByIdQuery(val hqId: Long)

// Command Handler
@Component
class HqCommandHandler(
    private val hqCommandRepository: HqCommandRepository
) : CreateHqUseCase {
    override fun handle(command: CreateHqCommand): CreateHqResult {
        // 비즈니스 로직 처리
        val hq = Hq.create(command)
        hqCommandRepository.save(hq)
        return CreateHqResult.from(hq)
    }
}

// Query Handler
@Component
class HqQueryHandler(
    private val hqQueryRepository: HqQueryRepository
) : GetHqUseCase {
    override fun handle(query: GetHqByIdQuery): HqResult? {
        return hqQueryRepository.findById(query.hqId)
            ?.let { HqResult.from(it) }
    }
}
```

---

## 🔄 Event-Driven MSA Communication

### 이벤트 기반 통신 아키텍처
```
Main Server                    POS Server
     │                             │
     ▼                             ▼
┌─────────┐ publish  ┌─────────┐ consume ┌─────────┐
│ Domain  │────────►│  Kafka  │◄────────│ Event   │
│ Events  │         │ Topics  │         │Listener │
└─────────┘         └─────────┘         └─────────┘
```

### 주요 이벤트 타입
```
📢 조직 관련 이벤트
├── HqCreatedEvent
├── StoreCreatedEvent
├── PosCreatedEvent
└── OrganizationUpdatedEvent

📢 상품 관련 이벤트
├── ProductCreatedEvent
├── ProductUpdatedEvent
├── ProductPriceChangedEvent
└── StockUpdatedEvent

📢 판매 관련 이벤트
├── SaleCompletedEvent
├── PaymentProcessedEvent
└── ReceiptGeneratedEvent
```

### 이벤트 플로우 예시
```
1️⃣ Main Server: 상품 생성
   └─ ProductCreatedEvent 발행

2️⃣ Kafka: 이벤트 라우팅
   └─ Topic: product-events

3️⃣ POS Server: 이벤트 수신
   └─ 로컬 상품 캐시 업데이트
```

### 이벤트 구조
```kotlin
// Base Event
abstract class DomainEvent(
    val eventId: String = UUID.randomUUID().toString(),
    val occurredAt: Instant = Instant.now(),
    val eventType: String,
    val aggregateId: String,
    val version: Long
)

// 구체적인 이벤트
data class ProductCreatedEvent(
    override val aggregateId: String,
    override val version: Long,
    val hqId: Long,
    val productName: String,
    val productCode: String,
    val price: BigDecimal
) : DomainEvent(
    eventType = "ProductCreated",
    aggregateId = aggregateId,
    version = version
)
```

### 이벤트 처리 패턴
```kotlin
// 이벤트 발행
@Component
class ProductEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, DomainEvent>
) {
    fun publishProductCreated(event: ProductCreatedEvent) {
        kafkaTemplate.send("product-events", event.aggregateId, event)
    }
}

// 이벤트 수신
@Component
class ProductEventListener {
    @KafkaListener(topics = ["product-events"])
    fun handleProductCreated(event: ProductCreatedEvent) {
        // POS Server에서 상품 정보 동기화
        productSyncService.syncProduct(event)
    }
}
```

### 이벤트 기반 통신의 장점
- **느슨한 결합**: 서비스 간 직접적인 의존성 제거
- **확장성**: 새로운 서비스 추가 시 기존 서비스 수정 불필요
- **복원력**: 한 서비스의 장애가 전체 시스템에 영향을 주지 않음
- **데이터 일관성**: 최종 일관성(Eventually Consistent) 보장

---

## 🛠️ Development Guidelines

### 개발 원칙
1. **Domain First**: 도메인 모델을 먼저 설계하고 인프라 구조 결정
2. **Event Sourcing**: 중요한 비즈니스 변경사항은 이벤트로 기록
3. **Idempotency**: 모든 명령은 멱등성을 보장해야 함
4. **Backward Compatibility**: 이벤트 스키마 변경 시 하위 호환성 유지

### 코딩 컨벤션
```kotlin
// 1. Value Objects 활용
data class Money(val amount: BigDecimal) {
    init {
        require(amount >= BigDecimal.ZERO) { "금액은 0 이상이어야 합니다" }
    }
}

// 2. Rich Domain Model
class Product private constructor(
    val id: ProductId?,
    val hqId: HqId,
    val productName: String,
    val productCode: ProductCode,
    val price: Money
) {
    companion object {
        fun create(command: CreateProductCommand): Product {
            // 비즈니스 규칙 검증
            return Product(...)
        }
    }
    
    fun changePrice(newPrice: Money): ProductPriceChangedEvent {
        // 가격 변경 로직
        return ProductPriceChangedEvent(...)
    }
}

// 3. 명확한 인터페이스 분리
interface ProductCommandRepository {
    fun save(product: Product): Product
    fun delete(productId: ProductId)
}

interface ProductQueryRepository {
    fun findById(productId: ProductId): Product?
    fun findByHqId(hqId: HqId): List<Product>
}
```

### 테스트 전략
- **Unit Tests**: 도메인 로직 테스트
- **Integration Tests**: 어댑터 레이어 테스트  
- **Contract Tests**: 서비스 간 API 계약 테스트
- **End-to-End Tests**: 전체 플로우 테스트

---

## 🚀 Quick Start Guide

### 1. 인프라 구성
```bash
# 인프라 서비스만 실행 (개발용)
./gradlew startMsaInfrastructure

# 전체 MSA 시스템 실행
./gradlew startAllServices
```

### 2. 개별 서비스 실행
```bash
# Main Server
cd main-server
./gradlew bootRun

# POS Server  
cd pos-server
./gradlew bootRun
```

### 3. 서비스 상태 확인
- Main Server: http://localhost:8080/actuator/health
- POS Server: http://localhost:8081/actuator/health
- Consul UI: http://localhost:8500
- Kafka UI: http://localhost:9092

---

## 📊 Monitoring & Observability

### 헬스체크 엔드포인트
- `/actuator/health`: 서비스 상태
- `/actuator/metrics`: 메트릭 정보
- `/actuator/info`: 서비스 정보

### 로그 레벨 설정
```yaml
logging:
  level:
    com.gijun: DEBUG
    org.springframework.kafka: INFO
    org.hibernate: WARN
```

### 분산 트레이싱 (향후 계획)
- Spring Cloud Sleuth
- Zipkin 또는 Jaeger 연동

---

## 🔮 Future Enhancements

### 계획된 기능들
- [ ] **API Gateway**: 단일 진입점 및 라우팅
- [ ] **Service Discovery**: Eureka 또는 Consul 연동
- [ ] **Circuit Breaker**: Resilience4j 적용
- [ ] **Event Sourcing**: 이벤트 스토어 구현
- [ ] **CQRS Read Model**: 별도 조회 DB 분리
- [ ] **Distributed Saga**: 분산 트랜잭션 관리

### 확장 가능한 서비스들
- **User Service**: 사용자 인증 및 권한 관리
- **Notification Service**: 알림 처리
- **Analytics Service**: 분석 및 리포팅
- **Payment Service**: 결제 처리

---

이 문서는 **Kotlin WebPOS** 프로젝트의 아키텍처 가이드라인을 제시하며, 
모든 개발자가 일관된 방향으로 개발할 수 있도록 돕는 것이 목적입니다.

*Last updated: 2025-01-14*