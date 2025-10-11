@echo off
REM Check all created products and their IDs

setlocal enabledelayedexpansion

set BASE_URL=http://localhost:8080/main

echo =========================================
echo Checking All Products
echo =========================================
echo.

echo Fetching all products...
echo.

curl -s -X GET "%BASE_URL%/product" -o products.json

echo Products saved to products.json
echo.

echo Parsing product IDs...
echo.

REM Parse and display products (using findstr for simple parsing)
type products.json

echo.
echo.
echo =========================================
echo Check products.json for actual Product IDs
echo =========================================
echo.

endlocal
