@echo off
REM Initialize Ingredient Stock for All Stores
REM Adds initial stock of all ingredients to each of the 15 stores

setlocal enabledelayedexpansion

set BASE_URL=http://localhost:8080/main

echo =========================================
echo Initialize Ingredient Stock
echo =========================================
echo.
echo This script will add initial stock of ingredients to all 15 stores
echo.

REM Ingredient Product IDs (assuming sequential creation)
set BEAN_ID=1
set MILK_ID=2
set SUGAR_ID=3
set VANILLA_ID=4
set CARAMEL_ID=5
set ICE_ID=6
set CREAM_ID=7
set CHOCOLATE_ID=8

echo [Step 1] Adding ingredient stock to all 15 stores...
echo.

set /a total_adjustments=0

REM Loop through all 15 stores (containers 1-15)
for /L %%c in (1,1,15) do (
    set /a hq_num=(%%c-1)/5+1
    echo Adding ingredients to Store %%c (HQ !hq_num!)...

    REM Coffee Beans - 10kg = 10,000g
    curl -s -X POST "%BASE_URL%/product/stock-adjustment" ^
        -H "Content-Type: application/json" ^
        -d "{\"containerId\":%%c,\"productId\":%BEAN_ID%,\"adjustmentType\":\"IN\",\"unitQty\":10,\"usageQty\":10000,\"reason\":\"Initial stock for testing\"}" ^
        > nul 2>&1
    set /a total_adjustments+=1

    REM Milk - 20L = 20,000ml
    curl -s -X POST "%BASE_URL%/product/stock-adjustment" ^
        -H "Content-Type: application/json" ^
        -d "{\"containerId\":%%c,\"productId\":%MILK_ID%,\"adjustmentType\":\"IN\",\"unitQty\":20,\"usageQty\":20000,\"reason\":\"Initial stock for testing\"}" ^
        > nul 2>&1
    set /a total_adjustments+=1

    REM Sugar - 5kg = 5,000g
    curl -s -X POST "%BASE_URL%/product/stock-adjustment" ^
        -H "Content-Type: application/json" ^
        -d "{\"containerId\":%%c,\"productId\":%SUGAR_ID%,\"adjustmentType\":\"IN\",\"unitQty\":5,\"usageQty\":5000,\"reason\":\"Initial stock for testing\"}" ^
        > nul 2>&1
    set /a total_adjustments+=1

    REM Vanilla Syrup - 5L = 5,000ml
    curl -s -X POST "%BASE_URL%/product/stock-adjustment" ^
        -H "Content-Type: application/json" ^
        -d "{\"containerId\":%%c,\"productId\":%VANILLA_ID%,\"adjustmentType\":\"IN\",\"unitQty\":5,\"usageQty\":5000,\"reason\":\"Initial stock for testing\"}" ^
        > nul 2>&1
    set /a total_adjustments+=1

    REM Caramel Syrup - 5L = 5,000ml
    curl -s -X POST "%BASE_URL%/product/stock-adjustment" ^
        -H "Content-Type: application/json" ^
        -d "{\"containerId\":%%c,\"productId\":%CARAMEL_ID%,\"adjustmentType\":\"IN\",\"unitQty\":5,\"usageQty\":5000,\"reason\":\"Initial stock for testing\"}" ^
        > nul 2>&1
    set /a total_adjustments+=1

    REM Ice - 50kg = 50,000g
    curl -s -X POST "%BASE_URL%/product/stock-adjustment" ^
        -H "Content-Type: application/json" ^
        -d "{\"containerId\":%%c,\"productId\":%ICE_ID%,\"adjustmentType\":\"IN\",\"unitQty\":50,\"usageQty\":50000,\"reason\":\"Initial stock for testing\"}" ^
        > nul 2>&1
    set /a total_adjustments+=1

    REM Whipped Cream - 3L = 3,000ml
    curl -s -X POST "%BASE_URL%/product/stock-adjustment" ^
        -H "Content-Type: application/json" ^
        -d "{\"containerId\":%%c,\"productId\":%CREAM_ID%,\"adjustmentType\":\"IN\",\"unitQty\":3,\"usageQty\":3000,\"reason\":\"Initial stock for testing\"}" ^
        > nul 2>&1
    set /a total_adjustments+=1

    REM Chocolate Sauce - 3L = 3,000ml
    curl -s -X POST "%BASE_URL%/product/stock-adjustment" ^
        -H "Content-Type: application/json" ^
        -d "{\"containerId\":%%c,\"productId\":%CHOCOLATE_ID%,\"adjustmentType\":\"IN\",\"unitQty\":3,\"usageQty\":3000,\"reason\":\"Initial stock for testing\"}" ^
        > nul 2>&1
    set /a total_adjustments+=1

    echo   [OK] Store %%c: Added 8 ingredients
)

echo.
echo =========================================
echo Ingredient Stock Initialization Complete!
echo =========================================
echo.
echo Summary:
echo   - Stores: 15
echo   - Ingredients per store: 8
echo   - Total stock adjustments: %total_adjustments%
echo.
echo Initial Stock Quantities per Store:
echo   - Coffee Beans: 10 kg (10,000 g)
echo   - Milk: 20 L (20,000 ml)
echo   - Sugar: 5 kg (5,000 g)
echo   - Vanilla Syrup: 5 L (5,000 ml)
echo   - Caramel Syrup: 5 L (5,000 ml)
echo   - Ice: 50 kg (50,000 g)
echo   - Whipped Cream: 3 L (3,000 ml)
echo   - Chocolate Sauce: 3 L (3,000 ml)
echo.
echo Next Steps:
echo   1. Verify stock with: verify-stock.bat
echo   2. Run test sales: test-single-sale.bat
echo   3. Run k6 load test: k6 run k6/tests/multi-tier-sales-test.js
echo.
echo =========================================

endlocal
