# Main Server 아키텍처 가이드

## 프로젝트 개요

Main Server는 다중 매장 관리 시스템의 중앙 서버로, 본사(HQ) - 매장(Store) - POS 계층 구조를 관리하며 재고, 상품, 조직 정보를 통합 관리합니다.

## 아키텍처 원칙

### 1. 헥사고날 아키텍처 (Ports and Adapters)
```
┌─────────────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                         │
│  ┌─────────────────┐                    ┌─────────────────┐     │
│  │   Web Adapters  │                    │ Persistence     │     │
│  │   (REST API)    │                    │   Adapters      │     │
│  └─────────────────┘                    └─────────────────┘     │
└─────────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────────┐
│                    Application Layer                            │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ Command Handlers│  │ Query Handlers  │  │ Cache Handlers  │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘  │
│  ┌─────────────────┐                    ┌─────────────────┐     │
│  │    Input Ports  │                    │   Output Ports  │     │
│  │  (Use Cases)    │                    │ (Repositories)  │     │
│  └─────────────────┘                    └─────────────────┘     │
└─────────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────────┐
│                      Domain Layer                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │  Organization   │  │    Product      │  │     Common      │  │
│  │  (Hq, Store,    │  │ (Product, Stock,│  │   (Exceptions,  │  │
│  │      Pos)       │  │   Container)    │  │   Base Classes) │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### 2. CQRS 패턴 적용
- **Command**: 데이터 변경 작업 (Create, Update, Delete)
- **Query**: 데이터 조회 작업 (Read)
- 각각 별도의 Handler와 Repository로 분리

### 3. 도메인 주도 설계 (DDD)
- Rich Domain Model
- 도메인 로직은 Domain Layer에 집중
- 인프라스트럭처와 완전 분리

## 디렉토리 구조

```
src/main/kotlin/com/gijun/mainserver/
├── application/                    # 애플리케이션 레이어
│   ├── dto/                       # 데이터 전송 객체
│   │   ├── command/               # CQRS 명령 객체
│   │   │   ├── organization/      # 조직 관련 명령
│   │   │   └── product/           # 상품 관련 명령
│   │   ├── query/                 # CQRS 쿼리 객체
│   │   └── result/                # 응답 결과 객체
│   ├── handler/                   # 비즈니스 로직 처리
│   │   ├── cache/                 # 캐시 처리 핸들러
│   │   ├── command/               # 명령 처리 핸들러
│   │   │   ├── organization/      # 조직 명령 처리
│   │   │   └── product/           # 상품 명령 처리
│   │   └── query/                 # 쿼리 처리 핸들러
│   ├── mapper/                    # 도메인 ↔ DTO 변환
│   └── port/                      # 포트 정의
│       ├── in/                    # 입력 포트 (Use Cases)
│       └── out/                   # 출력 포트 (Repository 인터페이스)
├── domain/                        # 도메인 레이어
│   ├── common/                    # 공통 도메인
│   │   ├── entity/                # 기본 엔티티
│   │   └── exception/             # 도메인 예외
│   ├── organization/              # 조직 도메인
│   │   ├── hq/model/              # 본사 모델
│   │   ├── store/model/           # 매장 모델
│   │   └── pos/                   # POS 모델
│   └── product/                   # 상품 도메인
│       ├── product/model/         # 상품 마스터
│       ├── productStock/model/    # 상품 재고
│       └── productContainer/model/# 상품 컨테이너
└── infrastructure/               # 인프라스트럭처 레이어
    ├── adapter/
    │   ├── in/web/               # REST API 어댑터
    │   │   ├── common/           # 공통 웹 컴포넌트
    │   │   ├── organization/     # 조직 API
    │   │   └── product/          # 상품 API
    │   └── out/                  # 외부 시스템 어댑터
    │       ├── cache/            # 캐시 어댑터
    │       └── persistence/      # 데이터베이스 어댑터
    └── config/                   # 설정
```

## 도메인 모델

### 조직 계층 구조
```
HQ (본사)
 ├── Store (매장)
 │   └── Pos (POS 단말기)
 └── Container (창고/컨테이너)
     └── ProductStock (상품별 재고)
```

### 핵심 도메인

#### 1. Organization Domain
- **Hq**: 본사 정보 관리
- **Store**: 매장 정보 관리
- **Pos**: POS 단말기 관리

#### 2. Product Domain
- **Product**: 상품 마스터 데이터 (가격, 타입, 코드 등)
- **ProductStock**: 컨테이너별 상품 재고 (unitQty, usageQty)
- **ProductContainer**: HQ-Container 단위 재고 관리

### 재고 관리 구조

#### 기존 구조 (AS-IS)
```
HQ → Store → ProductStock
```

#### 현재 구조 (TO-BE)
```
HQ → Container → ProductStock
Store (독립적 매장 정보)
```

**변경 사유**:
- 재고 관리와 매장 정보의 관심사 분리
- Container 기반 물리적/논리적 창고 관리
- 유연한 재고 할당 및 이동

## 주요 패턴 및 규칙

### 1. Single Class Per File Rule
- 한 파일에는 하나의 클래스, 인터페이스, enum만 정의
- 파일명과 클래스명 일치 필수

### 2. Custom Domain Exception Only
- 기본 Java/Kotlin 예외 사용 금지
- 모든 예외는 `domain.common.exception` 패키지의 커스텀 예외 사용
- 예: `InvalidArgumentException`, `EntityNotFoundException`, `ValidationException`

### 3. No Value Objects Rule
- Value Object 사용 금지
- 원시 타입 사용 (String, BigDecimal, Long 등)
- 도메인 모델의 `init` 블록에서 유효성 검증

### 4. API Response Pattern
```kotlin
// 성공 응답
ApiResponse.success(data)

// 실패 응답
ApiResponse.error(errorCode, message)
```

### 5. REST API URL 패턴
```
/main/[domain]/[resource]

예시:
- POST /main/organization/hq
- GET /main/organization/store/{id}
- PUT /main/product/{id}
- POST /main/product/container/adjust
```

## 캐싱 전략

### Redis 캐시 키 구조
```kotlin
object CacheKeys {
    // 상품 캐시
    const val PRODUCT_PREFIX = "product"
    fun productKey(productId: Long) = "$PRODUCT_PREFIX:$productId"
    fun productListByHqKey(hqId: Long) = "$PRODUCT_PREFIX:list:hq:$hqId"

    // 컨테이너 캐시
    const val PRODUCT_CONTAINER_PREFIX = "product:container"
    fun productContainerKey(hqId: Long, containerId: Long) =
        "$PRODUCT_CONTAINER_PREFIX:hq:$hqId:container:$containerId"

    // 재고 캐시
    const val PRODUCT_STOCK_PREFIX = "product:stock"
    fun productStockKey(containerId: Long, productId: Long) =
        "$PRODUCT_STOCK_PREFIX:container:$containerId:product:$productId"
}
```

### 캐시 무효화 전략
- **상품 변경**: 상품별 캐시 + 관련 리스트 캐시 무효화
- **재고 변경**: 컨테이너별 재고 캐시 무효화
- **컨테이너 변경**: 컨테이너별 캐시 + HQ 리스트 캐시 무효화

## 데이터베이스 설계

### 핵심 테이블

#### organization 스키마
```sql
-- 본사
CREATE TABLE hq (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    business_number VARCHAR(20) UNIQUE,
    email VARCHAR(100),
    phone_number VARCHAR(20),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 매장
CREATE TABLE store (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hq_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    store_code VARCHAR(20) UNIQUE,
    phone_number VARCHAR(20),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (hq_id) REFERENCES hq(id)
);

-- POS
CREATE TABLE pos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id BIGINT NOT NULL,
    pos_number VARCHAR(20) NOT NULL,
    device_type VARCHAR(50),
    status ENUM('ACTIVE', 'INACTIVE', 'MAINTENANCE', 'OFFLINE'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES store(id)
);
```

#### product 스키마
```sql
-- 상품 마스터
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hq_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    product_type ENUM('RECIPE', 'PRODUCT', 'INGREDIENT', 'PACKAGE', 'SERVICE', 'GIFT_CARD', 'VOUCHER', 'DIGITAL', 'SUBSCRIPTION', 'RENTAL'),
    product_code VARCHAR(50) NOT NULL,
    supply_amt DECIMAL(10,2),
    unit VARCHAR(20),
    usage_unit VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (hq_id) REFERENCES hq(id)
);

-- 상품 컨테이너 (HQ-Container 단위)
CREATE TABLE product_container (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hq_id BIGINT NOT NULL,
    container_id BIGINT NOT NULL,
    container_name VARCHAR(100) NOT NULL,
    unit_qty DECIMAL(10,2) DEFAULT 0,
    usage_qty DECIMAL(10,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (hq_id) REFERENCES hq(id)
);

-- 상품 재고 (Container-Product 단위)
CREATE TABLE product_stock (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    container_id BIGINT NOT NULL,
    unit_qty DECIMAL(10,2) DEFAULT 0,
    usage_qty DECIMAL(10,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES product(id),
    UNIQUE KEY uk_product_container (product_id, container_id)
);
```

## 테스트 전략

### 도메인 테스트
- 각 도메인 모델별 단위 테스트
- 비즈니스 규칙 검증
- 예외 상황 처리 확인

### 테스트 구조
```
src/test/kotlin/com/gijun/mainserver/
├── domain/
│   ├── organization/
│   │   ├── hq/model/HqTest.kt
│   │   ├── store/model/StoreTest.kt
│   │   └── pos/model/PosTest.kt
│   └── product/
│       ├── product/model/ProductTest.kt
│       ├── productStock/model/ProductStockTest.kt
│       └── productContainer/model/ProductContainerTest.kt
└── integration/
    ├── api/
    └── repository/
```

## 운영 고려사항

### 1. 모니터링
- API 응답 시간
- 캐시 히트율
- 데이터베이스 커넥션 풀
- 메모리 사용량

### 2. 로깅
- 비즈니스 로직 실행 로그
- 에러 로그 (스택 트레이스 포함)
- 성능 측정 로그
- 감사 로그 (데이터 변경 추적)

### 3. 보안
- JWT 토큰 기반 인증
- API 호출 권한 검증
- 민감 데이터 암호화
- SQL Injection 방지

### 4. 성능 최적화
- 데이터베이스 인덱스 최적화
- 캐시 전략 적용
- Connection Pool 튜닝
- 배치 처리 적용

## 확장 계획

### Phase 1 (현재)
- [x] 기본 CRUD API
- [x] 도메인 모델 정의
- [x] 캐시 시스템 구현
- [x] Container 기반 재고 관리

### Phase 2 (계획)
- [ ] 재고 예약 시스템
- [ ] 이벤트 기반 처리
- [ ] 분산락 구현
- [ ] 실시간 재고 동기화

### Phase 3 (확장)
- [ ] 다중 테넌트 지원
- [ ] 국제화 (i18n)
- [ ] 고급 분석 및 리포팅
- [ ] AI 기반 재고 예측