# Multi-Tier Sales Load Testing Guide

Complete guide for running comprehensive load tests on the multi-tier POS system.

## 📋 Test Architecture

### Organizational Structure
```
3 HQs (Seoul, Busan, Jeju)
├── 5 Stores per HQ (15 total)
│   ├── Store 1-5:   Seoul HQ
│   ├── Store 6-10:  Busan HQ
│   └── Store 11-15: Jeju HQ
└── 2 POS per Store (30 total)
    ├── POS 1-10:   Seoul HQ stores
    ├── POS 11-20:  Busan HQ stores
    └── POS 21-30:  Jeju HQ stores
```

### Product Catalog
- **8 Ingredients**: Shared across all HQs (Coffee Beans, Milk, Sugar, Vanilla Syrup, Caramel Syrup, Ice, Whipped Cream, Chocolate Sauce)
- **30 Recipe Products**: 10 per HQ (beverages that consume ingredients)
  - Hot beverages: Americano, Latte, Vanilla Latte, Caramel Macchiato, Mocha
  - Cold beverages: Iced versions of the above
- **30 Finished Products**: 10 per HQ (ready-to-sell items)
  - Bakery: Croissant, Chocolate Croissant, Muffin, Bagel, Scone, Cookie, Donut
  - Desserts: Cheesecake, Brownie, Sandwich

### Stock Management
- Each store has its own ProductContainer (containerId = storeId)
- Recipe products automatically deduct ingredients from store inventory
- Finished products directly deduct from store stock
- All stock operations are tracked per container

## 🚀 Quick Start

### Prerequisites

1. **Running servers**:
   ```bash
   # Main server on port 8080
   # POS server on port 8081
   ```

2. **k6 installed**:
   ```bash
   # Windows (Chocolatey)
   choco install k6

   # macOS (Homebrew)
   brew install k6

   # Linux
   wget https://github.com/grafana/k6/releases/download/v0.48.0/k6-v0.48.0-linux-amd64.tar.gz
   tar -xzf k6-v0.48.0-linux-amd64.tar.gz
   sudo cp k6-v0.48.0-linux-amd64/k6 /usr/local/bin/
   ```

3. **Create reports directory**:
   ```bash
   mkdir -p reports/k6
   ```

### Step 1: Create Test Data

Run the test data creation script to set up the complete organizational structure:

```bash
# Windows
cd scripts
create-multi-tier-test-data.bat

# Linux/macOS
cd scripts
chmod +x create-multi-tier-test-data.sh
./create-multi-tier-test-data.sh
```

This script will create:
- ✓ 3 HQs
- ✓ 15 Stores (5 per HQ)
- ✓ 30 POS terminals (2 per Store)
- ✓ 15 ProductContainers (1 per Store)
- ✓ 8 Ingredient products
- ✓ 30 Recipe products (10 per HQ)
- ✓ 30 Finished products with initial stock (10 per HQ)

**Note**: The script creates initial stock for finished products at the first store of each HQ. You may need to add stock to other stores or add recipes to recipe products manually.

### Step 2: Configure Stock (Optional)

If you need to add stock to additional stores or configure recipes:

#### Add Stock to Stores
```bash
# Example: Add stock to Store 2 (containerId = 2)
curl -X POST http://localhost:8080/main/product/stock-adjustment \
  -H "Content-Type: application/json" \
  -d '{
    "containerId": 2,
    "productId": 19,
    "adjustmentType": "IN",
    "unitQty": 100,
    "usageQty": 100,
    "reason": "Initial stock for testing"
  }'
```

#### Add Recipes to Recipe Products
Recipe products need their ingredient compositions defined. You'll need to add this via your admin interface or directly in the database.

Example recipe for Americano (Hot):
- Coffee Beans: 18g
- Water: 250ml

Example recipe for Cafe Latte (Hot):
- Coffee Beans: 18g
- Milk: 200ml
- Water: 100ml

### Step 3: Run Load Test

Execute the k6 load test:

```bash
cd k6
k6 run tests/multi-tier-sales-test.js
```

With custom server URLs:
```bash
k6 run tests/multi-tier-sales-test.js \
  -e MAIN_URL=http://your-main-server:8080 \
  -e POS_URL=http://your-pos-server:8081
```

## 📊 Test Scenarios

### Load Profile

The test simulates realistic POS usage patterns:

| Stage | Duration | Target VUs | Description |
|-------|----------|------------|-------------|
| Warm-up | 1 min | 10 | System initialization |
| Normal | 3 min | 30 | Normal business hours (all 30 POS active) |
| Peak | 3 min | 50 | Peak hours (some POS handling multiple transactions) |
| Cool-down | 2 min | 30 | Return to normal |
| Shutdown | 1 min | 0 | Graceful termination |

**Total Duration**: 10 minutes

### Transaction Simulation

Each virtual user (VU) represents a POS terminal processing sales:

1. **Random POS Selection**: Each transaction randomly selects one of 30 POS terminals
2. **Product Selection**: Randomly selects 1-3 products from the HQ's catalog
3. **Transaction Types**:
   - Recipe products (beverages): Triggers ingredient deduction
   - Finished products (food): Triggers direct stock deduction
4. **Payment**: Card payment for simplicity
5. **Timing**: 1-4 second delay between transactions (realistic cashier speed)

### Example Transaction Flow

```
1. Select POS-5 (Store 3, Seoul HQ)
2. Select products from Seoul HQ catalog:
   - Iced Americano (Recipe) - ₩5,000
   - Croissant (Product) - ₩3,800
3. Create sale with 2 line items
4. Process card payment: ₩8,800
5. Stock deductions:
   - Americano ingredients: Coffee Beans (18g), Ice (100g)
   - Croissant: -1 EA from Store 3 inventory
```

## 📈 Metrics and Thresholds

### Performance Thresholds

| Metric | Threshold | Description |
|--------|-----------|-------------|
| HTTP Request Duration (P95) | < 3000ms | 95% of requests under 3 seconds |
| Sales Transaction Duration (P90) | < 4000ms | 90% of sales under 4 seconds |
| Sales Creation API (P95) | < 2500ms | Sales API responds within 2.5s |
| Error Rate | < 10% | Less than 10% transaction failures |

### Custom Metrics

- **sales_count**: Total completed sales transactions
- **stock_deductions**: Number of stock deduction operations
- **recipe_sales**: Sales of recipe products (beverages)
- **product_sales**: Sales of finished products (food)
- **active_pos_terminals**: Number of active POS terminals

## 📋 Test Reports

After the test completes, reports are generated in `reports/k6/`:

### 1. Console Output
Real-time metrics displayed during test execution:
- Sales transaction count
- Stock deductions
- Performance metrics (avg, p90, p95)
- Error rate

### 2. JSON Report
Detailed metrics in JSON format:
```
reports/k6/multi-tier-sales-YYYY-MM-DDTHH-MM-SS.json
```

Contains:
- All metric values (min, max, avg, percentiles)
- VU statistics
- HTTP request details
- Custom metric data

### 3. HTML Report
Beautiful, interactive HTML report:
```
reports/k6/multi-tier-sales-YYYY-MM-DDTHH-MM-SS.html
```

Features:
- Visual metric cards
- Color-coded status indicators
- Performance charts
- Test configuration summary

## 🔍 Verification

After running the test, verify the following:

### 1. Check Stock Levels

```sql
-- Check stock levels for all stores
SELECT
  pc.container_id,
  pc.container_name,
  p.product_code,
  p.name,
  ps.unit_qty,
  ps.usage_qty
FROM product_stock ps
JOIN product_container pc ON ps.container_id = pc.container_id
JOIN product p ON ps.product_id = p.id
ORDER BY pc.container_id, p.product_type, p.name;
```

### 2. Check Sales Records

```sql
-- Count sales by store
SELECT
  store_id,
  COUNT(*) as sales_count,
  SUM(total_sale_amt) as total_amount
FROM sales_header
GROUP BY store_id
ORDER BY store_id;
```

### 3. Check Recipe Ingredient Deductions

```sql
-- Check ingredient stock levels
SELECT
  p.name,
  p.product_code,
  SUM(ps.usage_qty) as total_stock
FROM product_stock ps
JOIN product p ON ps.product_id = p.id
WHERE p.product_type = 'INGREDIENT'
GROUP BY p.id, p.name, p.product_code;
```

## 🛠️ Troubleshooting

### Common Issues

#### 1. High Error Rate

**Symptoms**: Error rate > 10%

**Possible Causes**:
- Insufficient stock at stores
- Recipe not configured for recipe products
- Database connection pool exhausted
- Network timeout

**Solutions**:
```bash
# Add more stock
curl -X POST http://localhost:8080/main/product/stock-adjustment \
  -H "Content-Type: application/json" \
  -d '{
    "containerId": 1,
    "productId": 19,
    "adjustmentType": "IN",
    "unitQty": 1000,
    "usageQty": 1000
  }'

# Check database connection pool settings
# Increase timeout in k6 test
```

#### 2. Slow Response Times

**Symptoms**: P95 > 3000ms

**Possible Causes**:
- Database query optimization needed
- Insufficient server resources
- Network latency

**Solutions**:
- Add database indexes
- Increase server memory/CPU
- Run test closer to servers
- Reduce concurrent VUs

#### 3. Product Not Found Errors

**Symptoms**: 404 errors for product IDs

**Possible Causes**:
- Test data not created properly
- Product IDs don't match expected range

**Solutions**:
- Re-run test data creation script
- Verify product IDs in database
- Update product ID ranges in test script

## 🎯 Advanced Configuration

### Custom Load Profiles

Edit `k6/tests/multi-tier-sales-test.js`:

```javascript
export const options = {
  stages: [
    { duration: '2m', target: 20 },   // Custom ramp-up
    { duration: '5m', target: 100 },  // Higher load
    { duration: '2m', target: 0 },    // Cool down
  ],
};
```

### Test Specific Stores

Modify the `generatePOSEnvironments()` function to filter specific stores:

```javascript
const generatePOSEnvironments = () => {
  const environments = [];

  // Only test Seoul HQ (HQ ID 1)
  for (let hqId = 1; hqId <= 1; hqId++) {
    // ... rest of code
  }

  return environments;
};
```

### Test Specific Products

Filter products in the test script:

```javascript
// Only test recipe products
const hqProducts = products.filter(p => p.hqId === hqId && p.type === 'RECIPE');

// Only test finished products
const hqProducts = products.filter(p => p.hqId === hqId && p.type === 'PRODUCT');
```

## 📚 Additional Resources

- [k6 Documentation](https://k6.io/docs/)
- [k6 Load Testing Best Practices](https://k6.io/docs/testing-guides/load-testing-best-practices/)
- [Understanding k6 Results](https://k6.io/docs/using-k6/metrics/)

## 📝 Test Data Summary

### Product ID Ranges (Estimated)

| Product Type | ID Range | Count | Description |
|--------------|----------|-------|-------------|
| Ingredients | 1-8 | 8 | Shared ingredients |
| Seoul Recipes | 9-18 | 10 | Seoul HQ beverages |
| Seoul Products | 19-28 | 10 | Seoul HQ food items |
| Busan Recipes | 29-38 | 10 | Busan HQ beverages |
| Busan Products | 39-48 | 10 | Busan HQ food items |
| Jeju Recipes | 49-58 | 10 | Jeju HQ beverages |
| Jeju Products | 59-68 | 10 | Jeju HQ food items |

**Note**: Actual IDs may vary based on database auto-increment settings.

### Store and Container IDs

| HQ | Store IDs | Container IDs | POS IDs |
|----|-----------|---------------|---------|
| Seoul | 1-5 | 1-5 | 1-10 |
| Busan | 6-10 | 6-10 | 11-20 |
| Jeju | 11-15 | 11-15 | 21-30 |

## 🎉 Success Criteria

A successful test run should show:

- ✅ Error rate < 10%
- ✅ P95 response time < 3s
- ✅ All 30 POS terminals processing transactions
- ✅ Stock levels decreased appropriately
- ✅ Sales records created for all transactions
- ✅ Recipe ingredient deductions working correctly
- ✅ No database deadlocks or connection errors

---

**Happy Testing! 🚀**
