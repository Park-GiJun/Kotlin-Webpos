# MSA 시스템 설계 문서

## 시스템 개요

MSA(Microservices Architecture) 형태의 멀티 매장 관리 시스템

## 매장 구조

### 계층 구조

```
HQ (본사)
└── Store (매장) [1:N]
    └── POS (포스) [1:N]
```

### 관리 범위

| 계층        | 관리 대상                 | 설명             |
|-----------|-----------------------|----------------|
| **HQ**    | 고객 관리, 상품 마스터         | 전사 표준 데이터 관리   |
| **Store** | 재고 관리, Application 주문 | 매장별 운영 데이터 관리  |
| **POS**   | 판매 처리                 | 개별 단말기별 판매 데이터 |

## 공통 규약

### 기술 스택

- **Language**: Kotlin
- **JDK**: Java 21
- **Database**: MySQL
- **Cache**: Redis (with Redisson)
- **Message Queue**: Kafka

### 프로젝트 구조

```
src/
├── application/
│   ├── dto/
│   │   ├── command/
│   │   │   └── [DOMAIN]/
│   │   ├── query/
│   │   │   └── [DOMAIN]/
│   │   ├── result/
│   │   │   └── [DOMAIN]/
│   │   └── event/
│   │       └── [DOMAIN]/
│   ├── handler/
│   │   ├── command/
│   │   │   └── [DOMAIN]/
│   │   ├── query/
│   │   │   └── [DOMAIN]/
│   │   ├── result/
│   │   │   └── [DOMAIN]/
│   │   └── event/
│   │       └── [DOMAIN]/
│   ├── port/
│   │   ├── in/
│   │   │   └── [DOMAIN]/
│   │   └── out/
│   │       └── [DOMAIN]/
│   └── mapper/
│       └── [DOMAIN]/
├── domain/
│   └── [DOMAIN]/
│       ├── model/
│       ├── exception/
│       └── service/
└── infrastructure/
    ├── adapter/
    │   ├── in/
    │   │   ├── web/
    │   │   │   └── [DOMAIN]/
    │   └── out/
    │       └── persistence/
    │           └── [DOMAIN]/
    │               ├── entity/
    │               ├── projection/
    │               ├── jpa/
    │               ├── adpater/
    │               └── mapper/  
    └── config/
```

### REST API 규칙

#### API 기본 구조

각 서버는 서버명을 기반으로 API 경로를 구성합니다:

- **MainServer**: `/main/**`
- **ApplicationServer**: `/application/**`
- **POSServer**: `/pos/**`

#### 응답 포맷

모든 API는 공통 DTO 구조를 사용합니다:

**요청 (Request)**

```kotlin
data class ApiRequest<T>(
    val data: T,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val requestId: String = UUID.randomUUID().toString()
)
```

**응답 (Response)**

```kotlin
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?,
    val errorCode: String?,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val requestId: String?
)
```

#### HTTP 상태 코드

**성공 응답 (2xx)**

- `200 OK`: 조회, 수정 성공
- `201 Created`: 생성 성공
- `204 No Content`: 삭제 성공 (응답 데이터 없음)

**클라이언트 오류 (4xx)**

- `400 Bad Request`: 잘못된 요청 (유효성 검증 실패)
- `401 Unauthorized`: 인증 실패
- `403 Forbidden`: 권한 없음
- `404 Not Found`: 리소스 없음
- `409 Conflict`: 중복 데이터 또는 비즈니스 규칙 위반
- `422 Unprocessable Entity`: 요청은 올바르나 비즈니스 로직 처리 불가

**서버 오류 (5xx)**

- `500 Internal Server Error`: 서버 내부 오류
- `502 Bad Gateway`: 외부 서비스 통신 오류
- `503 Service Unavailable`: 서비스 일시 중단
- `504 Gateway Timeout`: 외부 서비스 타임아웃

**사용자 정의 에러 코드**

- `VALIDATION_ERROR`: 입력값 검증 오류
- `BUSINESS_ERROR`: 비즈니스 로직 오류
- `RESOURCE_NOT_FOUND`: 리소스 없음
- `DUPLICATE_RESOURCE`: 중복 리소스
- `EXTERNAL_SERVICE_ERROR`: 외부 서비스 오류
- `DATABASE_ERROR`: 데이터베이스 오류

## 서버 구성

### 1. Main Server

**역할**: 중앙 허브 서버

**기능**:

- 상품 마스터 정보 CRUD (HQ 단위)
- 상품 정보의 Store별 배포
- 고객 정보 CRUD (HQ 단위)
- 통합 고객 관리
- 매장 관리 (HQ, Store, POS 정보)

### 2. POS Server

**역할**: 매장 내 판매 처리

**기능**:

- POS별 판매 처리
- Store 재고 연동
- 판매 내역 관리

### 3. Application Server

**역할**: 온라인 주문 처리

**기능**:

- Store별 온라인 주문 처리 (모바일/웹)
- Store 재고 연동
- 주문 내역 관리

## 아키텍처 패턴

### 헥사고날 아키텍처 (Hexagonal Architecture)

- **적용 범위**: 모든 서버
- **목적**:
    - 비즈니스 로직과 외부 의존성 분리
    - 테스트 가능한 구조 구축
    - 유연한 확장성 확보

### CQRS 패턴 (Command Query Responsibility Segregation)

- **적용 범위**: 모든 서버
- **목적**:
    - 읽기와 쓰기 작업 분리
    - 성능 최적화
    - 확장성 향상

## 데이터 흐름

### 재고 동기화

```
POS 판매 → Store 재고 차감 → Main Server 보고
```

### 상품 정보 배포

```
HQ 상품 마스터 → Main Server → Store별 상품 정보 동기화
```

### 주문 처리

```
Application 주문 → 해당 Store로 라우팅 → 재고 확인 → 주문 처리
```
