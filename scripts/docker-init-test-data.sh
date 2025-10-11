#!/bin/bash
# Docker MySQL Test Data Initialization Script
# Run this inside Docker container or from host with docker exec

set -e

MAIN_URL="http://host.docker.internal:8080/main"

echo "========================================="
echo "Docker Test Data Initialization"
echo "========================================="
echo ""
echo "This script will create:"
echo "  - 3 HQs"
echo "  - 15 Stores (5 per HQ)"
echo "  - 30 POS (2 per Store)"
echo "  - 15 Containers (1 per Store)"
echo "  - 8 Ingredients"
echo "  - 30 Recipe Products (10 per HQ)"
echo "  - 30 Finished Products (10 per HQ)"
echo ""
echo "========================================="
echo ""

# ==================================================
# Step 1: Create 3 HQs
# ==================================================
echo "[Step 1/7] Creating 3 HQs..."

curl -s -X POST "$MAIN_URL/organization/hq" \
  -H "Content-Type: application/json" \
  -d '{"name":"Seoul Headquarters","representation":"CEO 1","street":"Gangnam-daero 123","city":"Seoul","zipCode":"12341","email":"hq1@test.com","phoneNumber":"02-123-4561"}' \
  > /dev/null

curl -s -X POST "$MAIN_URL/organization/hq" \
  -H "Content-Type: application/json" \
  -d '{"name":"Busan Headquarters","representation":"CEO 2","street":"Haeundae-ro 456","city":"Busan","zipCode":"12342","email":"hq2@test.com","phoneNumber":"02-123-4562"}' \
  > /dev/null

curl -s -X POST "$MAIN_URL/organization/hq" \
  -H "Content-Type: application/json" \
  -d '{"name":"Jeju Headquarters","representation":"CEO 3","street":"Nohyeong-ro 789","city":"Jeju","zipCode":"12343","email":"hq3@test.com","phoneNumber":"02-123-4563"}' \
  > /dev/null

echo "  ✓ Created 3 HQs"
echo ""

# ==================================================
# Step 2: Create 15 Stores
# ==================================================
echo "[Step 2/7] Creating 15 Stores..."

STORE_NAMES=("Gangnam Store" "Hongdae Store" "Itaewon Store" "Myeongdong Store" "Jamsil Store")
store_id=1

for hq in 1 2 3; do
  for store_idx in 0 1 2 3 4; do
    store_name="${STORE_NAMES[$store_idx]}"

    curl -s -X POST "$MAIN_URL/organization/store" \
      -H "Content-Type: application/json" \
      -d "{\"hqId\":$hq,\"name\":\"$store_name\",\"street\":\"Street $store_id\",\"city\":\"City $hq\",\"zipCode\":\"5000$store_id\",\"email\":\"store$store_id@test.com\",\"phoneNumber\":\"02-200-${store_id}000\"}" \
      > /dev/null

    ((store_id++))
  done
done

echo "  ✓ Created 15 Stores"
echo ""

# ==================================================
# Step 3: Create 15 ProductContainers
# ==================================================
echo "[Step 3/7] Creating 15 ProductContainers..."

for container_id in {1..15}; do
  hq_id=$(( (container_id - 1) / 5 + 1 ))

  curl -s -X POST "$MAIN_URL/product/container" \
    -H "Content-Type: application/json" \
    -d "{\"hqId\":$hq_id,\"containerId\":$container_id,\"containerName\":\"Store $container_id Container\"}" \
    > /dev/null
done

echo "  ✓ Created 15 ProductContainers"
echo ""

# ==================================================
# Step 4: Create 30 POS
# ==================================================
echo "[Step 4/7] Creating 30 POS..."

pos_id=1
for store_id in {1..15}; do
  hq_id=$(( (store_id - 1) / 5 + 1 ))

  for pos_num in 1 2; do
    curl -s -X POST "$MAIN_URL/organization/pos" \
      -H "Content-Type: application/json" \
      -d "{\"hqId\":$hq_id,\"storeId\":$store_id,\"name\":\"POS-$pos_id\",\"macAddress\":\"AA:BB:CC:DD:EE:$(printf '%02d' $pos_id)\"}" \
      > /dev/null

    ((pos_id++))
  done
done

echo "  ✓ Created 30 POS"
echo ""

# ==================================================
# Step 5: Create 8 Ingredient Products
# ==================================================
echo "[Step 5/7] Creating 8 Ingredient Products..."

curl -s -X POST "$MAIN_URL/product" \
  -H "Content-Type: application/json" \
  -d '{"hqId":1,"name":"Coffee Beans","price":15000,"productType":"INGREDIENT","productCode":"ING-BEAN-001","supplyAmt":12000,"unit":"KG","usageUnit":"G"}' \
  > /dev/null

curl -s -X POST "$MAIN_URL/product" \
  -H "Content-Type: application/json" \
  -d '{"hqId":1,"name":"Milk","price":5000,"productType":"INGREDIENT","productCode":"ING-MILK-001","supplyAmt":4000,"unit":"L","usageUnit":"ML"}' \
  > /dev/null

curl -s -X POST "$MAIN_URL/product" \
  -H "Content-Type: application/json" \
  -d '{"hqId":1,"name":"Sugar","price":3000,"productType":"INGREDIENT","productCode":"ING-SUGAR-001","supplyAmt":2500,"unit":"KG","usageUnit":"G"}' \
  > /dev/null

curl -s -X POST "$MAIN_URL/product" \
  -H "Content-Type: application/json" \
  -d '{"hqId":1,"name":"Vanilla Syrup","price":8000,"productType":"INGREDIENT","productCode":"ING-VAN-001","supplyAmt":6500,"unit":"L","usageUnit":"ML"}' \
  > /dev/null

curl -s -X POST "$MAIN_URL/product" \
  -H "Content-Type: application/json" \
  -d '{"hqId":1,"name":"Caramel Syrup","price":8500,"productType":"INGREDIENT","productCode":"ING-CAR-001","supplyAmt":7000,"unit":"L","usageUnit":"ML"}' \
  > /dev/null

curl -s -X POST "$MAIN_URL/product" \
  -H "Content-Type: application/json" \
  -d '{"hqId":1,"name":"Ice","price":1000,"productType":"INGREDIENT","productCode":"ING-ICE-001","supplyAmt":500,"unit":"KG","usageUnit":"G"}' \
  > /dev/null

curl -s -X POST "$MAIN_URL/product" \
  -H "Content-Type: application/json" \
  -d '{"hqId":1,"name":"Whipped Cream","price":6000,"productType":"INGREDIENT","productCode":"ING-CREAM-001","supplyAmt":5000,"unit":"L","usageUnit":"ML"}' \
  > /dev/null

curl -s -X POST "$MAIN_URL/product" \
  -H "Content-Type: application/json" \
  -d '{"hqId":1,"name":"Chocolate Sauce","price":7000,"productType":"INGREDIENT","productCode":"ING-CHOC-001","supplyAmt":5500,"unit":"L","usageUnit":"ML"}' \
  > /dev/null

echo "  ✓ Created 8 Ingredient Products"
echo ""

# ==================================================
# Step 6: Create 30 Recipe Products (10 per HQ)
# ==================================================
echo "[Step 6/7] Creating 30 Recipe Products..."

RECIPE_NAMES=(
  "Americano Hot"
  "Cafe Latte Hot"
  "Vanilla Latte Hot"
  "Caramel Macchiato Hot"
  "Iced Americano"
  "Iced Cafe Latte"
  "Iced Vanilla Latte"
  "Iced Caramel Macchiato"
  "Mocha Hot"
  "Iced Mocha"
)

RECIPE_PRICES=(4500 5000 5500 5800 5000 5500 6000 6300 5800 6300)
RECIPE_CODES=("AMER-HOT" "LATTE-HOT" "VAN-HOT" "CAR-HOT" "AMER-ICE" "LATTE-ICE" "VAN-ICE" "CAR-ICE" "MOCHA-HOT" "MOCHA-ICE")

for hq in 1 2 3; do
  for idx in {0..9}; do
    name="${RECIPE_NAMES[$idx]} HQ$hq"
    price="${RECIPE_PRICES[$idx]}"
    code="REC-${RECIPE_CODES[$idx]}-$hq"

    curl -s -X POST "$MAIN_URL/product" \
      -H "Content-Type: application/json" \
      -d "{\"hqId\":$hq,\"name\":\"$name\",\"price\":$price,\"productType\":\"RECIPE\",\"productCode\":\"$code\",\"supplyAmt\":0,\"unit\":\"CUP\",\"usageUnit\":\"CUP\"}" \
      > /dev/null
  done
done

echo "  ✓ Created 30 Recipe Products"
echo ""

# ==================================================
# Step 7: Create 30 Finished Products (10 per HQ) with initial stock
# ==================================================
echo "[Step 7/7] Creating 30 Finished Products with stock..."

PRODUCT_NAMES=(
  "Croissant"
  "Chocolate Croissant"
  "Blueberry Muffin"
  "Cheesecake Slice"
  "Brownie"
  "Club Sandwich"
  "Plain Bagel"
  "Chocolate Chip Cookie"
  "Plain Scone"
  "Glazed Donut"
)

PRODUCT_PRICES=(3800 4200 4000 6500 4500 7500 3500 2500 3800 3200)
PRODUCT_SUPPLY=(2500 2800 2600 4500 3000 5000 2200 1500 2400 2000)
PRODUCT_CODES=("CROIS" "CHOC" "MUFFIN" "CHEESE" "BROWN" "SAND" "BAGEL" "COOKIE" "SCONE" "DONUT")
PRODUCT_STOCK=(100 80 90 50 70 60 100 150 80 120)

for hq in 1 2 3; do
  first_container=$(( (hq - 1) * 5 + 1 ))

  for idx in {0..9}; do
    name="${PRODUCT_NAMES[$idx]} HQ$hq"
    price="${PRODUCT_PRICES[$idx]}"
    supply="${PRODUCT_SUPPLY[$idx]}"
    code="PRD-${PRODUCT_CODES[$idx]}-$hq"
    stock="${PRODUCT_STOCK[$idx]}"

    curl -s -X POST "$MAIN_URL/product" \
      -H "Content-Type: application/json" \
      -d "{\"hqId\":$hq,\"name\":\"$name\",\"price\":$price,\"productType\":\"PRODUCT\",\"productCode\":\"$code\",\"supplyAmt\":$supply,\"unit\":\"EA\",\"usageUnit\":\"EA\",\"initialStock\":{\"containerId\":$first_container,\"unitQty\":$stock,\"usageQty\":$stock}}" \
      > /dev/null
  done
done

echo "  ✓ Created 30 Finished Products with initial stock"
echo ""

# ==================================================
# Summary
# ==================================================
echo "========================================="
echo "Test Data Initialization Complete!"
echo "========================================="
echo ""
echo "Summary:"
echo "  - HQs: 3"
echo "  - Stores: 15 (5 per HQ)"
echo "  - POS Terminals: 30 (2 per Store)"
echo "  - ProductContainers: 15 (1 per Store)"
echo "  - Ingredients: 8"
echo "  - Recipe Products: 30 (10 per HQ)"
echo "  - Finished Products: 30 (10 per HQ)"
echo "  - Total Products: 68"
echo ""
echo "Product ID Ranges:"
echo "  Ingredients: 1-8"
echo "  HQ 1 Recipes: 9-18"
echo "  HQ 1 Products: 19-28"
echo "  HQ 2 Recipes: 29-38"
echo "  HQ 2 Products: 39-48"
echo "  HQ 3 Recipes: 49-58"
echo "  HQ 3 Products: 59-68"
echo ""
echo "Next Steps:"
echo "  1. Run k6 tests: k6 run k6/tests/simple-sales-test.js"
echo "  2. Check results in database"
echo ""
echo "========================================="
