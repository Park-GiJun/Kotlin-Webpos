# Comprehensive Test Data Documentation

## Overview

This document describes the comprehensive test data created for the POS system, including ingredients, recipe products, and ready-to-sell items.

## Test Data Structure

### Summary
- **Total Products**: 35
- **Ingredients**: 10 (커피 원두, 우유, 시럽 등)
- **Recipe Products**: 15 (음료 레시피)
- **Ready Products**: 10 (베이커리, 디저트, 음료수)
- **Recipe Compositions**: 39 (레시피 구성)
- **Containers**: 2 (Store 1, Store 2)
- **Product Stocks**: 40 (각 매장 20개씩)

---

## 1. Ingredient Products (원재료) - IDs 101-110

These are raw materials used in recipe products. Stock is tracked and automatically deducted when recipe products are sold.

| ID | Name | Code | Unit | Stock (Store 1) | Price |
|----|------|------|------|----------------|-------|
| 101 | Arabica Coffee Beans | ING-COFFEE-001 | KG (used as G) | 50kg (50000g) | 25,000원 |
| 102 | Fresh Milk | ING-MILK-001 | L (used as ML) | 200L (200000ml) | 3,000원 |
| 103 | Vanilla Syrup | ING-SYRUP-VAN | L (used as ML) | 10L (10000ml) | 8,000원 |
| 104 | Caramel Syrup | ING-SYRUP-CAR | L (used as ML) | 10L (10000ml) | 8,000원 |
| 105 | Chocolate Powder | ING-CHOCO-001 | KG (used as G) | 20kg (20000g) | 12,000원 |
| 106 | Whipped Cream | ING-CREAM-001 | L (used as ML) | 15L (15000ml) | 5,000원 |
| 107 | Ice Cubes | ING-ICE-001 | KG (used as G) | 100kg (100000g) | 1,000원 |
| 108 | Sugar | ING-SUGAR-001 | KG (used as G) | 30kg (30000g) | 3,000원 |
| 109 | Green Tea Powder | ING-GREENTEA-001 | KG (used as G) | 10kg (10000g) | 15,000원 |
| 110 | Espresso Shot | ING-ESPRESSO-001 | SHOT | 500 shots | 0원 |

### Stock Notes
- Ingredients have **generous stock levels** for extended testing
- Store 2 has 60-80% of Store 1's stock levels
- Stock is in `usageQty` field (converted to usage_unit)

---

## 2. Recipe Products (레시피 음료) - IDs 201-215

These products require ingredients and automatically trigger stock deduction when sold.

### Hot Beverages (201-205)

| ID | Name | Code | Price | Ingredients Used |
|----|------|------|-------|------------------|
| 201 | Americano (Hot) | REC-AMER-HOT | 4,500원 | Coffee 18g |
| 202 | Cafe Latte (Hot) | REC-LATTE-HOT | 5,000원 | Coffee 18g + Milk 250ml |
| 203 | Cappuccino (Hot) | REC-CAPPU-HOT | 5,000원 | Coffee 18g + Milk 180ml |
| 204 | Vanilla Latte (Hot) | REC-VANLAT-HOT | 5,500원 | Coffee 18g + Milk 250ml + Vanilla 20ml |
| 205 | Caramel Macchiato (Hot) | REC-CARMAC-HOT | 5,800원 | Coffee 18g + Milk 250ml + Caramel 20ml |

### Cold Beverages (206-209)

| ID | Name | Code | Price | Ingredients Used |
|----|------|------|-------|------------------|
| 206 | Iced Americano | REC-AMER-ICE | 5,000원 | Coffee 18g + Ice 100g |
| 207 | Iced Cafe Latte | REC-LATTE-ICE | 5,500원 | Coffee 18g + Milk 250ml + Ice 100g |
| 208 | Iced Vanilla Latte | REC-VANLAT-ICE | 6,000원 | Coffee 18g + Milk 250ml + Vanilla 20ml + Ice 100g |
| 209 | Iced Caramel Macchiato | REC-CARMAC-ICE | 6,300원 | Coffee 18g + Milk 250ml + Caramel 20ml + Ice 100g |

### Specialty Drinks (210-215)

| ID | Name | Code | Price | Ingredients Used |
|----|------|------|-------|------------------|
| 210 | Mocha (Hot) | REC-MOCHA-HOT | 5,800원 | Coffee 18g + Milk 220ml + Chocolate 30g |
| 211 | Iced Mocha | REC-MOCHA-ICE | 6,300원 | Coffee 18g + Milk 220ml + Chocolate 30g + Ice 100g |
| 212 | Green Tea Latte (Hot) | REC-GREENTEA-HOT | 5,500원 | Green Tea 15g + Milk 250ml |
| 213 | Iced Green Tea Latte | REC-GREENTEA-ICE | 6,000원 | Green Tea 15g + Milk 250ml + Ice 100g |
| 214 | Affogato | REC-AFFOGATO | 6,500원 | Espresso 2 shots + Cream 50ml |
| 215 | Espresso (Double Shot) | REC-ESPRESSO-DBL | 4,000원 | Coffee 36g |

### Recipe Product Behavior
- ✅ **Automatic Stock Deduction**: When a recipe product is sold, ingredients are automatically deducted
- ✅ **No Direct Stock**: Recipe products don't have their own stock entries
- ✅ **Validation**: System checks if sufficient ingredients are available before sale

---

## 3. Ready Products (완제품) - IDs 301-310

These are ready-to-sell items with direct stock tracking.

### Bakery Items (301-305)

| ID | Name | Code | Price | Stock (Store 1) |
|----|------|------|-------|----------------|
| 301 | Croissant | PRD-CROIS-001 | 3,800원 | 100개 |
| 302 | Chocolate Croissant | PRD-CHOCRO-001 | 4,200원 | 80개 |
| 303 | Blueberry Muffin | PRD-MUFFIN-BLU | 4,000원 | 90개 |
| 304 | Banana Bread | PRD-BANANA-001 | 4,500원 | 70개 |
| 305 | Bagel Plain | PRD-BAGEL-PLN | 3,500원 | 100개 |

### Desserts (306-308)

| ID | Name | Code | Price | Stock (Store 1) |
|----|------|------|-------|----------------|
| 306 | Cheesecake Slice | PRD-CHEESE-001 | 6,500원 | 50개 |
| 307 | Tiramisu | PRD-TIRAMISU-001 | 7,000원 | 40개 |
| 308 | Brownie | PRD-BROWNIE-001 | 4,500원 | 80개 |

### Bottled Drinks (309-310)

| ID | Name | Code | Price | Stock (Store 1) |
|----|------|------|-------|----------------|
| 309 | Orange Juice (Bottled) | PRD-JUICE-ORA | 4,000원 | 150개 |
| 310 | Mineral Water | PRD-WATER-001 | 2,000원 | 200개 |

---

## 4. Stock Management

### Container Mapping
- **Container 1** = Store 1 (Gangnam Branch)
- **Container 2** = Store 2 (Hongdae Branch)

### Stock Levels by Store

#### Store 1 (Container 1) - Primary Testing Store
- Ingredients: **Very generous** (supports 500+ recipe sales)
- Products: 40-200 units per item
- Total investment: ~15 million won in ingredients + products

#### Store 2 (Container 2) - Secondary Store
- Ingredients: 60-80% of Store 1
- Products: 60-80% of Store 1
- For testing multi-store scenarios

---

## 5. Usage in Tests

### K6 Test Scripts Configuration

#### sales-with-stock.js
**Products Used** (12 items):
- 7 Recipe beverages (201, 202, 204, 205, 206, 207, 209)
- 5 Ready products (301, 302, 303, 306, 308)

**Test Scenario**:
```javascript
// Random product selection includes both RECIPE and PRODUCT types
// RECIPE sales will auto-deduct ingredients
// PRODUCT sales will deduct product stock
```

#### pos-server-api.js
**Products Used** (5 items):
- 3 Recipe beverages (201, 206, 202)
- 2 Ready products (301, 306)

#### stock-adjustment.js
**Products Used**:
- Ingredient products (101-105)
- Tests INCREASE and DECREASE operations

---

## 6. Creating Test Data

### Automated Setup

```bash
# Run comprehensive test data script
docker exec -i mysql-main mysql -umainuser -pmainpassword main_server < docker/init-scripts/03-comprehensive-test-data.sql
```

### Verification

```bash
# Check product counts
docker exec mysql-main mysql -umainuser -pmainpassword main_server -e "
  SELECT product_type, COUNT(*) as count
  FROM product
  WHERE id >= 101
  GROUP BY product_type;
"

# Check recipe compositions
docker exec mysql-main mysql -umainuser -pmainpassword main_server -e "
  SELECT COUNT(*) as recipe_count
  FROM recipe_composition
  WHERE recipe_product_id >= 201;
"

# Check stock for Store 1
docker exec mysql-main mysql -umainuser -pmainpassword main_server -e "
  SELECT p.product_type, COUNT(*) as items_with_stock
  FROM product_stock ps
  JOIN product p ON ps.product_id = p.id
  WHERE ps.container_id = 1 AND p.id >= 101
  GROUP BY p.product_type;
"
```

---

## 7. Testing Scenarios

### Scenario 1: Recipe Product Sale (Americano)
**Action**: Sell 1 Americano (Product 201)

**Expected**:
```
Sales Record Created:
- Product: 201 (Americano Hot)
- Quantity: 1
- Amount: 4500원

Ingredient Stock Deducted:
- Coffee Beans (101): -18g
- Final Stock: 49,982g
```

### Scenario 2: Complex Recipe (Iced Caramel Macchiato)
**Action**: Sell 1 Iced Caramel Macchiato (Product 209)

**Expected**:
```
Sales Record Created:
- Product: 209
- Quantity: 1
- Amount: 6300원

Ingredients Deducted:
- Coffee Beans (101): -18g
- Milk (102): -250ml
- Caramel Syrup (104): -20ml
- Ice (107): -100g
```

### Scenario 3: Ready Product Sale (Croissant)
**Action**: Sell 1 Croissant (Product 301)

**Expected**:
```
Sales Record Created:
- Product: 301
- Quantity: 1
- Amount: 3800원

Product Stock Deducted:
- Croissant (301): -1ea
- Final Stock: 99ea
```

### Scenario 4: High Volume Load Test
**Load**: 50 concurrent users × 100 sales each = 5000 sales

**With Current Stock**:
- Ingredient depletion: ~10-20% of stock
- Product depletion: ~50-100 units
- All sales should complete successfully
- No stock-out errors expected

---

## 8. Stock Replenishment

### After Load Testing

```bash
# Check remaining stock
docker exec mysql-main mysql -umainuser -pmainpassword main_server -e "
  SELECT
    p.id,
    p.name,
    ps.usage_qty,
    p.usage_unit
  FROM product_stock ps
  JOIN product p ON ps.product_id = p.id
  WHERE ps.container_id = 1
    AND p.product_type = 'INGREDIENT'
  ORDER BY ps.usage_qty ASC
  LIMIT 5;
"

# Replenish specific ingredient (example: Coffee)
curl -X PUT "http://localhost:8080/main/product-stock/product/101/store/1/adjust" \
  -H "Content-Type: application/json" \
  -d '{
    "adjustmentType": "INCREASE",
    "unitQty": 50,
    "usageQty": 50000,
    "reason": "Weekly replenishment"
  }'
```

### Bulk Replenishment Script

```sql
-- Replenish all ingredients to initial levels
UPDATE product_stock ps
JOIN product p ON ps.product_id = p.id
SET ps.usage_qty = CASE ps.product_id
    WHEN 101 THEN 50000.00  -- Coffee
    WHEN 102 THEN 200000.00 -- Milk
    WHEN 103 THEN 10000.00  -- Vanilla
    WHEN 104 THEN 10000.00  -- Caramel
    WHEN 105 THEN 20000.00  -- Chocolate
    WHEN 106 THEN 15000.00  -- Cream
    WHEN 107 THEN 100000.00 -- Ice
    WHEN 108 THEN 30000.00  -- Sugar
    WHEN 109 THEN 10000.00  -- Green Tea
    WHEN 110 THEN 500.00    -- Espresso
END,
ps.updated_at = NOW()
WHERE ps.container_id = 1
  AND ps.product_id BETWEEN 101 AND 110;
```

---

## 9. Troubleshooting

### Issue: "ProductStock not found"
**Cause**: Missing stock entry for ingredient or product

**Solution**:
```bash
# Re-run comprehensive data script
docker exec -i mysql-main mysql -umainuser -pmainpassword main_server < docker/init-scripts/03-comprehensive-test-data.sql
```

### Issue: "Insufficient stock" for Ingredients
**Cause**: High-volume testing depleted ingredient stock

**Solution**:
```bash
# Quick replenishment for all ingredients
docker exec mysql-main mysql -umainuser -pmainpassword main_server -e "
  UPDATE product_stock ps
  SET ps.usage_qty = ps.usage_qty * 10,
      ps.updated_at = NOW()
  WHERE ps.container_id = 1
    AND ps.product_id BETWEEN 101 AND 110;
"
```

### Issue: Recipe Product Sale Fails
**Cause**: Missing recipe composition or ingredient stock

**Check**:
```bash
# Verify recipe exists
docker exec mysql-main mysql -umainuser -pmainpassword main_server -e "
  SELECT rc.*, ip.name as ingredient
  FROM recipe_composition rc
  JOIN product ip ON rc.ingredient_product_id = ip.id
  WHERE rc.recipe_product_id = 201;
"

# Verify ingredient stock
docker exec mysql-main mysql -umainuser -pmainpassword main_server -e "
  SELECT ps.*, p.name
  FROM product_stock ps
  JOIN product p ON ps.product_id = p.id
  WHERE ps.product_id IN (101, 102, 107)
    AND ps.container_id = 1;
"
```

---

## 10. Benefits of This Data Structure

### Comprehensive Testing
✅ Tests both RECIPE and PRODUCT types
✅ Tests automatic ingredient deduction
✅ Tests multi-level stock management
✅ Supports high-volume load testing

### Realistic Scenario
✅ Mimics real coffee shop operations
✅ Proper ingredient-to-recipe relationships
✅ Realistic stock levels and prices
✅ Multiple product categories

### Flexible
✅ Easy to add new products
✅ Easy to modify recipes
✅ Easy to adjust stock levels
✅ Supports multiple stores

### Well-Documented
✅ Clear product IDs and codes
✅ Documented relationships
✅ Example scenarios
✅ Troubleshooting guide

---

## Related Documentation

- [TEST_DATA_SETUP.md](./TEST_DATA_SETUP.md) - Original simple test data
- [k6/TEST_DATA_INFO.md](./k6/TEST_DATA_INFO.md) - K6 test data guide
- [k6/CHANGELOG_K6_TESTS.md](./k6/CHANGELOG_K6_TESTS.md) - K6 test changelog

---

## Quick Reference

### Product ID Ranges
- **101-110**: Ingredients (원재료)
- **201-215**: Recipe Products (레시피 음료)
- **301-310**: Ready Products (완제품)

### Container IDs
- **1**: Store 1 - Gangnam Branch (Primary testing)
- **2**: Store 2 - Hongdae Branch (Secondary testing)

### HQ ID
- **1**: Test Coffee Chain HQ

### Key API Endpoints
```
POST   /main/organization/hq              # Create HQ
GET    /main/product/{id}                  # Get Product
PUT    /main/product-stock/product/{productId}/store/{storeId}/adjust  # Adjust Stock
POST   /pos/sales                          # Create Sales (triggers stock deduction)
GET    /pos/sales/bill/{billNo}           # Get Sales by Bill
```
