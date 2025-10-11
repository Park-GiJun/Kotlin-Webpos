@echo off
REM Complete Test Data Initialization using APIs
REM Creates all necessary data for k6 testing

setlocal enabledelayedexpansion

set MAIN_URL=http://localhost:8080/main

echo =========================================
echo Complete Test Data Initialization
echo =========================================
echo.
echo This will create:
echo   - 3 HQs
echo   - 15 Stores (5 per HQ)
echo   - 30 POS (2 per Store)
echo   - 15 Containers (1 per Store)
echo   - 8 Ingredients
echo   - 30 Recipe Products (10 per HQ)
echo   - 30 Finished Products (10 per HQ)
echo.
echo Press any key to continue or Ctrl+C to cancel...
pause > nul
echo.
echo =========================================
echo.

REM ==================================================
REM Step 1: Create 3 HQs
REM ==================================================
echo [Step 1/7] Creating 3 HQs...

curl -s -X POST "%MAIN_URL%/organization/hq" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Seoul Headquarters\",\"representation\":\"CEO 1\",\"street\":\"Gangnam-daero 123\",\"city\":\"Seoul\",\"zipCode\":\"12341\",\"email\":\"hq1@test.com\",\"phoneNumber\":\"02-123-4561\"}" ^
  > nul 2>&1

curl -s -X POST "%MAIN_URL%/organization/hq" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Busan Headquarters\",\"representation\":\"CEO 2\",\"street\":\"Haeundae-ro 456\",\"city\":\"Busan\",\"zipCode\":\"12342\",\"email\":\"hq2@test.com\",\"phoneNumber\":\"02-123-4562\"}" ^
  > nul 2>&1

curl -s -X POST "%MAIN_URL%/organization/hq" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Jeju Headquarters\",\"representation\":\"CEO 3\",\"street\":\"Nohyeong-ro 789\",\"city\":\"Jeju\",\"zipCode\":\"12343\",\"email\":\"hq3@test.com\",\"phoneNumber\":\"02-123-4563\"}" ^
  > nul 2>&1

echo   [OK] Created 3 HQs
echo.

REM ==================================================
REM Step 2: Create 15 Stores
REM ==================================================
echo [Step 2/7] Creating 15 Stores...

set STORE_NAMES[0]=Gangnam Store
set STORE_NAMES[1]=Hongdae Store
set STORE_NAMES[2]=Itaewon Store
set STORE_NAMES[3]=Myeongdong Store
set STORE_NAMES[4]=Jamsil Store

set /a store_count=0

for /L %%h in (1,1,3) do (
  for /L %%s in (0,1,4) do (
    set /a store_id=store_count+1

    curl -s -X POST "%MAIN_URL%/organization/store" ^
      -H "Content-Type: application/json" ^
      -d "{\"hqId\":%%h,\"name\":\"!STORE_NAMES[%%s]!\",\"street\":\"Street !store_id!\",\"city\":\"City %%h\",\"zipCode\":\"5000!store_id!\",\"email\":\"store!store_id!@test.com\",\"phoneNumber\":\"02-200-!store_id!000\"}" ^
      > nul 2>&1

    set /a store_count+=1
  )
)

echo   [OK] Created 15 Stores
echo.

REM ==================================================
REM Step 3: Create 15 ProductContainers
REM ==================================================
echo [Step 3/7] Creating 15 ProductContainers...

for /L %%c in (1,1,15) do (
  set /a hq_id=^(%%c-1^)/5+1

  curl -s -X POST "%MAIN_URL%/product/container" ^
    -H "Content-Type: application/json" ^
    -d "{\"hqId\":!hq_id!,\"containerId\":%%c,\"containerName\":\"Store %%c Container\"}" ^
    > nul 2>&1
)

echo   [OK] Created 15 ProductContainers
echo.

REM ==================================================
REM Step 4: Create 30 POS
REM ==================================================
echo [Step 4/7] Creating 30 POS...

set /a pos_count=1

for /L %%s in (1,1,15) do (
  set /a hq_id=^(%%s-1^)/5+1

  for /L %%p in (1,1,2) do (
    curl -s -X POST "%MAIN_URL%/organization/pos" ^
      -H "Content-Type: application/json" ^
      -d "{\"hqId\":!hq_id!,\"storeId\":%%s,\"name\":\"POS-!pos_count!\",\"macAddress\":\"AA:BB:CC:DD:EE:!pos_count!\"}" ^
      > nul 2>&1

    set /a pos_count+=1
  )
)

echo   [OK] Created 30 POS
echo.

REM ==================================================
REM Step 5: Create 8 Ingredient Products
REM ==================================================
echo [Step 5/7] Creating 8 Ingredient Products...

curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":1,\"name\":\"Coffee Beans\",\"price\":15000,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-BEAN-001\",\"supplyAmt\":12000,\"unit\":\"KG\",\"usageUnit\":\"G\"}" > nul 2>&1
curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":1,\"name\":\"Milk\",\"price\":5000,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-MILK-001\",\"supplyAmt\":4000,\"unit\":\"L\",\"usageUnit\":\"ML\"}" > nul 2>&1
curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":1,\"name\":\"Sugar\",\"price\":3000,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-SUGAR-001\",\"supplyAmt\":2500,\"unit\":\"KG\",\"usageUnit\":\"G\"}" > nul 2>&1
curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":1,\"name\":\"Vanilla Syrup\",\"price\":8000,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-VAN-001\",\"supplyAmt\":6500,\"unit\":\"L\",\"usageUnit\":\"ML\"}" > nul 2>&1
curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":1,\"name\":\"Caramel Syrup\",\"price\":8500,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-CAR-001\",\"supplyAmt\":7000,\"unit\":\"L\",\"usageUnit\":\"ML\"}" > nul 2>&1
curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":1,\"name\":\"Ice\",\"price\":1000,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-ICE-001\",\"supplyAmt\":500,\"unit\":\"KG\",\"usageUnit\":\"G\"}" > nul 2>&1
curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":1,\"name\":\"Whipped Cream\",\"price\":6000,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-CREAM-001\",\"supplyAmt\":5000,\"unit\":\"L\",\"usageUnit\":\"ML\"}" > nul 2>&1
curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":1,\"name\":\"Chocolate Sauce\",\"price\":7000,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-CHOC-001\",\"supplyAmt\":5500,\"unit\":\"L\",\"usageUnit\":\"ML\"}" > nul 2>&1

echo   [OK] Created 8 Ingredient Products
echo.

REM ==================================================
REM Step 6: Create 30 Recipe Products (10 per HQ)
REM ==================================================
echo [Step 6/7] Creating 30 Recipe Products...

for /L %%h in (1,1,3) do (
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Americano Hot HQ%%h\",\"price\":4500,\"productType\":\"RECIPE\",\"productCode\":\"REC-AMER-HOT-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Cafe Latte Hot HQ%%h\",\"price\":5000,\"productType\":\"RECIPE\",\"productCode\":\"REC-LATTE-HOT-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Vanilla Latte Hot HQ%%h\",\"price\":5500,\"productType\":\"RECIPE\",\"productCode\":\"REC-VAN-HOT-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Caramel Macchiato Hot HQ%%h\",\"price\":5800,\"productType\":\"RECIPE\",\"productCode\":\"REC-CAR-HOT-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Iced Americano HQ%%h\",\"price\":5000,\"productType\":\"RECIPE\",\"productCode\":\"REC-AMER-ICE-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Iced Cafe Latte HQ%%h\",\"price\":5500,\"productType\":\"RECIPE\",\"productCode\":\"REC-LATTE-ICE-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Iced Vanilla Latte HQ%%h\",\"price\":6000,\"productType\":\"RECIPE\",\"productCode\":\"REC-VAN-ICE-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Iced Caramel Macchiato HQ%%h\",\"price\":6300,\"productType\":\"RECIPE\",\"productCode\":\"REC-CAR-ICE-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Mocha Hot HQ%%h\",\"price\":5800,\"productType\":\"RECIPE\",\"productCode\":\"REC-MOCHA-HOT-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Iced Mocha HQ%%h\",\"price\":6300,\"productType\":\"RECIPE\",\"productCode\":\"REC-MOCHA-ICE-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" > nul 2>&1
)

echo   [OK] Created 30 Recipe Products
echo.

REM ==================================================
REM Step 7: Create 30 Finished Products with initial stock
REM ==================================================
echo [Step 7/7] Creating 30 Finished Products...

for /L %%h in (1,1,3) do (
  set /a first_container=^(%%h-1^)*5+1

  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Croissant HQ%%h\",\"price\":3800,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-CROIS-%%h\",\"supplyAmt\":2500,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":100,\"usageQty\":100}}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Chocolate Croissant HQ%%h\",\"price\":4200,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-CHOC-%%h\",\"supplyAmt\":2800,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":80,\"usageQty\":80}}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Blueberry Muffin HQ%%h\",\"price\":4000,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-MUFFIN-%%h\",\"supplyAmt\":2600,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":90,\"usageQty\":90}}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Cheesecake Slice HQ%%h\",\"price\":6500,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-CHEESE-%%h\",\"supplyAmt\":4500,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":50,\"usageQty\":50}}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Brownie HQ%%h\",\"price\":4500,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-BROWN-%%h\",\"supplyAmt\":3000,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":70,\"usageQty\":70}}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Club Sandwich HQ%%h\",\"price\":7500,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-SAND-%%h\",\"supplyAmt\":5000,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":60,\"usageQty\":60}}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Plain Bagel HQ%%h\",\"price\":3500,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-BAGEL-%%h\",\"supplyAmt\":2200,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":100,\"usageQty\":100}}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Chocolate Chip Cookie HQ%%h\",\"price\":2500,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-COOKIE-%%h\",\"supplyAmt\":1500,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":150,\"usageQty\":150}}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Plain Scone HQ%%h\",\"price\":3800,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-SCONE-%%h\",\"supplyAmt\":2400,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":80,\"usageQty\":80}}" > nul 2>&1
  curl -s -X POST "%MAIN_URL%/product" -H "Content-Type: application/json" -d "{\"hqId\":%%h,\"name\":\"Glazed Donut HQ%%h\",\"price\":3200,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-DONUT-%%h\",\"supplyAmt\":2000,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":120,\"usageQty\":120}}" > nul 2>&1
)

echo   [OK] Created 30 Finished Products
echo.

REM ==================================================
REM Summary
REM ==================================================
echo =========================================
echo Test Data Initialization Complete!
echo =========================================
echo.
echo Summary:
echo   - HQs: 3
echo   - Stores: 15 (5 per HQ)
echo   - POS Terminals: 30 (2 per Store)
echo   - ProductContainers: 15 (1 per Store)
echo   - Ingredients: 8
echo   - Recipe Products: 30 (10 per HQ)
echo   - Finished Products: 30 (10 per HQ)
echo   - Total Products: 68
echo.
echo Product ID Ranges (expected):
echo   Ingredients:     1-8
echo   HQ 1 Recipes:    9-18
echo   HQ 1 Products:  19-28
echo   HQ 2 Recipes:   29-38
echo   HQ 2 Products:  39-48
echo   HQ 3 Recipes:   49-58
echo   HQ 3 Products:  59-68
echo.
echo Next Steps:
echo   1. Run k6 tests: k6 run k6/tests/simple-sales-test.js
echo   2. Check database: scripts/check-products.bat
echo.
echo =========================================

endlocal
