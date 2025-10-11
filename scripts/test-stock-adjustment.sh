#!/bin/bash

echo "Testing Stock Adjustment Endpoint"
echo "=================================="

echo ""
echo "1. Current stock for product 7, container 1:"
docker exec mysql-main mysql -umainuser -pmainpassword main_server -e "SELECT product_id, container_id, unit_qty, usage_qty FROM product_stock WHERE product_id = 7 AND container_id = 1;" 2>&1 | grep -v "Warning"

echo ""
echo "2. Testing stock adjustment API:"
curl -X PUT "http://localhost:8080/main/product-stock/product/7/store/1/adjust" \
  -H "Content-Type: application/json" \
  -d '{"adjustmentType":"DECREASE","unitQty":0,"usageQty":1,"reason":"Test"}' \
  2>&1 | grep -v "^%" | grep -v "^  "

echo ""
echo ""
echo "3. Stock after adjustment:"
docker exec mysql-main mysql -umainuser -pmainpassword main_server -e "SELECT product_id, container_id, unit_qty, usage_qty FROM product_stock WHERE product_id = 7 AND container_id = 1;" 2>&1 | grep -v "Warning"
