# K6 Load Testing Suite

K6를 사용한 POS 시스템 부하 테스트 스크립트 모음입니다.

## 사전 준비

### 1. K6 설치

**Windows (Chocolatey):**
```bash
choco install k6
```

**Windows (Winget):**
```bash
winget install k6 --source winget
```

**Manual Download:**
https://k6.io/docs/get-started/installation/

### 2. 테스트 데이터 초기화

SQL 파일을 사용하여 테스트 데이터를 생성합니다:

```bash
# MySQL 컨테이너에 SQL 실행
docker exec -i mysql-main mysql -u mainuser -pmainpassword main_server < docker/init-scripts/06-complete-test-data.sql
```

또는 DataGrip/MySQL Workbench에서 직접 실행하세요.

### 3. 서버 실행

```bash
# Main Server
cd main-server
.\gradlew.bat bootRun

# POS Server (다른 터미널에서)
cd pos-server
.\gradlew.bat bootRun
```

## 테스트 스크립트

### 🔍 Quick Smoke Test (빠른 검증)

시스템이 정상적으로 작동하는지 빠르게 확인합니다.

```bash
cd k6
k6 run tests/quick-smoke-test.js
```

**특징:**
- 1 VU (Virtual User)
- 10 iterations
- HQ 1, Store 1, POS 1만 테스트
- 실행 시간: ~10초

### 🎯 Comprehensive Sales Test (종합 테스트)

전체 시스템을 대상으로 현실적인 부하 테스트를 수행합니다.

```bash
cd k6
run-comprehensive-test.bat
```

또는:

```bash
cd k6
k6 run tests/comprehensive-sales-test.js
```

**특징:**
- 3 HQs (Seoul, Busan, Jeju)
- 15 Stores (HQ당 5개)
- 30 POS terminals (Store당 2개)
- 68 Products (원재료 8개 + 레시피 30개 + 완제품 30개)
- 실행 시간: ~4분

**부하 단계:**
1. Ramp-up: 30초 동안 10 VUs까지 증가
2. Sustain: 1분 동안 10 VUs 유지
3. Ramp-up: 30초 동안 20 VUs까지 증가
4. Sustain: 1분 동안 20 VUs 유지
5. Ramp-down: 30초 동안 0 VUs까지 감소

## 테스트 데이터 구조

### HQ 1 (Seoul Headquarters)
- **Stores:** 1-5 (Gangnam, Hongdae, Itaewon, Myeongdong, Jamsil)
- **POS:** 1-10
- **Containers:** 1-5
- **Ingredients:** 1-8 (Coffee Beans, Milk, Sugar, etc.)
- **Recipes:** 9-18 (Americano, Latte, Mocha, etc.)
- **Products:** 39-48 (Croissant, Muffin, Cookie, etc.)

### HQ 2 (Busan Headquarters)
- **Stores:** 6-10
- **POS:** 11-20
- **Containers:** 6-10
- **Recipes:** 19-28
- **Products:** 49-58

### HQ 3 (Jeju Headquarters)
- **Stores:** 11-15
- **POS:** 21-30
- **Containers:** 11-15
- **Recipes:** 29-38
- **Products:** 59-68

## 성능 임계값 (Thresholds)

### Comprehensive Test
- `http_req_duration p(95) < 3000ms` - 95%의 요청이 3초 이내
- `http_req_failed rate < 0.1` - 실패율 10% 미만
- `errors rate < 0.15` - 에러율 15% 미만

### Quick Smoke Test
- `http_req_duration p(95) < 5000ms` - 95%의 요청이 5초 이내
- `http_req_failed rate < 0.2` - 실패율 20% 미만

## 출력 메트릭

테스트 완료 후 다음 메트릭이 표시됩니다:

- **Total Sales Transactions**: 성공한 판매 건수
- **Total Sales Amount**: 총 판매 금액
- **Average Sale Amount**: 평균 판매 금액
- **Error Rate**: 에러 발생률
- **Stock Errors**: 재고 부족 에러
- **Failed Requests**: 실패한 요청률
- **Avg Response Time**: 평균 응답 시간
- **P95 Response Time**: 95번째 백분위수 응답 시간

## 환경 변수

```bash
# POS Server URL 변경
k6 run --env POS_URL=http://localhost:8081 tests/comprehensive-sales-test.js

# Main Server URL 변경 (일부 테스트)
k6 run --env MAIN_URL=http://localhost:8080 tests/main-server-api.js
```

## 트러블슈팅

### 1. "k6 is not recognized" 에러

K6가 설치되지 않았거나 PATH에 추가되지 않았습니다.
```bash
# 설치 확인
k6 version
```

### 2. "Connection refused" 에러

서버가 실행되고 있지 않습니다.
```bash
# 서버 상태 확인
curl http://localhost:8081/actuator/health
```

### 3. "Stock insufficient" 에러

재고가 부족합니다. SQL 파일을 다시 실행하여 재고를 초기화하세요.

```bash
docker exec -i mysql-main mysql -u mainuser -pmainpassword main_server < docker/init-scripts/06-complete-test-data.sql
```

### 4. 높은 에러율

- 데이터베이스 연결 확인
- 서버 로그 확인
- 재고 초기화
- VU 수 감소 (부하 줄이기)

## 기타 테스트 스크립트

프로젝트에는 다음과 같은 추가 테스트 스크립트가 있습니다:

- `main-server-api.js` - Main Server API 테스트
- `pos-server-api.js` - POS Server API 테스트
- `smoke-test.js` - 기본 Smoke 테스트
- `sales-with-stock.js` - 재고 관리 포함 판매 테스트
- `stock-adjustment.js` - 재고 조정 테스트
- `multi-tier-sales-test.js` - 멀티 티어 판매 테스트
- `simple-sales-test.js` - 간단한 판매 테스트
- `stress-sales-test.js` - 스트레스 테스트

## 참고 자료

- [K6 Documentation](https://k6.io/docs/)
- [K6 Test Types](https://k6.io/docs/test-types/introduction/)
- [K6 Metrics](https://k6.io/docs/using-k6/metrics/)
