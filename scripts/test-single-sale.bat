@echo off
REM Single Sales Transaction Test
REM Tests a single sales transaction with multiple products

setlocal enabledelayedexpansion

set POS_URL=http://localhost:8081/pos

if "%1"=="" (
    set HQ_ID=1
    set STORE_ID=1
    set POS_ID=1
) else (
    set HQ_ID=%1
    set STORE_ID=%2
    set POS_ID=%3
)

echo =========================================
echo Single Sales Transaction Test
echo =========================================
echo.
echo Testing POS: HQ %HQ_ID%, Store %STORE_ID%, POS %POS_ID%
echo.

REM Generate unique bill number
set TIMESTAMP=%date:~-4%%date:~4,2%%date:~7,2%_%time:~0,2%%time:~3,2%%time:~6,2%
set TIMESTAMP=%TIMESTAMP: =0%
set BILL_NO=TEST-%TIMESTAMP%

echo Bill Number: %BILL_NO%
echo.

REM Create a sales transaction with 3 products
REM Assuming: Product 19 (Croissant), 20 (Chocolate Croissant), 21 (Muffin)

echo Creating sales transaction...
echo.

curl -X POST "%POS_URL%/sales" ^
    -H "Content-Type: application/json" ^
    -d "{\"hqId\":%HQ_ID%,\"storeId\":%STORE_ID%,\"posId\":%POS_ID%,\"billNo\":\"%BILL_NO%\",\"saleType\":true,\"saleDate\":\"%date:~-4%-%date:~4,2%-%date:~7,2%T%time:~0,2%:%time:~3,2%:%time:~6,2%\",\"details\":[{\"hqId\":%HQ_ID%,\"storeId\":%STORE_ID%,\"posId\":%POS_ID%,\"lineNo\":1,\"productId\":19,\"productCode\":\"PRD-CROIS-1\",\"qty\":1,\"unitAmt\":3800,\"saleQty\":1,\"saleType\":true,\"saleDate\":\"%date:~-4%-%date:~4,2%-%date:~7,2%T%time:~0,2%:%time:~3,2%:%time:~6,2%\",\"saleAmt\":3800,\"payAmt\":3800,\"dcAmt\":0,\"couponAmt\":0,\"cardAmt\":3800,\"cashAmt\":0,\"voucherAmt\":0,\"promotionAmt\":0},{\"hqId\":%HQ_ID%,\"storeId\":%STORE_ID%,\"posId\":%POS_ID%,\"lineNo\":2,\"productId\":20,\"productCode\":\"PRD-CHOC-1\",\"qty\":1,\"unitAmt\":4200,\"saleQty\":1,\"saleType\":true,\"saleDate\":\"%date:~-4%-%date:~4,2%-%date:~7,2%T%time:~0,2%:%time:~3,2%:%time:~6,2%\",\"saleAmt\":4200,\"payAmt\":4200,\"dcAmt\":0,\"couponAmt\":0,\"cardAmt\":4200,\"cashAmt\":0,\"voucherAmt\":0,\"promotionAmt\":0},{\"hqId\":%HQ_ID%,\"storeId\":%STORE_ID%,\"posId\":%POS_ID%,\"lineNo\":3,\"productId\":21,\"productCode\":\"PRD-MUFFIN-1\",\"qty\":2,\"unitAmt\":4000,\"saleQty\":2,\"saleType\":true,\"saleDate\":\"%date:~-4%-%date:~4,2%-%date:~7,2%T%time:~0,2%:%time:~3,2%:%time:~6,2%\",\"saleAmt\":8000,\"payAmt\":8000,\"dcAmt\":0,\"couponAmt\":0,\"cardAmt\":8000,\"cashAmt\":0,\"voucherAmt\":0,\"promotionAmt\":0}],\"payments\":[{\"paymentMethodId\":1,\"payAmt\":16000,\"saleType\":true,\"paymentDate\":\"%date:~-4%-%date:~4,2%-%date:~7,2%T%time:~0,2%:%time:~3,2%:%time:~6,2%\",\"changeAmt\":0}]}" ^
    -o response.json

echo.
echo Response:
type response.json
echo.
echo.

echo =========================================
echo Verifying sale...
echo =========================================
echo.

curl -X GET "%POS_URL%/sales/bill/%BILL_NO%" -o verify.json
echo.
type verify.json
echo.
echo.

del response.json 2>nul
del verify.json 2>nul

echo =========================================
echo Test Complete!
echo =========================================
echo.
echo To test with different POS:
echo   test-single-sale.bat [HQ_ID] [STORE_ID] [POS_ID]
echo.
echo Example:
echo   test-single-sale.bat 2 6 11
echo.

endlocal
