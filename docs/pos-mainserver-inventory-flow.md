# POS-MainServer 재고 연동 처리 흐름

## 개요
POS Server에서 판매 처리 시 Main Server의 재고를 차감하는 프로세스를 정의합니다.
재고는 HQ(본사) - Store(매장) - Product(상품)의 3단계 계층 구조로 관리되며,
동기 API 호출과 비동기 이벤트 처리를 조합한 하이브리드 방식을 채택합니다.

## 아키텍처 구성

### 시스템 구성요소
- **POS Server**: 매장 판매 시스템
- **Main Server**: 중앙 재고 관리 시스템
- **Redis**: 분산락 및 캐싱
- **Message Broker**: Kafka (이벤트 전달)
- **API Gateway**: REST API 통신

### 재고 계층 구조
```
HQ (본사)
 ├── Container (창고/컨테이너)
 │    └── ProductStock (상품별 재고)
 └── Store (매장)
      └── Product (상품 마스터 데이터)
```
- **HQ**: 본사 조직 정보
- **Container**: 물리적 창고 또는 논리적 컨테이너 단위
- **ProductStock**: 컨테이너별 상품 재고
- **Store**: 매장 정보 (재고와 분리)
- **Product**: 상품 마스터 데이터 (가격, 타입 등)

## 처리 흐름

### 1단계: 주문 생성 및 재고 예약

```
[POS Server]                [Redis]                    [Main Server]
     |                         |                             |
     |---- 1. 재고 확인 API -->|                             |
     |   GET /api/products/stock                            |
     |                         |---- 캐시 확인 ------------->|
     |<------ 재고 정보 -------|                             |
     |                         |                             |
     |---- 2. 재고 예약 API -->|                             |
     |  POST /api/inventory/reserve                         |
     |                         |---- 분산락 획득 ----------->|
     |                         |     (product:lock:{id})    |
     |                         |<--- 락 획득 성공 ----------|
     |                         |                             |
     |                         |---- 재고 예약 처리 -------->|
     |                         |                             |
     |<------ 예약 ID 응답 ----|<--- 예약 완료 + 락 해제 ---|
     |                         |                             |
     |---- 3. 주문 생성 ------>|                             |
     |    (내부 처리)          |                             |
```

**상세 동작**
1. POS에서 판매 시작 시 Main Server의 재고 확인 (Redis 캐시 우선 조회)
2. Redis 분산락을 획득하여 동시성 제어
3. 재고가 충분하면 임시 예약 (5분 TTL)
4. 예약 성공 시 분산락 해제 및 POS에서 주문 생성 진행

### 2단계: 결제 처리 및 재고 확정

```
[POS Server]                    [Kafka]                    [Main Server]
     |                             |                             |
     |---- 4. 결제 처리 ----------->|                             |
     |                             |                             |
     |---- 5. OrderCompleted ----->|                             |
     |         Event 발행          |                             |
     |                             |---- Event 전달 ------------>|
     |                             |                             |
     |                             |                    6. 재고 차감 처리
     |                             |                       - 예약 확정
     |                             |                       - 실제 재고 감소
     |                             |                             |
     |                             |<--- StockUpdated Event -----|
     |<---- Event 수신 ------------|                             |
     |                             |                             |
```

**상세 동작**
4. POS에서 결제 완료 처리
5. OrderCompleted 이벤트 발행 (주문ID, 예약ID, 상품정보 포함)
6. Main Server에서 이벤트 수신 후 재고 차감
   - 예약된 재고를 실제 차감으로 전환
   - StockUpdated 이벤트 발행

### 3단계: 실패 처리 및 보상

```
[실패 시나리오 1: 결제 실패]
POS Server: 결제 실패 → OrderCancelled Event 발행
Main Server: 예약 취소 및 재고 복구

[실패 시나리오 2: 예약 타임아웃]
Main Server: 5분 후 자동 예약 취소
POS Server: 예약 만료 알림 수신 → 재주문 필요

[실패 시나리오 3: 이벤트 처리 실패]
Main Server: DLQ(Dead Letter Queue)로 이동
운영팀: 수동 처리 또는 재처리
```

## 데이터 모델

### 재고 예약 요청 (API)
```json
{
  "hqId": "HQ001",
  "containerId": "CONTAINER001",
  "storeId": "STORE001",
  "items": [
    {
      "productId": "PRD001",
      "quantity": 2,
      "price": 10000
    }
  ],
  "transactionId": "TRX123456"
}
```

### OrderCompleted Event
```json
{
  "eventId": "evt_123",
  "eventType": "OrderCompleted",
  "timestamp": "2024-01-01T10:00:00Z",
  "data": {
    "orderId": "ORD123456",
    "reservationId": "RES789",
    "hqId": "HQ001",
    "containerId": "CONTAINER001",
    "storeId": "STORE001",
    "items": [
      {
        "productId": "PRD001",
        "quantity": 2,
        "actualPrice": 10000
      }
    ],
    "totalAmount": 20000
  }
}
```

### StockUpdated Event
```json
{
  "eventId": "evt_124",
  "eventType": "StockUpdated",
  "timestamp": "2024-01-01T10:00:01Z",
  "data": {
    "hqId": "HQ001",
    "containerId": "CONTAINER001",
    "productId": "PRD001",
    "stockLevels": {
      "containerStock": {
        "unitQty": {
          "previous": 1000,
          "current": 998
        },
        "usageQty": {
          "previous": 500,
          "current": 498
        }
      }
    },
    "changeAmount": {
      "unitQty": -2,
      "usageQty": -2
    },
    "changeReason": "SALE",
    "referenceId": "ORD123456"
  }
}
```

## API 엔드포인트

### Main Server가 제공해야 할 API

1. **컨테이너별 재고 조회**
   - `GET /api/v1/containers/{containerId}/products/{productId}/stock`
   - Response: `{hqId, containerId, productId, unitQty, usageQty, reservedStock}`

2. **재고 예약**
   - `POST /api/v1/inventory/reserve`
   - Request: `{hqId, containerId, items: [{productId, quantity}], transactionId}`
   - Response: `{reservationId, expiresAt, items: [{productId, reserved}]}`

3. **예약 취소**
   - `DELETE /api/v1/inventory/reserve/{reservationId}`
   - Response: `{success: boolean, message}`

4. **컨테이너 재고 조정** (관리자 전용)
   - `POST /api/v1/admin/container/{containerId}/adjust`
   - Request: `{adjustmentType: "INCREASE|DECREASE", unitQty, usageQty, reason}`
   - Response: `{adjustmentId, beforeQty, afterQty}`

5. **상품 컨테이너 관리** (관리자 전용)
   - `POST /api/v1/admin/product-containers/adjust`
   - Request: `{hqId, containerId, adjustmentType, unitQty, usageQty, reason}`
   - Response: `{containerAdjustmentId, status, stockLevels}`

## Redis 분산락 전략

### 락 구조
```
Lock Key Pattern: inventory:lock:{hqId}:{containerId}:{productId}
Lock Value: {nodeId}:{timestamp}:{requestId}
TTL: 10초 (자동 만료)

계층별 락 키:
- HQ 레벨: inventory:lock:hq:{hqId}
- Container 레벨: inventory:lock:container:{hqId}:{containerId}
- ProductStock 레벨: inventory:lock:stock:{hqId}:{containerId}:{productId}
- ProductContainer 레벨: inventory:lock:product-container:{hqId}:{containerId}
```

### Redlock 알고리즘 적용
```kotlin
// 예시 구현
class RedisDistributedLock {
    fun acquireLock(
        hqId: String,
        containerId: String,
        productId: String,
        timeout: Duration
    ): Boolean {
        val lockKey = "inventory:lock:stock:$hqId:$containerId:$productId"
        val lockValue = "${nodeId}:${timestamp}:${UUID.randomUUID()}"

        // Redisson RLock 사용
        val lock = redisson.getLock(lockKey)
        return try {
            lock.tryLock(timeout.toMillis(), TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            false
        }
    }

    fun releaseLock(
        hqId: String,
        containerId: String,
        productId: String,
        lockValue: String
    ): Boolean {
        val lockKey = "inventory:lock:stock:$hqId:$containerId:$productId"
        // Redisson의 RLock 사용으로 원자적 해제
        val lock = redisson.getLock(lockKey)
        return try {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock()
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }
}
```

### 분산락 적용 지점
1. **재고 예약 시점**
   - 상품별 분산락 획득
   - 재고 확인 및 예약 처리
   - 락 해제

2. **재고 차감 시점**
   - 예약 ID 기반 락 획득
   - 예약 확정 및 재고 차감
   - 락 해제

3. **데드락 방지**
   - 락 획득 순서 일관성 유지
   - 타임아웃 설정 필수
   - 락 해제 실패 시 TTL 의존

## 이벤트 토픽 구조

### Kafka Topics
- `order.completed` - 주문 완료 이벤트
- `order.cancelled` - 주문 취소 이벤트
- `stock.updated` - 재고 변경 이벤트
- `stock.reserved` - 재고 예약 이벤트
- `stock.reservation.expired` - 예약 만료 이벤트

## 비즈니스 규칙

1. **재고 예약**
   - 예약 유효 시간: 5분
   - 동일 상품 중복 예약 불가
   - 예약 수량은 매장 가용 재고 이내
   - 매장 재고 부족 시 HQ 재고 확인

2. **재고 차감**
   - OrderCompleted 이벤트 수신 시에만 실제 차감
   - 예약 없는 차감 불가
   - 차감 후 음수 재고 불가
   - HQ와 Store 재고 동시 차감

3. **동시성 처리**
   - Redis 분산락 우선 적용 (HQ-Store-Product 계층별)
   - 낙관적 락(Optimistic Lock) 보조 사용
   - 재시도 정책: 3회, exponential backoff
   - 락 타임아웃: 10초

4. **재고 계층 관리**
   - ProductContainer: HQ-Container 단위 재고 관리
   - ProductStock: Container별 상품 재고 (unitQty, usageQty)
   - Store는 재고와 독립적으로 관리 (조직 정보만)
   - 신규 입고는 Container 재고로 직접 반영
   - Container 간 재고 이동은 수기 처리 (관리자 권한)

## 모니터링 지표

### 추적해야 할 메트릭
- 재고 예약 성공/실패율
- 예약-확정 전환율
- 평균 예약 처리 시간
- 이벤트 처리 지연 시간
- 재고 불일치 발생 건수
- Redis 분산락 획득 시간
- 락 경합(contention) 빈도
- 데드락 발생 건수

### 알람 설정
- 예약 실패율 > 5%
- 이벤트 처리 지연 > 10초
- DLQ 메시지 발생
- 재고 불일치 감지

## 장애 대응

### Circuit Breaker
- Main Server API 호출 실패 시 Circuit Open
- 임시 로컬 재고 관리 모드 전환
- 복구 후 동기화 프로세스 실행

### Fallback 전략
1. API 호출 실패: 이벤트 기반으로 전환
2. 이벤트 발행 실패: 로컬 큐 저장 후 재시도
3. 완전 실패: 수동 개입 알림

## 구현 우선순위

### Phase 1 (MVP)
- [x] 재고 조회 API
- [x] 재고 예약 API
- [x] OrderCompleted 이벤트 처리
- [x] 기본 재고 차감 로직

### Phase 2 (안정화)
- [ ] Redis 분산락 구현
- [ ] 예약 만료 처리
- [ ] 보상 트랜잭션
- [ ] 모니터링 대시보드
- [ ] Circuit Breaker

### Phase 3 (최적화)
- [ ] 배치 재고 처리
- [ ] 재고 캐싱
- [ ] 예측 기반 사전 예약
- [ ] 멀티 스토어 재고 분산

## 주의사항

1. **데이터 일관성**
   - 최종 일관성(Eventual Consistency) 수용
   - 정기적인 재고 대사 작업 필수
   - 분산락과 DB 트랜잭션 분리

2. **성능 고려사항**
   - 피크 시간대 API 호출 최소화
   - 이벤트 배치 처리 고려
   - 재고 조회 캐싱 적용
   - Redis 클러스터 구성으로 분산락 성능 확보

3. **보안**
   - API 인증 토큰 관리
   - 이벤트 메시지 암호화
   - 감사 로그 기록

4. **Redis 분산락 운영**
   - Redis Sentinel 또는 Cluster 구성 필수
   - 락 모니터링 대시보드 구축
   - 락 획득 실패 시 재시도 전략
   - Split-brain 상황 대비 (Redlock 알고리즘)