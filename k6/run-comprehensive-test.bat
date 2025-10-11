@echo off
REM Comprehensive Sales Test Runner for K6
REM Tests all 3 HQs, 15 Stores, 30 POS with complete product catalog

setlocal

set POS_URL=http://localhost:8081
set TEST_FILE=tests/comprehensive-sales-test.js

echo =========================================
echo Comprehensive Sales Load Test
echo =========================================
echo.
echo Configuration:
echo   - POS Server: %POS_URL%
echo   - Test File: %TEST_FILE%
echo   - Coverage: 3 HQs, 15 Stores, 30 POS
echo   - Products: 68 (8 ingredients + 30 recipes + 30 finished)
echo.
echo Test Stages:
echo   1. Ramp-up:   30s to 10 VUs
echo   2. Sustain:   1m at 10 VUs
echo   3. Ramp-up:   30s to 20 VUs
echo   4. Sustain:   1m at 20 VUs
echo   5. Ramp-down: 30s to 0 VUs
echo.
echo Total Duration: ~4 minutes
echo =========================================
echo.

REM Check if k6 is installed
where k6 >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Error: k6 is not installed or not in PATH
    echo.
    echo Please install k6 from: https://k6.io/docs/get-started/installation/
    echo.
    pause
    exit /b 1
)

REM Check if POS server is running
echo Checking POS server availability...
curl -s -o nul -w "%%{http_code}" %POS_URL%/actuator/health >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Warning: POS server may not be running at %POS_URL%
    echo Please ensure the POS server is started before running the test.
    echo.
    set /p CONTINUE="Continue anyway? (y/N): "
    if /i not "!CONTINUE!"=="y" (
        echo Test cancelled.
        pause
        exit /b 1
    )
) else (
    echo POS server is available!
    echo.
)

REM Run the test
echo Starting comprehensive sales test...
echo.

k6 run --env POS_URL=%POS_URL% %TEST_FILE%

set EXIT_CODE=%ERRORLEVEL%

echo.
echo =========================================
echo Test completed with exit code: %EXIT_CODE%
echo =========================================
echo.

if %EXIT_CODE% EQU 0 (
    echo SUCCESS: All thresholds passed!
) else (
    echo FAILED: Some thresholds were not met.
    echo Please review the test results above.
)

echo.
pause

endlocal
exit /b %EXIT_CODE%
