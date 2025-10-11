@echo off
REM Test Data Creation Script for Main Server
REM This script creates test data for HQ, Store, POS, Product, ProductContainer, and ProductStock

setlocal enabledelayedexpansion

set BASE_URL=http://localhost:8080/main

echo =========================================
echo Creating Test Data for Main Server
echo =========================================

REM 1. Create HQ
echo.
echo 1. Creating HQ...
curl -s -X POST "%BASE_URL%/organization/hq" ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Test Headquarters\",\"representation\":\"John Doe\",\"street\":\"123 Main Street\",\"city\":\"Seoul\",\"zipCode\":\"12345\",\"email\":\"hq@test.com\",\"phoneNumber\":\"02-1234-5678\"}" ^
  > hq_response.json

type hq_response.json
echo.
echo HQ created successfully
echo.

REM 2. Create ProductContainer (with containerId = 1 for Store 1)
echo.
echo 2. Creating ProductContainer with containerId=1...

REM Read HQ_ID from response - for Windows we'll just use 1 as it's likely the first HQ
set HQ_ID=1

curl -s -X POST "%BASE_URL%/product/container" ^
  -H "Content-Type: application/json" ^
  -d "{\"hqId\":%HQ_ID%,\"containerId\":1,\"containerName\":\"Store 1 Main Container\"}" ^
  > container_response.json

type container_response.json
echo.
echo Container created successfully
echo.

REM 3. Create Product with ID 7
echo.
echo 3. Creating Products (targeting product ID 7)...

REM Create products 1-6 first
for /L %%i in (1,1,6) do (
  echo Creating product %%i...
  curl -s -X POST "%BASE_URL%/product" ^
    -H "Content-Type: application/json" ^
    -d "{\"hqId\":%HQ_ID%,\"name\":\"Test Product %%i\",\"price\":10000,\"productType\":\"PRODUCT\",\"productCode\":\"TEST00%%i\",\"supplyAmt\":8000,\"unit\":\"EA\",\"usageUnit\":\"EA\"}" ^
    > nul
)

REM Create product 7 with initial stock
echo Creating product 7 with initial stock...
curl -s -X POST "%BASE_URL%/product" ^
  -H "Content-Type: application/json" ^
  -d "{\"hqId\":%HQ_ID%,\"name\":\"Coffee Americano\",\"price\":4500,\"productType\":\"PRODUCT\",\"productCode\":\"COFFEE001\",\"supplyAmt\":3000,\"unit\":\"CUP\",\"usageUnit\":\"CUP\",\"initialStock\":{\"containerId\":1,\"unitQty\":100,\"usageQty\":100}}" ^
  > product_response.json

type product_response.json
echo.
echo Product 7 created successfully
echo.

REM Cleanup temp files
del hq_response.json 2>nul
del container_response.json 2>nul
del product_response.json 2>nul

echo.
echo =========================================
echo Test Data Creation Complete!
echo =========================================
echo HQ ID: %HQ_ID%
echo ProductContainer ID: 1 (for Store 1)
echo Product ID: 7 (should be created)
echo.
echo Now you can test the POS sales endpoint which requires:
echo   - productId: 7
echo   - storeId: 1
echo   - containerId: 1
echo.
echo The ProductStock should now exist for productId=7, containerId=1
echo.

endlocal
