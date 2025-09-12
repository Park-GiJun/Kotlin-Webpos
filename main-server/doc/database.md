# Main Server Database 설계

## 공통 규칙

- 외래키 제약조건 사용 안함
- 모든 테이블 Primary Key는 `id`로 통일
- 공통 컬럼: `created_at`, `created_by`, `updated_at`, `updated_by`, `is_deleted`, `deleted_at`, `deleted_by`

## ERD 구조

### 1. Organization 도메인

#### HQ (본사)

```sql
CREATE TABLE hq (
                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL COMMENT '본사명',
                    representative VARCHAR(50) NOT NULL COMMENT '대표자명',
                    address VARCHAR(255) NOT NULL COMMENT '주소',
                    email VARCHAR(100) COMMENT '이메일',
                    phone_number VARCHAR(20) COMMENT '전화번호',
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    created_by VARCHAR(50) NOT NULL,
                    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    updated_by VARCHAR(50) NOT NULL,
                    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                    deleted_at DATETIME NULL,
                    deleted_by VARCHAR(50) NULL
);
```

#### Store (매장)

```sql
CREATE TABLE store (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       hq_id BIGINT NOT NULL COMMENT 'HQ ID',
                       name VARCHAR(100) NOT NULL COMMENT '매장명',
                       representative VARCHAR(50) NOT NULL COMMENT '대표자명',
                       address VARCHAR(255) NOT NULL COMMENT '주소',
                       email VARCHAR(100) COMMENT '이메일',
                       phone_number VARCHAR(20) COMMENT '전화번호',
                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       created_by VARCHAR(50) NOT NULL,
                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       updated_by VARCHAR(50) NOT NULL,
                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                       deleted_at DATETIME NULL,
                       deleted_by VARCHAR(50) NULL
);
```

#### POS (포스)

```sql
CREATE TABLE pos (
                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                     hq_id BIGINT NOT NULL COMMENT 'HQ ID',
                     store_id BIGINT NOT NULL COMMENT 'Store ID',
                     pos_number VARCHAR(20) NOT NULL COMMENT 'POS 번호',
                     created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                     created_by VARCHAR(50) NOT NULL,
                     updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                     updated_by VARCHAR(50) NOT NULL,
                     is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                     deleted_at DATETIME NULL,
                     deleted_by VARCHAR(50) NULL
);
```

### 2. Product 도메인

#### Product (상품)

```sql
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hq_id BIGINT NOT NULL COMMENT 'HQ ID',
    product_name VARCHAR(200) NOT NULL COMMENT '상품명',
    product_code VARCHAR(50) NOT NULL COMMENT '상품코드',
    supply_amt DECIMAL(15,2) NOT NULL COMMENT '공급가액',
    unit VARCHAR(20) NOT NULL COMMENT '단위 (개, 통, g, ml 등)',
    price DECIAML(15,2) NOT NULL COMMENT '판매가'
    usage_unit VARCHAR(20) NOT NULL COMMENT '사용단위',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME NULL,
    deleted_by VARCHAR(50) NULL
);
```

#### Product Cost (상품 가격 이력)

```sql
CREATE TABLE product_cost (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hq_id BIGINT NOT NULL COMMENT 'HQ ID',
    product_id BIGINT NOT NULL COMMENT 'Product ID',
    start_date DATE NOT NULL COMMENT '시작일',
    end_date DATE NULL COMMENT '종료일',
    price DECIMAL(15,2) NOT NULL COMMENT '가격',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME NULL,
    deleted_by VARCHAR(50) NULL
);
```

#### Product Stock (상품 재고)

```sql
CREATE TABLE product_stock (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL COMMENT 'Product ID',
    hq_id BIGINT NOT NULL COMMENT 'HQ ID',
    store_id BIGINT NOT NULL COMMENT 'Store ID',
    unit_qty DECIMAL(15,3) NOT NULL COMMENT '단위 수량',
    usage_qty DECIMAL(15,3) NOT NULL COMMENT '사용 수량',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at DATETIME NULL,
    deleted_by VARCHAR(50) NULL
);
```
