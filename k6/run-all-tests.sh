#!/bin/bash

# Run all k6 load tests for WebPOS

echo "======================================"
echo "WebPOS k6 Load Test Suite"
echo "======================================"
echo

# Check if k6 is installed
if ! command -v k6 &> /dev/null; then
    echo "ERROR: k6 is not installed!"
    echo "Please install k6 from: https://k6.io/docs/get-started/installation/"
    exit 1
fi

# Create reports directory if it doesn't exist
mkdir -p reports

# Test counter
FAILED=0

echo "[1/4] Running Main Server API Test..."
k6 run tests/main-server-api.js
if [ $? -ne 0 ]; then
    echo "FAILED: Main Server API Test"
    FAILED=$((FAILED + 1))
fi
echo

echo "[2/4] Running POS Server API Test..."
k6 run tests/pos-server-api.js
if [ $? -ne 0 ]; then
    echo "FAILED: POS Server API Test"
    FAILED=$((FAILED + 1))
fi
echo

echo "[3/4] Running Stock Adjustment Test..."
k6 run tests/stock-adjustment.js
if [ $? -ne 0 ]; then
    echo "FAILED: Stock Adjustment Test"
    FAILED=$((FAILED + 1))
fi
echo

echo "[4/4] Running Sales with Stock Integration Test..."
k6 run tests/sales-with-stock.js
if [ $? -ne 0 ]; then
    echo "FAILED: Sales with Stock Integration Test"
    FAILED=$((FAILED + 1))
fi
echo

echo "======================================"
if [ $FAILED -eq 0 ]; then
    echo "All tests completed successfully!"
    echo "======================================"
    echo
    echo "Check the reports/ directory for detailed results."
    exit 0
else
    echo "$FAILED test(s) failed!"
    echo "======================================"
    exit 1
fi
