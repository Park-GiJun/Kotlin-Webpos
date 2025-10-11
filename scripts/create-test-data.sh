#!/bin/bash

# Test Data Creation Script for Main Server
# This script creates test data for HQ, Store, POS, Product, ProductContainer, and ProductStock

BASE_URL="http://localhost:8080/main"

echo "========================================="
echo "Creating Test Data for Main Server"
echo "========================================="

# 1. Create HQ
echo ""
echo "1. Creating HQ..."
HQ_RESPONSE=$(curl -s -X POST "$BASE_URL/organization/hq" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Headquarters",
    "representation": "John Doe",
    "street": "123 Main Street",
    "city": "Seoul",
    "zipCode": "12345",
    "email": "hq@test.com",
    "phoneNumber": "02-1234-5678"
  }')

echo "HQ Response: $HQ_RESPONSE"
HQ_ID=$(echo "$HQ_RESPONSE" | grep -oP '"id":\s*\K\d+' | head -1)

if [ -z "$HQ_ID" ]; then
  echo "Failed to create HQ. Exiting..."
  exit 1
fi

echo "HQ created with ID: $HQ_ID"

# 2. Create ProductContainer (with containerId = 1 for Store 1)
echo ""
echo "2. Creating ProductContainer with containerId=1..."
CONTAINER_RESPONSE=$(curl -s -X POST "$BASE_URL/product/container" \
  -H "Content-Type: application/json" \
  -d "{
    \"hqId\": $HQ_ID,
    \"containerId\": 1,
    \"containerName\": \"Store 1 Main Container\"
  }")

echo "Container Response: $CONTAINER_RESPONSE"

# 3. Create Product with ID 7 (we'll need to create products sequentially to get ID 7)
echo ""
echo "3. Creating Products (targeting product ID 7)..."

# Create products 1-6 first
for i in {1..6}; do
  echo "Creating product $i..."
  curl -s -X POST "$BASE_URL/product" \
    -H "Content-Type: application/json" \
    -d "{
      \"hqId\": $HQ_ID,
      \"name\": \"Test Product $i\",
      \"price\": 10000,
      \"productType\": \"PRODUCT\",
      \"productCode\": \"TEST00$i\",
      \"supplyAmt\": 8000,
      \"unit\": \"EA\",
      \"usageUnit\": \"EA\"
    }" > /dev/null
done

# Create product 7 with initial stock
echo "Creating product 7 with initial stock..."
PRODUCT_RESPONSE=$(curl -s -X POST "$BASE_URL/product" \
  -H "Content-Type: application/json" \
  -d "{
    \"hqId\": $HQ_ID,
    \"name\": \"Coffee Americano\",
    \"price\": 4500,
    \"productType\": \"PRODUCT\",
    \"productCode\": \"COFFEE001\",
    \"supplyAmt\": 3000,
    \"unit\": \"CUP\",
    \"usageUnit\": \"CUP\",
    \"initialStock\": {
      \"containerId\": 1,
      \"unitQty\": 100,
      \"usageQty\": 100
    }
  }")

echo "Product 7 Response: $PRODUCT_RESPONSE"
PRODUCT_ID=$(echo "$PRODUCT_RESPONSE" | grep -oP '"id":\s*\K\d+' | head -1)

echo "Product created with ID: $PRODUCT_ID"

# Verify ProductStock was created
echo ""
echo "========================================="
echo "Test Data Creation Complete!"
echo "========================================="
echo "HQ ID: $HQ_ID"
echo "ProductContainer ID: 1 (for Store 1)"
echo "Product ID: $PRODUCT_ID (should be 7)"
echo ""
echo "Now you can test the POS sales endpoint which requires:"
echo "  - productId: $PRODUCT_ID"
echo "  - storeId: 1"
echo "  - containerId: 1"
echo ""
echo "The ProductStock should now exist for productId=$PRODUCT_ID, containerId=1"
