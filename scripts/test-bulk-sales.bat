@echo off
REM Bulk Sales Transaction Test
REM Creates multiple random sales transactions across different POS terminals

setlocal enabledelayedexpansion

set POS_URL=http://localhost:8081/pos

if "%1"=="" (
    set NUM_SALES=10
) else (
    set NUM_SALES=%1
)

echo =========================================
echo Bulk Sales Transaction Test
echo =========================================
echo.
echo Creating %NUM_SALES% random sales transactions...
echo.

set /a success_count=0
set /a error_count=0

for /L %%i in (1,1,%NUM_SALES%) do (
    REM Random HQ (1-3)
    set /a hq_id=%%i %% 3 + 1

    REM Random Store based on HQ
    set /a store_offset=(!hq_id!-1)*5
    set /a store_rand=%%i %% 5 + 1
    set /a store_id=!store_offset! + !store_rand!

    REM Random POS for that store (2 POS per store)
    set /a pos_offset=(!store_id!-1)*2
    set /a pos_rand=%%i %% 2 + 1
    set /a pos_id=!pos_offset! + !pos_rand!

    REM Generate bill number
    set BILL_NO=BULK-%%i-%RANDOM%

    REM Random product selection (assuming 19-28 are products for HQ 1)
    set /a base_product_id=8 + (!hq_id!-1)*20 + 11
    set /a product1=!base_product_id!
    set /a product2=!base_product_id! + 1
    set /a product3=!base_product_id! + 2

    REM Random quantities
    set /a qty1=%%i %% 3 + 1
    set /a qty2=%%i %% 2 + 1

    REM Calculate amounts (using fixed prices for simplicity)
    set /a amt1=!qty1! * 3800
    set /a amt2=!qty2! * 4200
    set /a total=!amt1! + !amt2!

    echo [%%i/%NUM_SALES%] Creating sale for HQ !hq_id!, Store !store_id!, POS !pos_id!...
    echo   Products: !product1! (x!qty1!), !product2! (x!qty2!)
    echo   Total: !total! KRW

    REM Get current timestamp
    for /f "tokens=1-3 delims=/- " %%a in ('date /t') do (
        set mm=%%a
        set dd=%%b
        set yyyy=%%c
    )
    for /f "tokens=1-3 delims=:. " %%a in ('time /t') do (
        set hh=%%a
        set min=%%b
        set ss=00
    )

    curl -s -X POST "%POS_URL%/sales" ^
        -H "Content-Type: application/json" ^
        -d "{\"hqId\":!hq_id!,\"storeId\":!store_id!,\"posId\":!pos_id!,\"billNo\":\"!BILL_NO!\",\"saleType\":true,\"saleDate\":\"!yyyy!-!mm!-!dd!T!hh!:!min!:!ss!\",\"details\":[{\"hqId\":!hq_id!,\"storeId\":!store_id!,\"posId\":!pos_id!,\"lineNo\":1,\"productId\":!product1!,\"productCode\":\"PRD-!product1!\",\"qty\":!qty1!,\"unitAmt\":3800,\"saleQty\":!qty1!,\"saleType\":true,\"saleDate\":\"!yyyy!-!mm!-!dd!T!hh!:!min!:!ss!\",\"saleAmt\":!amt1!,\"payAmt\":!amt1!,\"dcAmt\":0,\"couponAmt\":0,\"cardAmt\":!amt1!,\"cashAmt\":0,\"voucherAmt\":0,\"promotionAmt\":0},{\"hqId\":!hq_id!,\"storeId\":!store_id!,\"posId\":!pos_id!,\"lineNo\":2,\"productId\":!product2!,\"productCode\":\"PRD-!product2!\",\"qty\":!qty2!,\"unitAmt\":4200,\"saleQty\":!qty2!,\"saleType\":true,\"saleDate\":\"!yyyy!-!mm!-!dd!T!hh!:!min!:!ss!\",\"saleAmt\":!amt2!,\"payAmt\":!amt2!,\"dcAmt\":0,\"couponAmt\":0,\"cardAmt\":!amt2!,\"cashAmt\":0,\"voucherAmt\":0,\"promotionAmt\":0}],\"payments\":[{\"paymentMethodId\":1,\"payAmt\":!total!,\"saleType\":true,\"paymentDate\":\"!yyyy!-!mm!-!dd!T!hh!:!min!:!ss!\",\"changeAmt\":0}]}" ^
        > temp_result_%%i.json 2>&1

    findstr /C:"\"id\"" temp_result_%%i.json >nul
    if !errorlevel! equ 0 (
        set /a success_count+=1
        echo   [OK] Sale created successfully
    ) else (
        set /a error_count+=1
        echo   [ERROR] Sale failed
        type temp_result_%%i.json
    )

    del temp_result_%%i.json 2>nul
    echo.

    REM Small delay between requests
    timeout /t 1 /nobreak > nul
)

echo.
echo =========================================
echo Bulk Sales Test Complete!
echo =========================================
echo.
echo Summary:
echo   Total Sales: %NUM_SALES%
echo   Successful: %success_count%
echo   Failed: %error_count%
echo   Success Rate:
set /a success_rate=%success_count%*100/%NUM_SALES%
echo   %success_rate%%%
echo.
echo To test with different quantity:
echo   test-bulk-sales.bat [NUM_SALES]
echo.
echo Example:
echo   test-bulk-sales.bat 50
echo.

endlocal
