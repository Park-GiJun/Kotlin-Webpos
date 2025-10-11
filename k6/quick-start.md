# k6 Quick Start Guide

## 1. k6 설치 확인

새 PowerShell 창을 열고 확인:

```powershell
k6 version
```

출력 예시:
```
k6 v1.3.0 (go1.21.0, windows/amd64)
```

## 2. 서버 실행

### Terminal 1: Main Server
```powershell
cd main-server
.\gradlew.bat bootRun
```

### Terminal 2: POS Server
```powershell
cd pos-server
.\gradlew.bat bootRun
```

서버가 완전히 시작될 때까지 기다리세요 (약 30초~1분).

## 3. 서버 상태 확인

### 브라우저에서:
- Main Server: http://localhost:8080/actuator/health
- POS Server: http://localhost:8081/pos/actuator/health

### PowerShell에서:
```powershell
# Main Server
curl http://localhost:8080/actuator/health

# POS Server
curl http://localhost:8081/pos/actuator/health
```

정상 응답: `{"status":"UP"}`

## 4. 스모크 테스트 실행 (가장 간단)

```powershell
cd k6
k6 run tests/smoke-test.js
```

이 테스트는:
- 1명의 가상 사용자
- 30초 동안 실행
- 서버 헬스 체크 및 기본 API 테스트

## 5. 개별 테스트 실행

### Main Server API 테스트
```powershell
k6 run tests/main-server-api.js
```

### POS Server API 테스트
```powershell
k6 run tests/pos-server-api.js
```

### 재고 차감 테스트
```powershell
k6 run tests/stock-adjustment.js
```

### 판매+재고 통합 테스트 (가장 중요!)
```powershell
k6 run tests/sales-with-stock.js
```

## 6. 테스트 결과 읽기

```
✓ Main server is healthy
✓ Get HQs status is 200

checks.........................: 100.00% ✓ 40   ✗ 0
data_received..................: 15 kB   500 B/s
data_sent......................: 3.2 kB  107 B/s
http_req_duration..............: avg=45ms min=12ms med=38ms max=125ms p(95)=98ms
http_req_failed................: 0.00%   ✓ 0    ✗ 40
http_reqs......................: 40      1.33/s
iteration_duration.............: avg=5.1s min=5.0s med=5.1s max=5.2s
iterations.....................: 6       0.2/s
vus............................: 1       min=1 max=1
```

**중요 지표:**
- ✓ checks: 모든 검증이 통과했는지
- http_req_failed: 실패율 (0.00%가 이상적)
- http_req_duration p(95): 95%의 요청이 이 시간 안에 완료
- http_reqs: 총 요청 수

## 7. 문제 해결

### "Connection refused" 에러
→ 서버가 실행 중인지 확인
```powershell
curl http://localhost:8080/actuator/health
```

### 테스트 데이터가 없다는 에러
→ 데이터베이스에 초기 데이터 필요
```bash
# 테스트 데이터 생성 스크립트 실행
docker exec -i mysql-main mysql -umainuser -pmainpassword main_server < docker/init-scripts/02-insert-test-data.sql

# 또는 MySQL에 접속하여 확인
docker exec mysql-main mysql -umainuser -pmainpassword main_server -e "
  SELECT ps.product_id, ps.container_id, ps.usage_qty, p.name
  FROM product_stock ps
  JOIN product p ON ps.product_id = p.id
  WHERE ps.product_id IN (7, 19) AND ps.container_id = 1;
"
```

### 재고 부족 에러
→ 재고 증가 먼저 실행
```powershell
# Product 7 재고 증가
curl -X PUT "http://localhost:8080/main/product-stock/product/7/store/1/adjust" -H "Content-Type: application/json" -d "{\"adjustmentType\":\"INCREASE\",\"unitQty\":0,\"usageQty\":100,\"reason\":\"Test\"}"

# 또는 stock-adjustment 테스트 실행 (자동으로 재고 증가)
k6 run tests/stock-adjustment.js

# 판매 테스트 실행
k6 run tests/sales-with-stock.js
```

## 8. 간단한 커스텀 테스트

최소한의 테스트 스크립트 예시:

```javascript
// my-test.js
import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 10,
  duration: '30s',
};

export default function() {
  const res = http.get('http://localhost:8080/actuator/health');
  check(res, {
    'status is 200': (r) => r.status === 200,
  });
}
```

실행:
```powershell
k6 run my-test.js
```

## 9. 전체 테스트 스위트 실행

```powershell
# Windows
.\run-all-tests.bat

# 또는 수동으로
k6 run tests/smoke-test.js
k6 run tests/main-server-api.js
k6 run tests/pos-server-api.js
k6 run tests/stock-adjustment.js
k6 run tests/sales-with-stock.js
```

## 10. Swagger UI로 API 확인

테스트하기 전에 API 구조 확인:
- Main Server: http://localhost:8080/swagger-ui.html
- POS Server: http://localhost:8081/pos/swagger-ui.html

## 다음 단계

1. ✅ 스모크 테스트로 서버 정상 동작 확인
2. ✅ 개별 API 테스트 실행
3. ✅ 통합 테스트 (sales-with-stock.js) 실행
4. 📊 결과 분석 및 성능 개선
5. 🔄 CI/CD 파이프라인에 통합

## 유용한 k6 옵션

```powershell
# 가상 사용자 50명, 1분간
k6 run --vus 50 --duration 1m tests/smoke-test.js

# 결과를 JSON 파일로 저장
k6 run --out json=reports/result.json tests/smoke-test.js

# HTTP 요청/응답 디버깅
k6 run --http-debug tests/smoke-test.js

# 1회만 실행 (디버깅용)
k6 run --vus 1 --iterations 1 tests/smoke-test.js
```

## 참고 자료

- 테스트 데이터 정보: [TEST_DATA_INFO.md](./TEST_DATA_INFO.md) ⭐ 중요!
- 전체 문서: [k6/README.md](./README.md)
- k6 공식 문서: https://k6.io/docs/
- 테스트 스크립트: [k6/tests/](./tests/)
- 프로젝트 테스트 데이터 설정: [../TEST_DATA_SETUP.md](../TEST_DATA_SETUP.md)
