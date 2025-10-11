import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Counter, Trend } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const salesCount = new Counter('sales_count');
const salesAmount = new Trend('sales_amount');
const stockErrors = new Counter('stock_errors');

// Test configuration
export const options = {
  stages: [
    { duration: '30s', target: 10 },   // Ramp up to 10 VUs
    { duration: '1m', target: 10 },    // Stay at 10 VUs
    { duration: '30s', target: 20 },   // Ramp up to 20 VUs
    { duration: '1m', target: 20 },    // Stay at 20 VUs
    { duration: '30s', target: 0 },    // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<3000'],  // 95% requests under 3s
    http_req_failed: ['rate<0.1'],      // Less than 10% failed requests
    errors: ['rate<0.15'],               // Error rate under 15%
  },
};

const POS_URL = __ENV.POS_URL || 'http://localhost:8081';

// =========================================
// Test Data based on 06-complete-test-data.sql
// =========================================

// HQ 1: Ingredients (1-8)
const INGREDIENTS_HQ1 = [
  { id: 1, code: 'ING-BEAN-001', name: 'Coffee Beans', price: 15000 },
  { id: 2, code: 'ING-MILK-001', name: 'Milk', price: 5000 },
  { id: 3, code: 'ING-SUGAR-001', name: 'Sugar', price: 3000 },
  { id: 4, code: 'ING-VAN-001', name: 'Vanilla Syrup', price: 8000 },
  { id: 5, code: 'ING-CAR-001', name: 'Caramel Syrup', price: 8500 },
  { id: 6, code: 'ING-ICE-001', name: 'Ice', price: 1000 },
  { id: 7, code: 'ING-CREAM-001', name: 'Whipped Cream', price: 6000 },
  { id: 8, code: 'ING-CHOC-001', name: 'Chocolate Sauce', price: 7000 },
];

// HQ 1: Recipe Products (9-18)
const RECIPES_HQ1 = [
  { id: 9, code: 'REC-AMER-HOT-1', name: 'Americano Hot HQ1', price: 4500, type: 'RECIPE' },
  { id: 10, code: 'REC-LATTE-HOT-1', name: 'Cafe Latte Hot HQ1', price: 5000, type: 'RECIPE' },
  { id: 11, code: 'REC-VAN-HOT-1', name: 'Vanilla Latte Hot HQ1', price: 5500, type: 'RECIPE' },
  { id: 12, code: 'REC-CAR-HOT-1', name: 'Caramel Macchiato Hot HQ1', price: 5800, type: 'RECIPE' },
  { id: 13, code: 'REC-AMER-ICE-1', name: 'Iced Americano HQ1', price: 5000, type: 'RECIPE' },
  { id: 14, code: 'REC-LATTE-ICE-1', name: 'Iced Cafe Latte HQ1', price: 5500, type: 'RECIPE' },
  { id: 15, code: 'REC-VAN-ICE-1', name: 'Iced Vanilla Latte HQ1', price: 6000, type: 'RECIPE' },
  { id: 16, code: 'REC-CAR-ICE-1', name: 'Iced Caramel Macchiato HQ1', price: 6300, type: 'RECIPE' },
  { id: 17, code: 'REC-MOCHA-HOT-1', name: 'Mocha Hot HQ1', price: 5800, type: 'RECIPE' },
  { id: 18, code: 'REC-MOCHA-ICE-1', name: 'Iced Mocha HQ1', price: 6300, type: 'RECIPE' },
];

// HQ 2: Recipe Products (19-28)
const RECIPES_HQ2 = [
  { id: 19, code: 'REC-AMER-HOT-2', name: 'Americano Hot HQ2', price: 4500, type: 'RECIPE' },
  { id: 20, code: 'REC-LATTE-HOT-2', name: 'Cafe Latte Hot HQ2', price: 5000, type: 'RECIPE' },
  { id: 21, code: 'REC-VAN-HOT-2', name: 'Vanilla Latte Hot HQ2', price: 5500, type: 'RECIPE' },
  { id: 22, code: 'REC-CAR-HOT-2', name: 'Caramel Macchiato Hot HQ2', price: 5800, type: 'RECIPE' },
  { id: 23, code: 'REC-AMER-ICE-2', name: 'Iced Americano HQ2', price: 5000, type: 'RECIPE' },
  { id: 24, code: 'REC-LATTE-ICE-2', name: 'Iced Cafe Latte HQ2', price: 5500, type: 'RECIPE' },
  { id: 25, code: 'REC-VAN-ICE-2', name: 'Iced Vanilla Latte HQ2', price: 6000, type: 'RECIPE' },
  { id: 26, code: 'REC-CAR-ICE-2', name: 'Iced Caramel Macchiato HQ2', price: 6300, type: 'RECIPE' },
  { id: 27, code: 'REC-MOCHA-HOT-2', name: 'Mocha Hot HQ2', price: 5800, type: 'RECIPE' },
  { id: 28, code: 'REC-MOCHA-ICE-2', name: 'Iced Mocha HQ2', price: 6300, type: 'RECIPE' },
];

// HQ 3: Recipe Products (29-38)
const RECIPES_HQ3 = [
  { id: 29, code: 'REC-AMER-HOT-3', name: 'Americano Hot HQ3', price: 4500, type: 'RECIPE' },
  { id: 30, code: 'REC-LATTE-HOT-3', name: 'Cafe Latte Hot HQ3', price: 5000, type: 'RECIPE' },
  { id: 31, code: 'REC-VAN-HOT-3', name: 'Vanilla Latte Hot HQ3', price: 5500, type: 'RECIPE' },
  { id: 32, code: 'REC-CAR-HOT-3', name: 'Caramel Macchiato Hot HQ3', price: 5800, type: 'RECIPE' },
  { id: 33, code: 'REC-AMER-ICE-3', name: 'Iced Americano HQ3', price: 5000, type: 'RECIPE' },
  { id: 34, code: 'REC-LATTE-ICE-3', name: 'Iced Cafe Latte HQ3', price: 5500, type: 'RECIPE' },
  { id: 35, code: 'REC-VAN-ICE-3', name: 'Iced Vanilla Latte HQ3', price: 6000, type: 'RECIPE' },
  { id: 36, code: 'REC-CAR-ICE-3', name: 'Iced Caramel Macchiato HQ3', price: 6300, type: 'RECIPE' },
  { id: 37, code: 'REC-MOCHA-HOT-3', name: 'Mocha Hot HQ3', price: 5800, type: 'RECIPE' },
  { id: 38, code: 'REC-MOCHA-ICE-3', name: 'Iced Mocha HQ3', price: 6300, type: 'RECIPE' },
];

// HQ 1: Finished Products with Stock (39-48, Container 1)
const PRODUCTS_HQ1 = [
  { id: 39, code: 'PRD-CROIS-1', name: 'Croissant HQ1', price: 3800, type: 'PRODUCT', stock: 100 },
  { id: 40, code: 'PRD-CHOC-1', name: 'Chocolate Croissant HQ1', price: 4200, type: 'PRODUCT', stock: 80 },
  { id: 41, code: 'PRD-MUFFIN-1', name: 'Blueberry Muffin HQ1', price: 4000, type: 'PRODUCT', stock: 90 },
  { id: 42, code: 'PRD-CHEESE-1', name: 'Cheesecake Slice HQ1', price: 6500, type: 'PRODUCT', stock: 50 },
  { id: 43, code: 'PRD-BROWN-1', name: 'Brownie HQ1', price: 4500, type: 'PRODUCT', stock: 70 },
  { id: 44, code: 'PRD-SAND-1', name: 'Club Sandwich HQ1', price: 7500, type: 'PRODUCT', stock: 60 },
  { id: 45, code: 'PRD-BAGEL-1', name: 'Plain Bagel HQ1', price: 3500, type: 'PRODUCT', stock: 100 },
  { id: 46, code: 'PRD-COOKIE-1', name: 'Chocolate Chip Cookie HQ1', price: 2500, type: 'PRODUCT', stock: 150 },
  { id: 47, code: 'PRD-SCONE-1', name: 'Plain Scone HQ1', price: 3800, type: 'PRODUCT', stock: 80 },
  { id: 48, code: 'PRD-DONUT-1', name: 'Glazed Donut HQ1', price: 3200, type: 'PRODUCT', stock: 120 },
];

// HQ 2: Finished Products with Stock (49-58, Container 6)
const PRODUCTS_HQ2 = [
  { id: 49, code: 'PRD-CROIS-2', name: 'Croissant HQ2', price: 3800, type: 'PRODUCT', stock: 100 },
  { id: 50, code: 'PRD-CHOC-2', name: 'Chocolate Croissant HQ2', price: 4200, type: 'PRODUCT', stock: 80 },
  { id: 51, code: 'PRD-MUFFIN-2', name: 'Blueberry Muffin HQ2', price: 4000, type: 'PRODUCT', stock: 90 },
  { id: 52, code: 'PRD-CHEESE-2', name: 'Cheesecake Slice HQ2', price: 6500, type: 'PRODUCT', stock: 50 },
  { id: 53, code: 'PRD-BROWN-2', name: 'Brownie HQ2', price: 4500, type: 'PRODUCT', stock: 70 },
  { id: 54, code: 'PRD-SAND-2', name: 'Club Sandwich HQ2', price: 7500, type: 'PRODUCT', stock: 60 },
  { id: 55, code: 'PRD-BAGEL-2', name: 'Plain Bagel HQ2', price: 3500, type: 'PRODUCT', stock: 100 },
  { id: 56, code: 'PRD-COOKIE-2', name: 'Chocolate Chip Cookie HQ2', price: 2500, type: 'PRODUCT', stock: 150 },
  { id: 57, code: 'PRD-SCONE-2', name: 'Plain Scone HQ2', price: 3800, type: 'PRODUCT', stock: 80 },
  { id: 58, code: 'PRD-DONUT-2', name: 'Glazed Donut HQ2', price: 3200, type: 'PRODUCT', stock: 120 },
];

// HQ 3: Finished Products with Stock (59-68, Container 11)
const PRODUCTS_HQ3 = [
  { id: 59, code: 'PRD-CROIS-3', name: 'Croissant HQ3', price: 3800, type: 'PRODUCT', stock: 100 },
  { id: 60, code: 'PRD-CHOC-3', name: 'Chocolate Croissant HQ3', price: 4200, type: 'PRODUCT', stock: 80 },
  { id: 61, code: 'PRD-MUFFIN-3', name: 'Blueberry Muffin HQ3', price: 4000, type: 'PRODUCT', stock: 90 },
  { id: 62, code: 'PRD-CHEESE-3', name: 'Cheesecake Slice HQ3', price: 6500, type: 'PRODUCT', stock: 50 },
  { id: 63, code: 'PRD-BROWN-3', name: 'Brownie HQ3', price: 4500, type: 'PRODUCT', stock: 70 },
  { id: 64, code: 'PRD-SAND-3', name: 'Club Sandwich HQ3', price: 7500, type: 'PRODUCT', stock: 60 },
  { id: 65, code: 'PRD-BAGEL-3', name: 'Plain Bagel HQ3', price: 3500, type: 'PRODUCT', stock: 100 },
  { id: 66, code: 'PRD-COOKIE-3', name: 'Chocolate Chip Cookie HQ3', price: 2500, type: 'PRODUCT', stock: 150 },
  { id: 67, code: 'PRD-SCONE-3', name: 'Plain Scone HQ3', price: 3800, type: 'PRODUCT', stock: 80 },
  { id: 68, code: 'PRD-DONUT-3', name: 'Glazed Donut HQ3', price: 3200, type: 'PRODUCT', stock: 120 },
];

// Store configurations
const STORE_NAMES = ['Gangnam Store', 'Hongdae Store', 'Itaewon Store', 'Myeongdong Store', 'Jamsil Store'];

// Generate POS configurations for all 3 HQs
const POS_CONFIGS = [];

// HQ 1: Stores 1-5, POS 1-10
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
      containerId: storeId,
      recipes: RECIPES_HQ1,
      products: PRODUCTS_HQ1
    });
  }
}

// HQ 2: Stores 6-10, POS 11-20
for (let storeIdx = 0; storeIdx < 5; storeIdx++) {
  const storeId = storeIdx + 6;
  for (let posIdx = 0; posIdx < 2; posIdx++) {
    const posId = (storeId - 1) * 2 + posIdx + 1;
    POS_CONFIGS.push({
      hqId: 2,
      hqName: 'Busan HQ',
      storeId,
      storeName: STORE_NAMES[storeIdx],
      posId,
      containerId: storeId,
      recipes: RECIPES_HQ2,
      products: PRODUCTS_HQ2
    });
  }
}

// HQ 3: Stores 11-15, POS 21-30
for (let storeIdx = 0; storeIdx < 5; storeIdx++) {
  const storeId = storeIdx + 11;
  for (let posIdx = 0; posIdx < 2; posIdx++) {
    const posId = (storeId - 1) * 2 + posIdx + 1;
    POS_CONFIGS.push({
      hqId: 3,
      hqName: 'Jeju HQ',
      storeId,
      storeName: STORE_NAMES[storeIdx],
      posId,
      containerId: storeId,
      recipes: RECIPES_HQ3,
      products: PRODUCTS_HQ3
    });
  }
}

// =========================================
// Main Test Function
// =========================================
export default function() {
  // Select random POS configuration
  const config = POS_CONFIGS[Math.floor(Math.random() * POS_CONFIGS.length)];
  const { hqId, hqName, storeId, storeName, posId, containerId, recipes, products } = config;

  // Generate unique bill number
  const billNo = `BILL-${Date.now()}-${__VU}-${__ITER}`;

  // ONLY use finished products (not recipes) since we don't have recipe compositions
  const availableProducts = products;

  // Select 1-4 random products
  const numProducts = Math.floor(Math.random() * 4) + 1;
  const selectedProducts = [];
  const usedIndices = new Set();

  for (let i = 0; i < numProducts; i++) {
    let idx;
    do {
      idx = Math.floor(Math.random() * availableProducts.length);
    } while (usedIndices.has(idx));
    usedIndices.add(idx);
    selectedProducts.push(availableProducts[idx]);
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
    'response has success': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.success === true || body.data || body.id;
      } catch (e) {
        return false;
      }
    }
  });

  if (success) {
    salesCount.add(1);
    salesAmount.add(totalAmount);
    if (__ITER % 10 === 0) {
      console.log(`✓ [${hqName}/${storeName}/POS-${posId}] ${billNo}: ₩${totalAmount.toLocaleString()} (${details.length} items)`);
    }
  } else {
    errorRate.add(1);

    // Check if it's a stock error
    if (res.body && res.body.includes('stock')) {
      stockErrors.add(1);
      console.warn(`⚠ [${hqName}/${storeName}/POS-${posId}] Stock error: ${billNo}`);
    } else {
      console.error(`✗ [${hqName}/${storeName}/POS-${posId}] Failed: ${billNo} - Status: ${res.status}`);
      if (res.body) {
        console.error(`Response: ${res.body.substring(0, 300)}`);
      }
    }
  }

  sleep(Math.random() * 2 + 0.5); // 0.5-2.5 seconds between transactions
}

// =========================================
// Summary Handler
// =========================================
export function handleSummary(data) {
  const sales = data.metrics.sales_count?.values.count || 0;
  const stockErr = data.metrics.stock_errors?.values.count || 0;
  const errors = (data.metrics.errors?.values.rate || 0) * 100;
  const avgAmount = data.metrics.sales_amount?.values.avg || 0;
  const totalAmount = (data.metrics.sales_amount?.values.count || 0) * avgAmount;
  const avgDuration = data.metrics.http_req_duration?.values.avg || 0;
  const p95Duration = data.metrics.http_req_duration?.values['p(95)'] || 0;
  const failedReqs = (data.metrics.http_req_failed?.values.rate || 0) * 100;

  console.log('\n' + '='.repeat(70));
  console.log('COMPREHENSIVE SALES TEST - RESULTS');
  console.log('='.repeat(70));
  console.log(`Total Sales Transactions: ${sales}`);
  console.log(`Total Sales Amount: ₩${totalAmount.toLocaleString()}`);
  console.log(`Average Sale Amount: ₩${avgAmount.toLocaleString()}`);
  console.log('─'.repeat(70));
  console.log(`Error Rate: ${errors.toFixed(2)}%`);
  console.log(`Stock Errors: ${stockErr}`);
  console.log(`Failed Requests: ${failedReqs.toFixed(2)}%`);
  console.log('─'.repeat(70));
  console.log(`Avg Response Time: ${avgDuration.toFixed(2)}ms`);
  console.log(`P95 Response Time: ${p95Duration.toFixed(2)}ms`);
  console.log('─'.repeat(70));
  console.log(`Test Coverage:`);
  console.log(`  - 3 HQs (Seoul, Busan, Jeju)`);
  console.log(`  - 15 Stores (5 per HQ)`);
  console.log(`  - 30 POS terminals (2 per Store)`);
  console.log(`  - 30 Finished Products (10 per HQ)`);
  console.log('='.repeat(70) + '\n');

  return {
    'stdout': '',
  };
}
