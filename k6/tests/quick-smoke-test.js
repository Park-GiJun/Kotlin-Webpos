import http from 'k6/http';
import { check, sleep } from 'k6';

// Quick smoke test - just verify the system works
export const options = {
  vus: 1,
  iterations: 10,
  thresholds: {
    http_req_duration: ['p(95)<5000'],
    http_req_failed: ['rate<0.2'],
  },
};

const POS_URL = __ENV.POS_URL || 'http://localhost:8081';

// Test with HQ 1, Store 1, POS 1
const TEST_CONFIG = {
  hqId: 1,
  hqName: 'Seoul HQ',
  storeId: 1,
  storeName: 'Gangnam Store',
  posId: 1,
};

// Simple product selection from HQ 1 (only finished products, not recipes)
const TEST_PRODUCTS = [
  { id: 39, code: 'PRD-CROIS-1', name: 'Croissant', price: 3800 },
  { id: 46, code: 'PRD-COOKIE-1', name: 'Cookie', price: 2500 },
  { id: 48, code: 'PRD-DONUT-1', name: 'Donut', price: 3200 },
];

export default function() {
  const { hqId, storeId, posId, hqName, storeName } = TEST_CONFIG;
  const billNo = `SMOKE-${Date.now()}-${__ITER}`;
  const saleDate = new Date().toISOString().slice(0, 19);

  // Pick 1-2 random products
  const numProducts = Math.floor(Math.random() * 2) + 1;
  const selectedProducts = [];
  for (let i = 0; i < numProducts; i++) {
    const product = TEST_PRODUCTS[Math.floor(Math.random() * TEST_PRODUCTS.length)];
    selectedProducts.push(product);
  }

  // Build details
  const details = [];
  let totalAmount = 0;

  selectedProducts.forEach((product, index) => {
    const qty = 1;
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

  // Sales payload
  const payload = JSON.stringify({
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

  // Make request
  const res = http.post(`${POS_URL}/pos/sales`, payload, {
    headers: { 'Content-Type': 'application/json' },
    timeout: '10s',
  });

  const success = check(res, {
    'status 200/201': (r) => r.status === 200 || r.status === 201,
    'has response': (r) => r.body && r.body.length > 0,
  });

  if (success) {
    console.log(`✓ Sale ${__ITER + 1}/10: ${billNo} - ₩${totalAmount.toLocaleString()}`);
  } else {
    console.error(`✗ Sale ${__ITER + 1}/10 FAILED: ${billNo} - Status: ${res.status}`);
    if (res.body) {
      console.error(`  Response: ${res.body.substring(0, 200)}`);
    }
  }

  sleep(0.5);
}

export function handleSummary(data) {
  const passed = data.metrics.checks?.values.passes || 0;
  const failed = data.metrics.checks?.values.fails || 0;
  const total = passed + failed;
  const successRate = total > 0 ? (passed / total * 100).toFixed(1) : 0;

  console.log('\n' + '='.repeat(50));
  console.log('SMOKE TEST RESULTS');
  console.log('='.repeat(50));
  console.log(`Total Checks: ${total}`);
  console.log(`Passed: ${passed}`);
  console.log(`Failed: ${failed}`);
  console.log(`Success Rate: ${successRate}%`);
  console.log('='.repeat(50) + '\n');

  return { 'stdout': '' };
}
