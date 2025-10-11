@echo off
REM Multi-Tier Test Data Creation Script
REM Creates: 3 HQs, each with 5 Stores, each Store with 2 POS
REM Products: 10 Recipe products + 10 Finished products per HQ
REM Each recipe contains multiple ingredients with stock tracking

setlocal enabledelayedexpansion

set BASE_URL=http://localhost:8080/main
set LOG_FILE=test-data-creation.log

echo ========================================= > %LOG_FILE%
echo Multi-Tier Test Data Creation >> %LOG_FILE%
echo Started at: %DATE% %TIME% >> %LOG_FILE%
echo ========================================= >> %LOG_FILE%

echo =========================================
echo Multi-Tier Test Data Creation
echo =========================================
echo.
echo Structure:
echo - 3 HQs
echo - 5 Stores per HQ (15 total)
echo - 2 POS per Store (30 total)
echo - 10 Recipe products per HQ (with ingredients)
echo - 10 Finished products per HQ
echo =========================================
echo.

REM Arrays to store IDs
set /a hq_count=0
set /a store_count=0
set /a pos_count=0
set /a container_count=0

REM ==================================================
REM Step 1: Create 3 HQs
REM ==================================================
echo.
echo [Step 1] Creating 3 HQs...
echo.

for /L %%h in (1,1,3) do (
    echo Creating HQ %%h...

    if %%h==1 (
        set "hq_name=Seoul Headquarters"
        set "hq_city=Seoul"
        set "hq_street=Gangnam-daero 123"
    ) else if %%h==2 (
        set "hq_name=Busan Headquarters"
        set "hq_city=Busan"
        set "hq_street=Haeundae-ro 456"
    ) else (
        set "hq_name=Jeju Headquarters"
        set "hq_city=Jeju"
        set "hq_street=Nohyeong-ro 789"
    )

    curl -s -X POST "%BASE_URL%/organization/hq" ^
        -H "Content-Type: application/json" ^
        -d "{\"name\":\"!hq_name!\",\"representation\":\"CEO %%h\",\"street\":\"!hq_street!\",\"city\":\"!hq_city!\",\"zipCode\":\"1234%%h\",\"email\":\"hq%%h@test.com\",\"phoneNumber\":\"02-123-456%%h\"}" ^
        > temp_hq_%%h.json

    set /a hq_count+=1
    echo   HQ %%h created: !hq_name!
    echo [HQ %%h] !hq_name! created >> %LOG_FILE%
)

echo.
echo [OK] Created %hq_count% HQs
echo.

REM ==================================================
REM Step 2: Create 5 Stores per HQ (15 total)
REM ==================================================
echo.
echo [Step 2] Creating 5 Stores per HQ (15 total)...
echo.

set /a global_store_id=1

for /L %%h in (1,1,3) do (
    echo Creating 5 Stores for HQ %%h...

    for /L %%s in (1,1,5) do (
        if %%s==1 (set "store_name=Gangnam Store")
        if %%s==2 (set "store_name=Hongdae Store")
        if %%s==3 (set "store_name=Itaewon Store")
        if %%s==4 (set "store_name=Myeongdong Store")
        if %%s==5 (set "store_name=Jamsil Store")

        curl -s -X POST "%BASE_URL%/organization/store" ^
            -H "Content-Type: application/json" ^
            -d "{\"hqId\":%%h,\"name\":\"!store_name!\",\"street\":\"Street !global_store_id!\",\"city\":\"City %%h\",\"zipCode\":\"5000!global_store_id!\",\"email\":\"store!global_store_id!@test.com\",\"phoneNumber\":\"02-200-!global_store_id!000\"}" ^
            > temp_store_!global_store_id!.json 2>&1

        set /a store_count+=1
        echo   Store !global_store_id! HQ %%h: !store_name!
        echo [STORE !global_store_id!] HQ %%h - !store_name! >> %LOG_FILE%

        set /a global_store_id+=1
    )
    echo.
)

echo [OK] Created %store_count% Stores
echo.

REM ==================================================
REM Step 3: Create ProductContainers (one per Store)
REM ==================================================
echo.
echo [Step 3] Creating ProductContainers for each Store...
echo.

REM HQ 1: Stores 1-5
for /L %%s in (1,1,5) do (
    curl -s -X POST "%BASE_URL%/product/container" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":1,\"containerId\":%%s,\"containerName\":\"Store %%s Container\"}" ^
        > temp_container_%%s.json 2>&1

    set /a container_count+=1
    echo   Container %%s for Store %%s (HQ 1)
    echo [CONTAINER %%s] Store %%s (HQ 1) >> %LOG_FILE%
)

REM HQ 2: Stores 6-10
for /L %%s in (6,1,10) do (
    curl -s -X POST "%BASE_URL%/product/container" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":2,\"containerId\":%%s,\"containerName\":\"Store %%s Container\"}" ^
        > temp_container_%%s.json 2>&1

    set /a container_count+=1
    echo   Container %%s for Store %%s (HQ 2)
    echo [CONTAINER %%s] Store %%s (HQ 2) >> %LOG_FILE%
)

REM HQ 3: Stores 11-15
for /L %%s in (11,1,15) do (
    curl -s -X POST "%BASE_URL%/product/container" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":3,\"containerId\":%%s,\"containerName\":\"Store %%s Container\"}" ^
        > temp_container_%%s.json 2>&1

    set /a container_count+=1
    echo   Container %%s for Store %%s (HQ 3)
    echo [CONTAINER %%s] Store %%s (HQ 3) >> %LOG_FILE%
)

echo.
echo [OK] Created %container_count% ProductContainers
echo.

REM ==================================================
REM Step 4: Create 2 POS per Store (30 total)
REM ==================================================
echo.
echo [Step 4] Creating 2 POS per Store (30 total)...
echo.

set /a global_pos_id=1

REM HQ 1: Stores 1-5, POS 1-10
for /L %%s in (1,1,5) do (
    for /L %%p in (1,1,2) do (
        curl -s -X POST "%BASE_URL%/organization/pos" ^
            -H "Content-Type: application/json" ^
            -d "{\"hqId\":1,\"storeId\":%%s,\"name\":\"POS-!global_pos_id!\",\"macAddress\":\"AA:BB:CC:DD:EE:!global_pos_id!\"}" ^
            > temp_pos_!global_pos_id!.json 2>&1

        set /a pos_count+=1
        echo   POS !global_pos_id! for Store %%s (HQ 1)
        echo [POS !global_pos_id!] Store %%s (HQ 1) >> %LOG_FILE%

        set /a global_pos_id+=1
    )
)

REM HQ 2: Stores 6-10, POS 11-20
for /L %%s in (6,1,10) do (
    for /L %%p in (1,1,2) do (
        curl -s -X POST "%BASE_URL%/organization/pos" ^
            -H "Content-Type: application/json" ^
            -d "{\"hqId\":2,\"storeId\":%%s,\"name\":\"POS-!global_pos_id!\",\"macAddress\":\"AA:BB:CC:DD:EE:!global_pos_id!\"}" ^
            > temp_pos_!global_pos_id!.json 2>&1

        set /a pos_count+=1
        echo   POS !global_pos_id! for Store %%s (HQ 2)
        echo [POS !global_pos_id!] Store %%s (HQ 2) >> %LOG_FILE%

        set /a global_pos_id+=1
    )
)

REM HQ 3: Stores 11-15, POS 21-30
for /L %%s in (11,1,15) do (
    for /L %%p in (1,1,2) do (
        curl -s -X POST "%BASE_URL%/organization/pos" ^
            -H "Content-Type: application/json" ^
            -d "{\"hqId\":3,\"storeId\":%%s,\"name\":\"POS-!global_pos_id!\",\"macAddress\":\"AA:BB:CC:DD:EE:!global_pos_id!\"}" ^
            > temp_pos_!global_pos_id!.json 2>&1

        set /a pos_count+=1
        echo   POS !global_pos_id! for Store %%s (HQ 3)
        echo [POS !global_pos_id!] Store %%s (HQ 3) >> %LOG_FILE%

        set /a global_pos_id+=1
    )
)

echo [OK] Created %pos_count% POS terminals
echo.

REM ==================================================
REM Step 5: Create Ingredient Products (shared across HQs)
REM ==================================================
echo.
echo [Step 5] Creating Ingredient Products...
echo.

set /a ingredient_start=1
set /a product_counter=1

REM Common ingredients used in recipes
echo Creating Coffee Beans...
curl -s -X POST "%BASE_URL%/product" ^
    -H "Content-Type: application/json" ^
    -d "{\"hqId\":1,\"name\":\"Coffee Beans\",\"price\":15000,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-BEAN-001\",\"supplyAmt\":12000,\"unit\":\"KG\",\"usageUnit\":\"G\"}" ^
    > nul
set /a product_counter+=1

echo Creating Milk...
curl -s -X POST "%BASE_URL%/product" ^
    -H "Content-Type: application/json" ^
    -d "{\"hqId\":1,\"name\":\"Milk\",\"price\":5000,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-MILK-001\",\"supplyAmt\":4000,\"unit\":\"L\",\"usageUnit\":\"ML\"}" ^
    > nul
set /a product_counter+=1

echo Creating Sugar...
curl -s -X POST "%BASE_URL%/product" ^
    -H "Content-Type: application/json" ^
    -d "{\"hqId\":1,\"name\":\"Sugar\",\"price\":3000,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-SUGAR-001\",\"supplyAmt\":2500,\"unit\":\"KG\",\"usageUnit\":\"G\"}" ^
    > nul
set /a product_counter+=1

echo Creating Vanilla Syrup...
curl -s -X POST "%BASE_URL%/product" ^
    -H "Content-Type: application/json" ^
    -d "{\"hqId\":1,\"name\":\"Vanilla Syrup\",\"price\":8000,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-VAN-001\",\"supplyAmt\":6500,\"unit\":\"L\",\"usageUnit\":\"ML\"}" ^
    > nul
set /a product_counter+=1

echo Creating Caramel Syrup...
curl -s -X POST "%BASE_URL%/product" ^
    -H "Content-Type: application/json" ^
    -d "{\"hqId\":1,\"name\":\"Caramel Syrup\",\"price\":8500,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-CAR-001\",\"supplyAmt\":7000,\"unit\":\"L\",\"usageUnit\":\"ML\"}" ^
    > nul
set /a product_counter+=1

echo Creating Ice...
curl -s -X POST "%BASE_URL%/product" ^
    -H "Content-Type: application/json" ^
    -d "{\"hqId\":1,\"name\":\"Ice\",\"price\":1000,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-ICE-001\",\"supplyAmt\":500,\"unit\":\"KG\",\"usageUnit\":\"G\"}" ^
    > nul
set /a product_counter+=1

echo Creating Whipped Cream...
curl -s -X POST "%BASE_URL%/product" ^
    -H "Content-Type: application/json" ^
    -d "{\"hqId\":1,\"name\":\"Whipped Cream\",\"price\":6000,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-CREAM-001\",\"supplyAmt\":5000,\"unit\":\"L\",\"usageUnit\":\"ML\"}" ^
    > nul
set /a product_counter+=1

echo Creating Chocolate Sauce...
curl -s -X POST "%BASE_URL%/product" ^
    -H "Content-Type: application/json" ^
    -d "{\"hqId\":1,\"name\":\"Chocolate Sauce\",\"price\":7000,\"productType\":\"INGREDIENT\",\"productCode\":\"ING-CHOC-001\",\"supplyAmt\":5500,\"unit\":\"L\",\"usageUnit\":\"ML\"}" ^
    > nul
set /a product_counter+=1

echo.
echo [OK] Created 8 ingredient products
echo.

REM ==================================================
REM Step 6: Create Recipe Products for each HQ
REM ==================================================
echo.
echo [Step 6] Creating 10 Recipe Products per HQ (30 total)...
echo.

for /L %%h in (1,1,3) do (
    echo Creating Recipe Products for HQ %%h...

    REM Recipe 1: Americano Hot
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Americano Hot HQ%%h\",\"price\":4500,\"productType\":\"RECIPE\",\"productCode\":\"REC-AMER-HOT-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" ^
        > nul 2>&1
    set /a product_counter+=1

    REM Recipe 2: Cafe Latte Hot
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Cafe Latte Hot HQ%%h\",\"price\":5000,\"productType\":\"RECIPE\",\"productCode\":\"REC-LATTE-HOT-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" ^
        > nul 2>&1
    set /a product_counter+=1

    REM Recipe 3: Vanilla Latte Hot
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Vanilla Latte Hot HQ%%h\",\"price\":5500,\"productType\":\"RECIPE\",\"productCode\":\"REC-VAN-HOT-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" ^
        > nul 2>&1
    set /a product_counter+=1

    REM Recipe 4: Caramel Macchiato Hot
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Caramel Macchiato Hot HQ%%h\",\"price\":5800,\"productType\":\"RECIPE\",\"productCode\":\"REC-CAR-HOT-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" ^
        > nul 2>&1
    set /a product_counter+=1

    REM Recipe 5: Americano Iced
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Iced Americano HQ%%h\",\"price\":5000,\"productType\":\"RECIPE\",\"productCode\":\"REC-AMER-ICE-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" ^
        > nul 2>&1
    set /a product_counter+=1

    REM Recipe 6: Cafe Latte Iced
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Iced Cafe Latte HQ%%h\",\"price\":5500,\"productType\":\"RECIPE\",\"productCode\":\"REC-LATTE-ICE-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" ^
        > nul 2>&1
    set /a product_counter+=1

    REM Recipe 7: Vanilla Latte Iced
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Iced Vanilla Latte HQ%%h\",\"price\":6000,\"productType\":\"RECIPE\",\"productCode\":\"REC-VAN-ICE-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" ^
        > nul 2>&1
    set /a product_counter+=1

    REM Recipe 8: Caramel Macchiato Iced
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Iced Caramel Macchiato HQ%%h\",\"price\":6300,\"productType\":\"RECIPE\",\"productCode\":\"REC-CAR-ICE-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" ^
        > nul 2>&1
    set /a product_counter+=1

    REM Recipe 9: Mocha Hot
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Mocha Hot HQ%%h\",\"price\":5800,\"productType\":\"RECIPE\",\"productCode\":\"REC-MOCHA-HOT-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" ^
        > nul 2>&1
    set /a product_counter+=1

    REM Recipe 10: Mocha Iced
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Iced Mocha HQ%%h\",\"price\":6300,\"productType\":\"RECIPE\",\"productCode\":\"REC-MOCHA-ICE-%%h\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" ^
        > nul 2>&1
    set /a product_counter+=1

    echo   [OK] HQ %%h: Created 10 recipe products
    echo [RECIPES] HQ %%h: 10 recipe products created >> %LOG_FILE%
)

echo.
echo [OK] Created 30 recipe products (10 per HQ)
echo.

REM ==================================================
REM Step 7: Create Finished Products for each HQ
REM ==================================================
echo.
echo [Step 7] Creating 10 Finished Products per HQ (30 total)...
echo.

for /L %%h in (1,1,3) do (
    set /a first_container=(%%h-1)*5+1

    echo Creating Finished Products for HQ %%h...

    REM Product 1: Croissant
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Croissant HQ%%h\",\"price\":3800,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-CROIS-%%h\",\"supplyAmt\":2500,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":100,\"usageQty\":100}}" ^
        > nul
    set /a product_counter+=1

    REM Product 2: Chocolate Croissant
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Chocolate Croissant HQ%%h\",\"price\":4200,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-CHOC-%%h\",\"supplyAmt\":2800,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":80,\"usageQty\":80}}" ^
        > nul
    set /a product_counter+=1

    REM Product 3: Blueberry Muffin
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Blueberry Muffin HQ%%h\",\"price\":4000,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-MUFFIN-%%h\",\"supplyAmt\":2600,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":90,\"usageQty\":90}}" ^
        > nul
    set /a product_counter+=1

    REM Product 4: Cheesecake
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Cheesecake Slice HQ%%h\",\"price\":6500,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-CHEESE-%%h\",\"supplyAmt\":4500,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":50,\"usageQty\":50}}" ^
        > nul
    set /a product_counter+=1

    REM Product 5: Brownie
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Brownie HQ%%h\",\"price\":4500,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-BROWN-%%h\",\"supplyAmt\":3000,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":70,\"usageQty\":70}}" ^
        > nul
    set /a product_counter+=1

    REM Product 6: Sandwich
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Club Sandwich HQ%%h\",\"price\":7500,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-SAND-%%h\",\"supplyAmt\":5000,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":60,\"usageQty\":60}}" ^
        > nul
    set /a product_counter+=1

    REM Product 7: Bagel
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Plain Bagel HQ%%h\",\"price\":3500,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-BAGEL-%%h\",\"supplyAmt\":2200,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":100,\"usageQty\":100}}" ^
        > nul
    set /a product_counter+=1

    REM Product 8: Cookie
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Chocolate Chip Cookie HQ%%h\",\"price\":2500,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-COOKIE-%%h\",\"supplyAmt\":1500,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":150,\"usageQty\":150}}" ^
        > nul
    set /a product_counter+=1

    REM Product 9: Scone
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Plain Scone HQ%%h\",\"price\":3800,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-SCONE-%%h\",\"supplyAmt\":2400,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":80,\"usageQty\":80}}" ^
        > nul
    set /a product_counter+=1

    REM Product 10: Donut
    curl -s -X POST "%BASE_URL%/product" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":%%h,\"name\":\"Glazed Donut HQ%%h\",\"price\":3200,\"productType\":\"PRODUCT\",\"productCode\":\"PRD-DONUT-%%h\",\"supplyAmt\":2000,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":!first_container!,\"unitQty\":120,\"usageQty\":120}}" ^
        > nul
    set /a product_counter+=1

    echo   [OK] HQ %%h: Created 10 finished products with initial stock
    echo [PRODUCTS] HQ %%h: 10 finished products created >> %LOG_FILE%
)

echo.
echo [OK] Created 30 finished products (10 per HQ)
echo.

REM ==================================================
REM Cleanup
REM ==================================================
echo.
echo Cleaning up temporary files...
del temp_*.json 2>nul

REM ==================================================
REM Summary
REM ==================================================
echo.
echo =========================================
echo Test Data Creation Complete!
echo =========================================
echo.
echo Summary:
echo   - HQs: 3
echo   - Stores: 15 (5 per HQ)
echo   - POS Terminals: 30 (2 per Store)
echo   - ProductContainers: 15 (1 per Store)
echo   - Ingredients: 8 (shared)
echo   - Recipe Products: 30 (10 per HQ)
echo   - Finished Products: 30 (10 per HQ)
echo   - Total Products: 68
echo.
echo Organization Structure:
echo   HQ 1 (Seoul): Stores 1-5, POS 1-10
echo   HQ 2 (Busan): Stores 6-10, POS 11-20
echo   HQ 3 (Jeju): Stores 11-15, POS 21-30
echo.
echo Next Steps:
echo   1. Add recipes to Recipe products (via API or database)
echo   2. Initialize stock for ingredients at each store
echo   3. Run k6 load test: k6 run k6/tests/multi-tier-sales-test.js
echo.
echo Log file: %LOG_FILE%
echo =========================================
echo.

echo Test data creation completed at: %DATE% %TIME% >> %LOG_FILE%

endlocal
