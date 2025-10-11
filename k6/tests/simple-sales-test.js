import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Counter } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const salesCount = new Counter('sales_count');

// Simple test configuration
export const options = {
  vus: 5,           // 5 virtual users (POS terminals)
  duration: '30s',  // Run for 30 seconds
  thresholds: {
    http_req_duration: ['p(95)<2000'],  // 95% requests under 2s
    errors: ['rate<0.1'],                // Error rate under 10%
  },
};

const POS_URL = __ENV.POS_URL || 'http://localhost:8081';

// Use the same product IDs from sales-with-stock.js (known to exist)
const KNOWN_PRODUCTS = [
  // Recipe Products (Hot Beverages)
  { id: 201, code: 'REC-AMER-HOT', name: 'Americano (Hot)', price: 4500, type: 'RECIPE' },
  { id: 202, code: 'REC-LATTE-HOT', name: 'Cafe Latte (Hot)', price: 5000, type: 'RECIPE' },
  { id: 204, code: 'REC-VANLAT-HOT', name: 'Vanilla Latte (Hot)', price: 5500, type: 'RECIPE' },
  { id: 205, code: 'REC-CARMAC-HOT', name: 'Caramel Macchiato (Hot)', price: 5800, type: 'RECIPE' },
  // Recipe Products (Cold Beverages)
  { id: 206, code: 'REC-AMER-ICE', name: 'Iced Americano', price: 5000, type: 'RECIPE' },
  { id: 207, code: 'REC-LATTE-ICE', name: 'Iced Cafe Latte', price: 5500, type: 'RECIPE' },
  { id: 209, code: 'REC-CARMAC-ICE', name: 'Iced Caramel Macchiato', price: 6300, type: 'RECIPE' },
  // Ready-to-Sell Products (Bakery)
  { id: 301, code: 'PRD-CROIS-001', name: 'Croissant', price: 3800, type: 'PRODUCT' },
  { id: 302, code: 'PRD-CHOCRO-001', name: 'Chocolate Croissant', price: 4200, type: 'PRODUCT' },
  { id: 303, code: 'PRD-MUFFIN-BLU', name: 'Blueberry Muffin', price: 4000, type: 'PRODUCT' },
  // Desserts
  { id: 306, code: 'PRD-CHEESE-001', name: 'Cheesecake Slice', price: 6500, type: 'PRODUCT' },
  { id: 308, code: 'PRD-BROWNIE-001', name: 'Brownie', price: 4500, type: 'PRODUCT' },
];

// Generate POS configurations
const POS_CONFIGS = [];
const STORE_NAMES = ['Gangnam Store', 'Hongdae Store', 'Itaewon Store', 'Myeongdong Store', 'Jamsil Store'];

// Use HQ 1 with known products for all POS terminals
for (let storeIdx = 0; storeIdx < 5; storeIdx++) {
  const storeId = storeIdx + 1;

  for (let posIdx = 0; posIdx < 2; posIdx++) {
    const posId = (storeId - 1) * 2 + posIdx + 1;

    POS_CONFIGS.push({
      hqId: 1,
      hqName: 'Seoul HQ',
      storeId,
      storeName: STORE_NAMES[storeIdx],
      posId,
      products: KNOWN_PRODUCTS
    });
  }
}

export default function() {
  // Select random POS configuration
  const config = POS_CONFIGS[Math.floor(Math.random() * POS_CONFIGS.length)];
  const { hqId, hqName, storeId, storeName, posId, products } = config;

  // Generate unique bill number
  const billNo = `TEST-${Date.now()}-${__VU}-${__ITER}`;

  // Select 1-3 random products
  const numProducts = Math.floor(Math.random() * 3) + 1;
  const selectedProducts = [];
  const usedIndices = new Set();

  for (let i = 0; i < numProducts; i++) {
    let idx;
    do {
      idx = Math.floor(Math.random() * products.length);
    } while (usedIndices.has(idx));
    usedIndices.add(idx);
    selectedProducts.push(products[idx]);
  }

  // Build sales details
  const details = [];
  let totalAmount = 0;
  const saleDate = new Date().toISOString().slice(0, 19);

  selectedProducts.forEach((product, index) => {
    const qty = Math.floor(Math.random() * 2) + 1; // 1-2 quantity
    const lineAmount = qty * product.price;
    totalAmount += lineAmount;

    details.push({
      hqId,
      storeId,
      posId,
      lineNo: index + 1,
      productId: product.id,
      productCode: product.code,
      qty,
      unitAmt: product.price,
      saleQty: qty,
      saleType: true,
      saleDate,
      saleAmt: lineAmount,
      payAmt: lineAmount,
      dcAmt: 0,
      couponAmt: 0,
      cardAmt: lineAmount,
      cashAmt: 0,
      voucherAmt: 0,
      promotionAmt: 0
    });
  });

  // Build sales payload
  const salesPayload = JSON.stringify({
    hqId,
    storeId,
    posId,
    billNo,
    saleType: true,
    saleDate,
    details,
    payments: [{
      paymentMethodId: 1,
      payAmt: totalAmount,
      saleType: true,
      paymentDate: saleDate,
      changeAmt: 0
    }]
  });

  // Create sales transaction
  const res = http.post(
    `${POS_URL}/pos/sales`,
    salesPayload,
    {
      headers: { 'Content-Type': 'application/json' },
      timeout: '10s',
    }
  );

  const success = check(res, {
    'status is 200/201': (r) => r.status === 200 || r.status === 201,
    'response has data': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data || body.id;
      } catch (e) {
        return false;
      }
    }
  });

  if (success) {
    salesCount.add(1);
    if (__ITER % 5 === 0) {
      console.log(`✓ [${hqName}/${storeName}/POS${posId}] Sale ${billNo}: ₩${totalAmount.toLocaleString()}`);
    }
  } else {
    errorRate.add(1);
    console.error(`✗ [${hqName}/${storeName}/POS${posId}] Failed: ${billNo} - Status: ${res.status}`);
    console.error(`Response: ${res.body.substring(0, 200)}`);
  }

  sleep(1); // 1 second between transactions
}

export function handleSummary(data) {
  const sales = data.metrics.sales_count?.values.count || 0;
  const errors = (data.metrics.errors?.values.rate || 0) * 100;

  console.log('\n' + '='.repeat(60));
  console.log('Simple Sales Test - Results');
  console.log('='.repeat(60));
  console.log(`Total Sales: ${sales}`);
  console.log(`Error Rate: ${errors.toFixed(2)}%`);
  console.log(`Avg Response Time: ${data.metrics.http_req_duration?.values.avg.toFixed(2)}ms`);
  console.log('='.repeat(60) + '\n');

  return {
    'stdout': '',
  };
}
