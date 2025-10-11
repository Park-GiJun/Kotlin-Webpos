@echo off
REM Run all k6 load tests for WebPOS

echo ======================================
echo WebPOS k6 Load Test Suite
echo ======================================
echo.

REM Check if k6 is installed
where k6 >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: k6 is not installed!
    echo Please install k6 from: https://k6.io/docs/get-started/installation/
    exit /b 1
)

REM Create reports directory if it doesn't exist
if not exist "reports" mkdir reports

echo [1/4] Running Main Server API Test...
k6 run tests/main-server-api.js
if %ERRORLEVEL% NEQ 0 (
    echo FAILED: Main Server API Test
    goto :error
)
echo.

echo [2/4] Running POS Server API Test...
k6 run tests/pos-server-api.js
if %ERRORLEVEL% NEQ 0 (
    echo FAILED: POS Server API Test
    goto :error
)
echo.

echo [3/4] Running Stock Adjustment Test...
k6 run tests/stock-adjustment.js
if %ERRORLEVEL% NEQ 0 (
    echo FAILED: Stock Adjustment Test
    goto :error
)
echo.

echo [4/4] Running Sales with Stock Integration Test...
k6 run tests/sales-with-stock.js
if %ERRORLEVEL% NEQ 0 (
    echo FAILED: Sales with Stock Integration Test
    goto :error
)
echo.

echo ======================================
echo All tests completed successfully!
echo ======================================
echo.
echo Check the reports/ directory for detailed results.
exit /b 0

:error
echo ======================================
echo Test suite failed!
echo ======================================
exit /b 1
