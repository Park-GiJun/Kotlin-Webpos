# K6 Tests Changelog

## Date: 2025-10-11

### Summary
Updated all K6 test scripts to use the newly created test data in the main-server database. Tests now use Product IDs 7 and 19, which have ProductStock available in container 1.

---

## Changes Made

### 1. Test Data Updated

#### Previous Configuration
- Used products 1, 2, 6, 7 from existing database
- Products: 아메리카노(Tall), 카페라떼(Tall), 치즈케이크, 크루아상
- Some products had no stock or insufficient stock

#### New Configuration
- Uses products **7** and **19** exclusively
- **Product 7**: Test Product 7 (4000 KRW)
- **Product 19**: Coffee Americano (4500 KRW)
- Both products have **100 units** of stock in **container 1**
- ProductStock properly configured for stock adjustment operations

### 2. Test Scripts Modified

#### smoke-test.js
**Changes**:
- Updated product endpoint from `/main/product/product` to `/main/product`
- Added specific product test (Product 7) with existence verification
- Added sleep timing adjustments

**Why**: Ensures smoke test validates core functionality with verified test data

#### stock-adjustment.js
**Changes**:
- Changed from random product selection (6, 7) to fixed **Product 7**
- Updated comments to reflect new test data structure
- Clarified that storeId maps to containerId

**Before**:
```javascript
const products = [6, 7];
const productId = products[Math.floor(Math.random() * products.length)];
const storeId = 1; // 강남역점
```

**After**:
```javascript
const productId = 7;
const storeId = 1; // Container ID 1 (maps to Store 1)
```

**Why**: Product 7 is guaranteed to have stock in container 1

#### pos-server-api.js
**Changes**:
- Updated product list to use IDs 7 and 19
- Changed product codes to TEST007 and COFFEE001
- Updated product names and prices to match new test data

**Before**:
```javascript
const products = [
  { id: 6, code: 'FOD-CHZ-001', name: '뉴욕 치즈케이크', price: 6300 },
  { id: 7, code: 'FOD-CRO-001', name: '초콜릿 크루아상', price: 4000 }
];
```

**After**:
```javascript
const products = [
  { id: 7, code: 'TEST007', name: 'Test Product 7', price: 4000 },
  { id: 19, code: 'COFFEE001', name: 'Coffee Americano', price: 4500 }
];
```

**Why**: Ensures all sales transactions use products with verified stock

#### sales-with-stock.js
**Changes**:
- Updated setup() function to use new product data
- Changed from 4 products (recipe + general) to 2 general products
- Updated product metadata (codes, names, prices)

**Before**:
```javascript
products: [
  { id: 6, code: 'FOD-CHZ-001', name: '뉴욕 치즈케이크', price: 6300, type: 'GENERAL' },
  { id: 7, code: 'FOD-CRO-001', name: '초콜릿 크루아상', price: 4000, type: 'GENERAL' },
  { id: 1, code: 'BEV-AME-T', name: '아메리카노(Tall)', price: 4500, type: 'RECIPE' },
  { id: 2, code: 'BEV-LAT-T', name: '카페라떼(Tall)', price: 5000, type: 'RECIPE' },
]
```

**After**:
```javascript
products: [
  { id: 7, code: 'TEST007', name: 'Test Product 7', price: 4000, type: 'GENERAL' },
  { id: 19, code: 'COFFEE001', name: 'Coffee Americano', price: 4500, type: 'GENERAL' },
]
```

**Why**: Simplified test data, focused on general products with guaranteed stock

### 3. Documentation Created/Updated

#### New Files
1. **TEST_DATA_INFO.md**
   - Complete guide to test data configuration
   - Product details and stock levels
   - Test script explanations
   - Running instructions
   - Troubleshooting guide
   - Results interpretation

#### Updated Files
1. **quick-start.md**
   - Updated troubleshooting section with new commands
   - Added reference to TEST_DATA_INFO.md
   - Updated test data verification commands
   - Added stock replenishment instructions

### 4. Test Data Scripts

Referenced but not modified (created separately):
- `docker/init-scripts/02-insert-test-data.sql`
- `scripts/create-test-data.bat`
- `scripts/create-test-data.sh`
- `TEST_DATA_SETUP.md` (project root)

---

## Benefits

### 1. Reliability
- All tests now use verified, consistent test data
- ProductStock entries guaranteed to exist
- Eliminates "ProductStock not found" errors

### 2. Simplicity
- Reduced from 4 test products to 2
- Focused on GENERAL product type (no RECIPE complexity)
- Clear, predictable product IDs (7, 19)

### 3. Maintainability
- Comprehensive documentation
- Clear troubleshooting steps
- Easy stock replenishment process

### 4. Reproducibility
- Consistent test data across environments
- SQL scripts for quick setup
- Documented verification steps

---

## Migration Guide

### For Existing Test Environments

1. **Run Test Data Creation Script**
   ```bash
   docker exec -i mysql-main mysql -umainuser -pmainpassword main_server < docker/init-scripts/02-insert-test-data.sql
   ```

2. **Verify Test Data**
   ```bash
   docker exec mysql-main mysql -umainuser -pmainpassword main_server -e "
     SELECT ps.product_id, ps.container_id, ps.usage_qty, p.name
     FROM product_stock ps
     JOIN product p ON ps.product_id = p.id
     WHERE ps.product_id IN (7, 19) AND ps.container_id = 1;
   "
   ```

3. **Update K6 Test Scripts**
   - Pull latest changes from repository
   - All test files in `k6/tests/` directory have been updated

4. **Run Smoke Test**
   ```bash
   cd k6
   k6 run tests/smoke-test.js
   ```

5. **Run Full Test Suite**
   ```bash
   .\run-all-tests.bat  # Windows
   bash run-all-tests.sh  # Linux/Mac
   ```

---

## Breaking Changes

### Product IDs Changed
If you have custom test scripts or external tools referencing the old product IDs, update them:

| Old ID | Old Name | New ID | New Name |
|--------|----------|--------|----------|
| 6 | 뉴욕 치즈케이크 | 7 | Test Product 7 |
| 7 | 초콜릿 크루아상 | 19 | Coffee Americano |
| 1 | 아메리카노(Tall) | ❌ Not used | - |
| 2 | 카페라떼(Tall) | ❌ Not used | - |

### API Endpoints
No API endpoints changed, but ensure your test data matches the expected IDs.

---

## Testing Checklist

After updating to new test data:

- [ ] Test data script executed successfully
- [ ] ProductStock verified for products 7 and 19
- [ ] Smoke test passes (100% checks)
- [ ] Stock adjustment test passes
- [ ] POS server API test passes
- [ ] Sales with stock test passes
- [ ] Stock levels replenished after load tests
- [ ] Documentation reviewed

---

## Rollback Plan

If issues occur with new test data:

1. **Restore Old Data** (if backed up):
   ```sql
   -- Restore from backup SQL file
   ```

2. **Or Recreate Test Data**:
   ```bash
   docker exec -i mysql-main mysql -umainuser -pmainpassword main_server < docker/init-scripts/02-insert-test-data.sql
   ```

3. **Verify and Retest**:
   ```bash
   k6 run tests/smoke-test.js
   ```

---

## Future Improvements

1. **Automated Stock Replenishment**
   - Add pre-test hook to reset stock levels
   - Implement in CI/CD pipeline

2. **Dynamic Test Data Generation**
   - Create setup script that generates fresh data before each test run
   - Teardown script to clean up test data

3. **Extended Product Coverage**
   - Add RECIPE type products with stock
   - Test ingredient deduction for recipe products

4. **Performance Baselines**
   - Establish performance benchmarks with new test data
   - Set up automated regression detection

---

## Contact

For questions or issues with the updated test data:
1. Check [TEST_DATA_INFO.md](./TEST_DATA_INFO.md)
2. Check [TEST_DATA_SETUP.md](../TEST_DATA_SETUP.md)
3. Review test script source code in `k6/tests/`

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 2.0.0 | 2025-10-11 | Major update: New test data (Products 7, 19) |
| 1.0.0 | Previous | Initial test scripts with mixed product IDs |
