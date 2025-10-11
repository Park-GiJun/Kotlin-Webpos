# Test Data Setup Guide

## Problem

The POS server was failing with the following error when creating sales:
```
ProductStock with id productId: 7, containerId: 1 not found
```

## Solution

Created comprehensive test data for the main-server database to resolve the ProductStock not found error.

## Test Data Created

### 1. HQ (Headquarters)
- **ID**: 2
- **Name**: Test Headquarters
- **Representative**: John Doe
- **Address**: 123 Main Street, Seoul, 12345
- **Email**: hq@test.com
- **Phone**: 02-1234-5678

### 2. ProductContainer
- **ID**: 7 (database primary key)
- **Container ID**: 1 (business key - maps to Store 1)
- **Name**: Store 1 Main Container
- **HQ ID**: 2

### 3. Products
Created 7 test products (IDs 13-19):
- Products 1-6: Generic test products (TEST001-TEST006)
- **Product 7 (ID: 19)**: Coffee Americano
  - Price: 4500 KRW
  - Product Code: COFFEE001
  - Type: PRODUCT
  - Unit: CUP

### 4. ProductStock
- **Product ID**: 7
- **Container ID**: 1
- **Unit Qty**: 100.00
- **Usage Qty**: 100.00

Also created ProductStock for Product 19 (Coffee Americano) with the same quantities.

## How to Use

### Running the Test Data Script

The test data has already been inserted. To re-run or add more data:

```bash
# Using SQL script
docker exec -i mysql-main mysql -umainuser -pmainpassword main_server < docker/init-scripts/02-insert-test-data.sql

# Using Windows batch script
cd scripts
create-test-data.bat

# Using bash script (WSL/Git Bash)
cd scripts
bash create-test-data.sh
```

### Verify Test Data

```bash
# Check ProductStock for product 7
docker exec mysql-main mysql -umainuser -pmainpassword main_server -e "
  SELECT ps.product_id, ps.container_id, ps.unit_qty, ps.usage_qty, p.name
  FROM product_stock ps
  JOIN product p ON ps.product_id = p.id
  WHERE ps.product_id = 7 AND ps.container_id = 1;
"
```

### Test Stock Adjustment API

```bash
# Test the stock adjustment endpoint
curl -X PUT "http://localhost:8080/main/product-stock/product/7/store/1/adjust" \
  -H "Content-Type: application/json" \
  -d '{
    "adjustmentType": "DECREASE",
    "unitQty": 0,
    "usageQty": 1,
    "reason": "Sales transaction"
  }'
```

### Test POS Sales Creation

```bash
# Create a sales transaction
curl -X POST "http://localhost:8081/pos/sales" \
  -H "Content-Type: application/json" \
  -d '{
    "hqId": 1,
    "storeId": 1,
    "posId": 1,
    "billNo": "TEST-001",
    "saleType": true,
    "saleDate": "2025-10-11T19:45:00",
    "details": [
      {
        "productId": 7,
        "qty": 1,
        "unitPrice": 4000,
        "tax": 0,
        "discountAmt": 0
      }
    ],
    "payments": [
      {
        "paymentMethodId": "CASH",
        "paymentAmt": 4000
      }
    ]
  }'
```

## Architecture Understanding

### Domain Hierarchy
```
HQ (Headquarters)
  ├── Store
  │   ├── POS
  │   └── ProductContainer (containerId = storeId)
  └── Product
      └── ProductStock (links Product to ProductContainer)
```

### Key Relationships
- **HQ** → **Store**: One-to-many
- **Store** → **POS**: One-to-many
- **Store** → **ProductContainer**: One-to-one (containerId = storeId)
- **Product** → **ProductStock**: One-to-many (one per container)
- **ProductContainer** → **ProductStock**: One-to-many

### Stock Adjustment Flow
1. POS server creates a sales transaction
2. POS server calls Main server's stock adjustment endpoint:
   ```
   PUT /main/product-stock/product/{productId}/store/{storeId}/adjust
   ```
3. Main server converts `storeId` to `containerId` (they're the same value)
4. Main server finds ProductStock by `productId` and `containerId`
5. Main server adjusts the stock quantities
6. Main server returns the adjustment result

## Database Schema

### Table: product_stock
```sql
CREATE TABLE product_stock (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  container_id BIGINT NOT NULL,
  unit_qty DECIMAL(10,2) NOT NULL,
  usage_qty DECIMAL(10,2) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  created_by VARCHAR(255) NOT NULL,
  updated_by VARCHAR(255) NOT NULL,
  is_deleted BIT(1) NOT NULL,
  deleted_at DATETIME(6),
  deleted_by VARCHAR(255)
);
```

## Files Created

1. **docker/init-scripts/02-insert-test-data.sql** - SQL script to insert test data
2. **scripts/create-test-data.bat** - Windows batch script to create test data via API
3. **scripts/create-test-data.sh** - Bash script to create test data via API
4. **scripts/test-stock-adjustment.sh** - Script to test stock adjustment endpoint

## Notes

- The ProductStock was successfully created with productId=7 and containerId=1
- The test data resolves the original "ProductStock not found" error
- Additional test data (Product 19 - Coffee Americano) was also created for future testing
