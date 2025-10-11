@echo off
REM Quick smoke test - Run this after restarting PowerShell

echo ======================================
echo WebPOS Smoke Test
echo ======================================
echo.

REM Try to find k6 in common installation paths
set K6_PATH=

REM Check if k6 is in PATH
where k6 >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    set K6_PATH=k6
    goto :run_test
)

REM Check Program Files
if exist "C:\Program Files\k6\k6.exe" (
    set K6_PATH="C:\Program Files\k6\k6.exe"
    goto :run_test
)

REM Check Local AppData
if exist "%LOCALAPPDATA%\Microsoft\WinGet\Packages\GrafanaLabs.k6_*\k6.exe" (
    for /d %%i in ("%LOCALAPPDATA%\Microsoft\WinGet\Packages\GrafanaLabs.k6_*") do (
        if exist "%%i\k6.exe" (
            set K6_PATH="%%i\k6.exe"
            goto :run_test
        )
    )
)

REM k6 not found
echo ERROR: k6 executable not found!
echo.
echo Please do one of the following:
echo 1. Close this PowerShell window completely and open a new one
echo 2. Or run this command in PowerShell:
echo    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
echo 3. Or manually locate k6.exe and run:
echo    "C:\path\to\k6.exe" run tests\smoke-test.js
echo.
pause
exit /b 1

:run_test
echo Found k6 at: %K6_PATH%
echo.
echo Running smoke test...
echo.

%K6_PATH% run tests\smoke-test.js

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ======================================
    echo Smoke test PASSED!
    echo ======================================
) else (
    echo.
    echo ======================================
    echo Smoke test FAILED!
    echo ======================================
)

pause
