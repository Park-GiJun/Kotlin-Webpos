# K6 Test Data Information

## Current Test Data Configuration

All K6 tests have been updated to use the new test data created in the main-server database.

### Available Test Products

The following products have **ProductStock** available in **container 1** (maps to Store 1):

| Product ID | Product Code | Name | Price | Stock Qty | Type |
|------------|-------------|------|-------|-----------|------|
| 7 | TEST007 | Test Product 7 | 4000 KRW | 100.00 | GENERAL |
| 19 | COFFEE001 | Coffee Americano | 4500 KRW | 100.00 | GENERAL |

### Test Configuration

- **HQ ID**: 1
- **Store ID**: 1
- **POS ID**: 1
- **Container ID**: 1 (automatically maps to Store ID 1)

## Updated Test Scripts

### 1. smoke-test.js
**Purpose**: Quick validation with minimal load

**Changes**:
- Tests health endpoints for both servers
- Gets HQ list
- Gets product list
- Gets specific product (Product 7) with stock verification

**No setup required** - uses existing data

### 2. stock-adjustment.js
**Purpose**: Test stock increase/decrease operations

**Changes**:
- Now uses **Product ID 7** exclusively (has stock in container 1)
- Tests both INCREASE and DECREASE operations
- Verifies stock changes are reflected

**API Endpoint**: `PUT /main/product-stock/product/{productId}/store/{storeId}/adjust`

### 3. pos-server-api.js
**Purpose**: Test POS server sales creation

**Changes**:
- Uses products 7 and 19 (both have stock)
- Creates sales transactions with random product selection
- Verifies sales record creation

**Note**: This will trigger automatic stock deduction in main-server

### 4. sales-with-stock.js
**Purpose**: Complete sales transaction flow with stock deduction

**Changes**:
- Updated product list to use IDs 7 and 19
- Simulates real POS usage with concurrent terminals
- Tests complete flow:
  1. Check product availability
  2. Create sales transaction
  3. Verify automatic stock deduction
  4. Verify sales record

**Load stages**:
- Warm up: 5 concurrent POS terminals (1m)
- Normal: 20 concurrent POS (3m)
- Peak: 50 concurrent POS (2m)
- Cool down (2m)

## Running the Tests

### Quick Start
```bash
# Smoke test - verify everything works
cd k6
k6 run tests/smoke-test.js
```

### Individual Tests
```bash
# Stock adjustment test
k6 run tests/stock-adjustment.js

# POS server API test
k6 run tests/pos-server-api.js

# Complete sales with stock test
k6 run tests/sales-with-stock.js
```

### All Tests
```bash
# Windows
.\run-all-tests.bat

# Linux/Mac
bash run-all-tests.sh
```

## Prerequisites

### 1. Ensure Test Data Exists
```bash
# Check if product 7 has stock
docker exec mysql-main mysql -umainuser -pmainpassword main_server -e "
  SELECT ps.product_id, ps.container_id, ps.unit_qty, ps.usage_qty, p.name
  FROM product_stock ps
  JOIN product p ON ps.product_id = p.id
  WHERE ps.product_id IN (7, 19) AND ps.container_id = 1;
"
```

Expected output:
```
product_id  container_id  unit_qty  usage_qty  name
7           1             100.00    100.00     Test Product 7 (or similar)
19          1             100.00    100.00     Coffee Americano
```

### 2. Increase Stock if Needed
If stock is depleted, run:
```bash
# Increase stock for product 7
curl -X PUT "http://localhost:8080/main/product-stock/product/7/store/1/adjust" \
  -H "Content-Type: application/json" \
  -d '{
    "adjustmentType": "INCREASE",
    "unitQty": 0,
    "usageQty": 100,
    "reason": "Replenish for testing"
  }'

# Increase stock for product 19
curl -X PUT "http://localhost:8080/main/product-stock/product/19/store/1/adjust" \
  -H "Content-Type: application/json" \
  -d '{
    "adjustmentType": "INCREASE",
    "unitQty": 0,
    "usageQty": 100,
    "reason": "Replenish for testing"
  }'
```

## Understanding Test Results

### Key Metrics

1. **checks**: Percentage of validation checks that passed
   - Target: 100%
   - Example: `checks.........................: 100.00% ✓ 40   ✗ 0`

2. **http_req_failed**: Percentage of failed HTTP requests
   - Target: 0.00%
   - Example: `http_req_failed................: 0.00%   ✓ 0    ✗ 40`

3. **http_req_duration**: HTTP request response time
   - Important: p(95) - 95% of requests complete within this time
   - Target: p(95) < 2000ms for most tests
   - Example: `http_req_duration..............: avg=45ms min=12ms med=38ms max=125ms p(95)=98ms`

4. **errors** (custom metric): Error rate across all operations
   - Target: < 5% for sales tests
   - Example: `errors.........................: 2.50%`

5. **sales_count** (custom metric): Total sales transactions completed
   - Example: `sales_count....................: 150`

### Interpreting Results

**✅ Good Results**:
```
checks.........................: 100.00% ✓ 500  ✗ 0
http_req_failed................: 0.00%   ✓ 0    ✗ 500
http_req_duration p(95)........: 800ms
errors.........................: 0.50%
```

**⚠️ Warning Signs**:
```
checks.........................: 95.00%  ✓ 475  ✗ 25    # Some checks failing
http_req_failed................: 2.00%   ✓ 10   ✗ 490   # Some requests failing
http_req_duration p(95)........: 3500ms              # Slow response times
errors.........................: 8.00%               # High error rate
```

**❌ Poor Results**:
```
checks.........................: 80.00%  ✓ 400  ✗ 100   # Many failures
http_req_failed................: 10.00%  ✓ 50   ✗ 450   # High failure rate
http_req_duration p(95)........: 5000ms              # Very slow
errors.........................: 15.00%              # Unacceptable error rate
```

## Troubleshooting

### Test Fails with "ProductStock not found"
**Cause**: Stock for product 7 or 19 doesn't exist in container 1

**Solution**:
1. Run the test data creation script:
   ```bash
   docker exec -i mysql-main mysql -umainuser -pmainpassword main_server < docker/init-scripts/02-insert-test-data.sql
   ```

2. Or manually insert stock:
   ```sql
   INSERT INTO product_stock (product_id, container_id, unit_qty, usage_qty, created_at, updated_at, created_by, updated_by, is_deleted)
   VALUES (7, 1, 100.00, 100.00, NOW(), NOW(), 'SYSTEM', 'SYSTEM', 0);
   ```

### Test Fails with "Insufficient stock"
**Cause**: Stock depleted during testing

**Solution**: Increase stock using the curl command above

### High Error Rate
**Causes**:
- Servers not running
- Database connection issues
- Stock depletion during load test

**Solutions**:
1. Check server status:
   ```bash
   curl http://localhost:8080/actuator/health
   curl http://localhost:8081/pos/actuator/health
   ```

2. Check logs for errors

3. Replenish stock before running tests

### Slow Response Times
**Causes**:
- High concurrent load
- Database performance issues
- Network latency

**Solutions**:
1. Reduce virtual users: `k6 run --vus 10 tests/smoke-test.js`
2. Check database connection pool settings
3. Monitor database query performance

## Test Data Maintenance

### After Running Load Tests

Stock levels will be depleted. To reset:

```bash
# Check current stock
docker exec mysql-main mysql -umainuser -pmainpassword main_server -e "
  SELECT product_id, container_id, usage_qty
  FROM product_stock
  WHERE product_id IN (7, 19) AND container_id = 1;
"

# Replenish stock
curl -X PUT "http://localhost:8080/main/product-stock/product/7/store/1/adjust" \
  -H "Content-Type: application/json" \
  -d '{"adjustmentType":"INCREASE","unitQty":0,"usageQty":1000,"reason":"Reset after testing"}'
```

## Next Steps

1. ✅ Run smoke test to verify setup
2. ✅ Run individual tests
3. ✅ Run full load test (sales-with-stock.js)
4. 📊 Analyze results and identify bottlenecks
5. 🔧 Optimize based on findings
6. 🔄 Integrate into CI/CD pipeline

## Related Documentation

- [Quick Start Guide](./quick-start.md)
- [Main README](./README.md)
- [Test Data Setup](../TEST_DATA_SETUP.md)
