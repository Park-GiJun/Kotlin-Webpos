import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Counter, Trend } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const salesCount = new Counter('sales_count');
const salesDuration = new Trend('sales_duration');
const failedSales = new Counter('failed_sales');

// Stress test configuration - gradually increase load
export const options = {
  stages: [
    { duration: '30s', target: 10 },   // Ramp up to 10 POS
    { duration: '1m', target: 30 },    // Ramp up to 30 POS (all terminals)
    { duration: '2m', target: 50 },    // Spike to 50 concurrent transactions
    { duration: '1m', target: 100 },   // Stress test: 100 concurrent
    { duration: '1m', target: 30 },    // Ramp down
    { duration: '30s', target: 0 },    // Cool down
  ],
  thresholds: {
    http_req_duration: ['p(99)<5000'],  // 99% requests under 5s
    errors: ['rate<0.15'],               // Error rate under 15%
    sales_duration: ['p(95)<3000'],      // 95% sales under 3s
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

// All 30 POS terminals
const ALL_POS = [];
const STORE_NAMES = ['Gangnam Store', 'Hongdae Store', 'Itaewon Store', 'Myeongdong Store', 'Jamsil Store'];

// Generate all POS configurations with known products
for (let storeIdx = 0; storeIdx < 5; storeIdx++) {
  const storeId = storeIdx + 1;

  for (let posIdx = 0; posIdx < 2; posIdx++) {
    const posId = (storeId - 1) * 2 + posIdx + 1;

    ALL_POS.push({
      hqId: 1,
      hqName: 'Seoul HQ',
      storeId,
      storeName: STORE_NAMES[storeIdx],
      posId,
      products: KNOWN_PRODUCTS
    });
  }
}

export function setup() {
  console.log('========================================');
  console.log('Stress Test - Sales Load');
  console.log('========================================');
  console.log(`Configured ${ALL_POS.length} POS terminals`);
  console.log('Load stages:');
  console.log('  30s: 0 → 10 VU');
  console.log('  1m:  10 → 30 VU');
  console.log('  2m:  30 → 50 VU');
  console.log('  1m:  50 → 100 VU (STRESS)');
  console.log('  1m:  100 → 30 VU');
  console.log('  30s: 30 → 0 VU');
  console.log('========================================\n');
}

export default function() {
  const startTime = Date.now();

  // Select random POS terminal
  const config = ALL_POS[Math.floor(Math.random() * ALL_POS.length)];
  const { hqId, hqName, storeId, storeName, posId, products } = config;

  // Generate unique bill number
  const billNo = `STRESS-${Date.now()}-${__VU}-${__ITER}`;

  // Select 1-4 random products
  const numProducts = Math.floor(Math.random() * 4) + 1;
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
    const qty = Math.floor(Math.random() * 3) + 1; // 1-3 quantity
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
      timeout: '15s',
    }
  );

  const success = check(res, {
    'status is 200/201': (r) => r.status === 200 || r.status === 201,
    'response is valid': (r) => {
      try {
        const body = JSON.parse(r.body);
        return body.data || body.id;
      } catch (e) {
        return false;
      }
    },
    'response time < 10s': (r) => r.timings.duration < 10000,
  });

  if (success) {
    salesCount.add(1);
    if (__ITER % 10 === 0) {
      console.log(`✓ [${hqName}/${storeName}/POS${posId}] VU${__VU}: ${salesCount.values.count || __ITER} sales`);
    }
  } else {
    errorRate.add(1);
    failedSales.add(1);
    console.error(`✗ [${hqName}/${storeName}/POS${posId}] VU${__VU} Failed - Status: ${res.status}`);
    console.error(`Response: ${res.body.substring(0, 200)}`);
  }

  const endTime = Date.now();
  salesDuration.add(endTime - startTime);

  // Random delay between 0.5-2 seconds
  sleep(Math.random() * 1.5 + 0.5);
}

export function teardown(data) {
  console.log('\n========================================');
  console.log('Stress Test Complete');
  console.log('========================================\n');
}

export function handleSummary(data) {
  const metrics = data.metrics;

  console.log('\n' + '='.repeat(70));
  console.log('STRESS TEST RESULTS - Sales Load');
  console.log('='.repeat(70));

  console.log('\n📊 SALES METRICS');
  console.log('-'.repeat(70));
  console.log(`Total Sales:        ${metrics.sales_count?.values.count || 0}`);
  console.log(`Failed Sales:       ${metrics.failed_sales?.values.count || 0}`);
  console.log(`Success Rate:       ${((1 - (metrics.errors?.values.rate || 0)) * 100).toFixed(2)}%`);

  console.log('\n⚡ PERFORMANCE METRICS');
  console.log('-'.repeat(70));
  console.log(`Avg Sales Duration: ${metrics.sales_duration?.values.avg.toFixed(2)}ms`);
  console.log(`Min Duration:       ${metrics.sales_duration?.values.min.toFixed(2)}ms`);
  console.log(`Max Duration:       ${metrics.sales_duration?.values.max.toFixed(2)}ms`);
  console.log(`P90:                ${metrics.sales_duration?.values['p(90)'].toFixed(2)}ms`);
  console.log(`P95:                ${metrics.sales_duration?.values['p(95)'].toFixed(2)}ms`);
  console.log(`P99:                ${metrics.sales_duration?.values['p(99)'].toFixed(2)}ms`);

  console.log('\n🌐 HTTP METRICS');
  console.log('-'.repeat(70));
  console.log(`Total Requests:     ${metrics.http_reqs?.values.count || 0}`);
  console.log(`Avg Response Time:  ${metrics.http_req_duration?.values.avg.toFixed(2)}ms`);
  console.log(`P95 Response Time:  ${metrics.http_req_duration?.values['p(95)'].toFixed(2)}ms`);
  console.log(`P99 Response Time:  ${metrics.http_req_duration?.values['p(99)'].toFixed(2)}ms`);

  console.log('\n👥 CONCURRENCY');
  console.log('-'.repeat(70));
  console.log(`Peak VUs:           ${metrics.vus_max?.values.max || 0}`);

  const errorPct = (metrics.errors?.values.rate || 0) * 100;
  const status = errorPct < 15 ? '✓ PASS' : '✗ FAIL';
  console.log(`\n📈 STRESS TEST STATUS: ${status}`);
  console.log(`Error Rate:         ${errorPct.toFixed(2)}% (threshold: <15%)`);

  console.log('\n' + '='.repeat(70) + '\n');

  return {
    'stdout': '',
  };
}
